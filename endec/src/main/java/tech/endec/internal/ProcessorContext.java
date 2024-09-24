package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Objects;

public record ProcessorContext(
        @Nonnull Filer filer,
        @Nonnull Messager messager,
        @Nonnull Elements elementUtils)
{
    public @Nonnull ProcessorReport createReport(@Nonnull ProcessorIssue issue)
    {
        return new ProcessorReport(messager, issue);
    }

    public @Nonnull JavaFileObject createSourceFile(@Nonnull CharSequence name, @Nonnull Element... originatingElements)
            throws IOException
    {
        return filer.createSourceFile(name, originatingElements);
    }

    public @Nonnull PackageElement getPackageOf(@Nonnull Element element)
    {
        return Objects.requireNonNull(elementUtils.getPackageOf(element));
    }
}
