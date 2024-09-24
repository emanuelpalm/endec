package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.io.IOException;

public class EncoderGeneratorForRecord implements EncoderGenerator
{
    @Override public boolean tryGenerate(@Nonnull TypeElement element, @Nonnull ProcessorContext context)
            throws IOException
    {
        if (element.getKind() != ElementKind.RECORD) {
            return false;
        }

        var packageElement = context.getPackageOf(element);
        var simpleName = element.getSimpleName();
        var components = element.getRecordComponents();

        var builder = new StringBuilder();
        var writer = new CodeWriter(builder);

        var imports = ImportSet.createForClassInPackage(packageElement.toString());
        imports.add("java.util.Objects");
        imports.add("tech.endec.type.Encoder");
        for (var component : components) {
            imports.add(component.asType());
        }

        var root = writer.scope();
        root.line().write("package ").write(packageElement).write(';');
        root.line();
        imports.writeTo(root);
        root.line();
        root.line().write("public final class ").write(simpleName).write("Encoder");
        root.line().write('{');

        var type = root.scope();
        type.line().write("private ").write(simpleName).write("Encoder").write("() {}");
        type.line();
        type.line().write("public static void encode(").write(simpleName).write(" v, Encoder e)");
        type.line().write('{');

        var method = type.scope();
        method.line().write("Objects.requireNonNull(e);");
        method.line();
        method.line().write("if (v == null) {");

        var condition = method.scope();
        condition.line().write("e.encodeNull();");
        condition.line().write("return;");
        condition.end();

        method.line().write('}');
        method.line();
        method.line().write("var m = e.encodeMap(v, ").write(components.size()).write(");");
        method.line();

        for (var component : components) {
            var name = component.getSimpleName();
            method.line().write("m.nextKey().encodeString(\"").write(name).write("\");");

            var componentType = component.asType();
            if (componentType instanceof DeclaredType declaredType && !componentType.toString().equals("java.lang.String")) {
                method.line().write(declaredType.asElement().getSimpleName()).write("Encoder.encode(v.").write(name).write("(), m.nextValue());");
            }
            else {
                method.line().write("m.nextValue().encodeString(v.").write(name).write("());");
            }
            method.line();
        }

        method.line().write("m.end();");
        method.end();

        type.line().write('}');
        type.end();

        root.line().write('}');
        root.end();

        var sourceFile = context.createSourceFile(element.getQualifiedName() + "Encoder", element);
        try (var output = sourceFile.openOutputStream()) {
            output.write(builder.toString().getBytes());
        }

        return true;
    }
}
