package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.tools.Diagnostic;

import static javax.tools.Diagnostic.Kind.ERROR;

public record ProcessorIssue(
        @Nonnull String code,
        @Nonnull Diagnostic.Kind kind,
        @Nonnull String description)
{
    public static final @Nonnull ProcessorIssue ENCODER_GENERATOR_EXCEPTION_CAUGHT =
            new ProcessorIssue("E0001", ERROR, "Exception caught while " +
                    "generating encoder");

    public static final @Nonnull ProcessorIssue ENCODER_GENERATOR_UNAVAILABLE_FOR_TYPE =
            new ProcessorIssue("E0002", ERROR, "No encoder generator is " +
                    "available for this kind of class or interface");
}
