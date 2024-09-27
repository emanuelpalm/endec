package tech.endec.internal.audit;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

public class AuditIssue
{
    private final @Nonnull AuditCode code;

    private @Nullable Element element;
    private @Nullable AnnotationMirror annotationMirror;
    private @Nullable AnnotationValue annotationValue;
    private @Nullable Throwable throwable;

    public AuditIssue(@Nonnull AuditCode code)
    {
        this.code = code;
    }

    public @Nonnull AuditIssue element(@Nullable Element element)
    {
        this.element = element;
        return this;
    }

    public @Nonnull AuditIssue annotationMirror(@Nullable AnnotationMirror annotationMirror)
    {
        this.annotationMirror = annotationMirror;
        return this;
    }

    public @Nonnull AuditIssue annotationValue(@Nullable AnnotationValue annotationValue)
    {
        this.annotationValue = annotationValue;
        return this;
    }

    public @Nonnull AuditIssue throwable(@Nullable Throwable throwable)
    {
        this.throwable = throwable;
        return this;
    }

    void printTo(@Nonnull Messager messager)
    {
        var builder = new StringBuilder()
                .append('[').append(code.digits()).append("] ")
                .append(code.description());

        if (throwable != null) {
            builder.append(": <").append(throwable.getClass().getName())
                    .append("> ").append(throwable.getLocalizedMessage());
        }

        messager.printMessage(code.kind(), builder.toString(), element,
                annotationMirror, annotationValue);
    }
}
