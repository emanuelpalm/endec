package tech.endec.annotation.internal.generator;

import jakarta.annotation.Nonnull;
import tech.endec.annotation.internal.audit.Audit;
import tech.endec.annotation.internal.source.SourceFile;
import tech.endec.annotation.internal.source.SourceWriter;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public record EncoderGenerator(
        @Nonnull Audit audit,
        @Nonnull Elements elementUtils,
        @Nonnull Types typeUtils)
{
    public @Nonnull SourceFile generate(@Nonnull TypeElement element)
    {
        return generate(element, new SourceWriter());
    }

    private @Nonnull SourceFile generate(@Nonnull TypeElement element, @Nonnull SourceWriter writer)
    {
        if (element.getKind() != ElementKind.RECORD) {
            throw new UnsupportedOperationException("TODO: Only records are supported");
        }

        var packageElement = elementUtils.getPackageOf(element);
        var simpleName = element.getSimpleName();
        var components = element.getRecordComponents();

        var imports = ImportSet.createForClassInPackage(packageElement.toString());
        imports.add("tech.endec.type.Encoder");
        for (var component : components) {
            imports.add(component.getAccessor().getReturnType());
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
        method.line().write("if (e == null) {");

        var ifEIsNull = method.scope();
        ifEIsNull.line().write("throw new NullPointerException();");
        ifEIsNull.end();

        method.line().write('}');
        method.line().write("if (v == null) {");

        var ifVIsNull = method.scope();
        ifVIsNull.line().write("e.encodeNull();");
        ifVIsNull.line().write("return;");
        ifVIsNull.end();

        method.line().write('}');
        method.line();
        method.line().write("var m = e.encodeMap(v, ").write(components.size()).write(");");

        for (int i = 0; i < components.size(); i++) {
            var component = components.get(i);
            var name = component.getSimpleName();
            var expression = "m.next(\"" + name + "\", " + i + ")";
            EncoderWriting.write(expression, "v", component, method);
        }

        method.line().write("m.end();");
        method.end();

        type.line().write('}');
        type.end();

        root.line().write('}');
        root.end();

        return writer.toFile(element.getQualifiedName() + "Encoder");
    }
}
