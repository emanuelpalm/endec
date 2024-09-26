package tech.endec.internal;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.type.*;
import java.util.Set;
import java.util.TreeSet;

public class ImportSet
{
    private final @Nonnull String packageName;
    private final @Nonnull Set<String> qualifiedNames = new TreeSet<>();

    public static @Nonnull ImportSet createForClassInPackage(@Nonnull String packageName)
    {
        return new ImportSet(packageName);
    }

    private ImportSet(@Nonnull String packageName)
    {
        this.packageName = packageName;
    }

    public void add(@Nonnull CharSequence qualifiedName)
    {
        var n = qualifiedName.toString();

        // Classes at the root level of the java.lang package, as well as those
        // in the current package, are implicitly imported and can be ignored.
        if (isMemberOf(n, "java.lang") || isMemberOf(n, packageName)) {
            return;
        }

        qualifiedNames.add(n);
    }

    private boolean isMemberOf(@Nonnull String qualifiedName, @Nonnull String packageName)
    {
        var qualifiedNameMinLength = packageName.length() + 1;
        return qualifiedName.length() > qualifiedNameMinLength
                && qualifiedName.charAt(packageName.length()) == '.'
                && qualifiedName.startsWith(packageName)
                && qualifiedName.indexOf('.', qualifiedNameMinLength) == -1;
    }

    public void add(@Nullable TypeMirror type)
    {
        switch (type) {
            case ArrayType t -> add(t.getComponentType());
            case DeclaredType t -> {
                var enclosingType = t.getEnclosingType();
                if (enclosingType == null) {
                    if (t.asElement() instanceof QualifiedNameable nameable) {
                        add(nameable.getQualifiedName());
                    }
                }
                else {
                    add(enclosingType);
                }
                for (var typeArgument : t.getTypeArguments()) {
                    add(typeArgument);
                }
            }
            case ExecutableType t -> {
                for (var parameterType : t.getParameterTypes()) {
                    add(parameterType);
                }
                add(t.getReturnType());
                add(t.getReceiverType());
                for (var thrownType : t.getThrownTypes()) {
                    add(thrownType);
                }
                for (var typeVariable : t.getTypeVariables()) {
                    add(typeVariable);
                }
            }
            case IntersectionType t -> {
                for (var bound : t.getBounds()) {
                    add(bound);
                }
            }
            case NoType _, NullType _ -> {}
            case null -> {}
            case PrimitiveType _ -> {}
            case TypeVariable t -> {
                add(t.getLowerBound());
                add(t.getUpperBound());
            }
            case UnionType t -> {
                for (var alternative : t.getAlternatives()) {
                    add(alternative);
                }
            }
            case WildcardType t -> {
                add(t.getExtendsBound());
                add(t.getSuperBound());
            }
            default -> throw new UnsupportedOperationException("Import " +
                    "resolution is not implemented for the type " + type);
        }
    }

    public void writeTo(@Nonnull CodeWriter.Scope scope)
    {
        for (var qualifiedName : qualifiedNames) {
            scope.line().write("import ").write(qualifiedName).write(';');
        }
    }
}
