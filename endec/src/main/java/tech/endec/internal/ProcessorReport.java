package tech.endec.internal;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

public class ProcessorReport
{
    private final @Nonnull Messager messager;
    private final @Nonnull ProcessorIssue issue;

    private Element element;
    private AnnotationMirror annotationMirror;
    private AnnotationValue annotationValue;
    private Throwable throwable;

    public ProcessorReport(@Nonnull Messager messager, @Nonnull ProcessorIssue issue)
    {
        this.messager = messager;
        this.issue = issue;
    }

    public @Nonnull ProcessorReport element(@Nullable Element element)
    {
        this.element = element;
        return this;
    }

    public @Nonnull ProcessorReport annotationMirror(@Nullable AnnotationMirror annotationMirror)
    {
        this.annotationMirror = annotationMirror;
        return this;
    }

    public @Nonnull ProcessorReport annotationValue(@Nullable AnnotationValue annotationValue)
    {
        this.annotationValue = annotationValue;
        return this;
    }

    public @Nonnull ProcessorReport throwable(@Nullable Throwable throwable)
    {
        this.throwable = throwable;
        return this;
    }

    public void submit()
    {
        var builder = new StringBuilder()
                .append('[').append(issue.code()).append("] ")
                .append(issue.description());

        if (throwable != null) {
            builder.append(": <").append(throwable.getClass().getName())
                    .append("> ").append(throwable.getLocalizedMessage());
        }

        messager.printMessage(issue.kind(), builder.toString(), element,
                annotationMirror, annotationValue);
    }
}
