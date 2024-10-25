package tech.endec.internal.generator;

import jakarta.annotation.Nonnull;
import tech.endec.internal.source.SourceWriter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;

public final class EncoderWriting
{
    private EncoderWriting() {}

    public static void write(
            @Nonnull String encoderExpression,
            @Nonnull String memberExpression,
            @Nonnull Element element,
            @Nonnull SourceWriter.Scope scope)
    {
        var type = element.asType();
        switch (type) {
            case ArrayType t -> write(encoderExpression, memberExpression, element, t, scope);
            case DeclaredType t -> write(encoderExpression, memberExpression, element, t, scope);
            case PrimitiveType t -> write(encoderExpression, memberExpression, element, t, scope);
            default -> throw new UnsupportedOperationException("Import " +
                    "resolution is not implemented for the type " + type);
        }
    }

    private static void write(
            @Nonnull String encoderExpression,
            @Nonnull String memberExpression,
            @Nonnull Element element,
            @Nonnull ArrayType type,
            @Nonnull SourceWriter.Scope scope)
    {
        throw new UnsupportedOperationException("TODO");
    }

    private static void write(
            @Nonnull String encoderExpression,
            @Nonnull String memberExpression,
            @Nonnull Element element,
            @Nonnull DeclaredType type,
            @Nonnull SourceWriter.Scope scope)
    {
        var line = scope.line();
        if (type.toString().equals("java.lang.String")) {
            line.write(encoderExpression).write(".encodeCharSequence(");
            write(memberExpression, element, line);
            line.write(");");
        }
        else {
            line.write(type.asElement().getSimpleName()).write("Encoder.encode(");
            write(memberExpression, element, line);
            line.write(", ").write(encoderExpression).write(");");
        }
    }

    private static void write(
            @Nonnull String encoderExpression,
            @Nonnull String memberExpression,
            @Nonnull Element element,
            @Nonnull PrimitiveType type,
            @Nonnull SourceWriter.Scope scope)
    {
        var line = scope.line();

        line.write(encoderExpression).write(".encode").write(switch (type.getKind()) {
            case BOOLEAN -> "Boolean";
            case BYTE -> "Byte";
            case SHORT -> "Short";
            case INT -> "Int";
            case LONG -> "Long";
            case CHAR -> "Char";
            case FLOAT -> "Float";
            case DOUBLE -> "Double";
            default -> throw new UnsupportedOperationException("Unsupported primitive type " + type);
        }).write('(');

        write(memberExpression, element, line);

        line.write(");");
    }

    private static void write(
            @Nonnull String memberExpression,
            @Nonnull Element element,
            @Nonnull SourceWriter.Line line)
    {
        switch (element) {
            case ExecutableElement e -> write(memberExpression, e, line);
            case RecordComponentElement e -> write(memberExpression, e.getAccessor(), line);
            case VariableElement e -> write(memberExpression, e, line);
            default -> throw new UnsupportedOperationException("Unsupported element type " + element);
        }
    }

    private static void write(
            @Nonnull String memberExpression,
            @Nonnull ExecutableElement element,
            @Nonnull SourceWriter.Line line)
    {
        var receiverType = element.getReceiverType();
        if (receiverType != null && receiverType.getKind() != TypeKind.NONE) {
            throw new UnsupportedOperationException("TODO");
        }
        if (!element.getParameters().isEmpty()) {
            throw new UnsupportedOperationException("TODO");
        }
        if (!element.getThrownTypes().isEmpty()) {
            throw new UnsupportedOperationException("TODO");
        }
        line.write(memberExpression).write('.').write(element.getSimpleName()).write("()");
    }

    private static void write(
            @Nonnull String memberExpression,
            @Nonnull VariableElement element,
            @Nonnull SourceWriter.Line line)
    {
        if (element.isUnnamed()) {
            throw new UnsupportedOperationException("TODO");
        }
        if (element.getConstantValue() != null) {
            throw new UnsupportedOperationException("TODO");
        }
        line.write(memberExpression).write('.').write(element.getSimpleName());
    }
}
