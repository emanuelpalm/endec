package tech.endec.internal;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public record ProcessorContext(
        @Nonnull Filer filer,
        @Nonnull Messager messager,
        @Nonnull Elements elementUtils)
{
    public JavaFileObject createSourceFile(@Nonnull CharSequence name, @Nonnull Element... originatingElements)
            throws IOException
    {
        return filer.createSourceFile(name, originatingElements);
    }

    public @Nonnull PackageElement getPackageOf(@Nonnull TypeElement element)
    {
        return Objects.requireNonNull(elementUtils.getPackageOf(element));
    }

    public void printError(@Nonnull String message, @Nullable Element element)
    {
        messager.printError(message, element);
    }

    public void printError(@Nonnull String message, @Nullable Element element, @Nonnull Throwable throwable)
    {
        var buffer = new ByteArrayOutputStream(8192);
        var output = new PrintWriter(buffer);
        output.printf("%s%n", message);
        throwable.printStackTrace(output);
        messager.printError(buffer.toString(), element);
    }
}
