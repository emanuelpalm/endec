package tech.endec.annotation.internal.audit;

import jakarta.annotation.Nonnull;

import javax.tools.Diagnostic;

import static javax.tools.Diagnostic.Kind.ERROR;

public record AuditCode(
        @Nonnull String digits,
        @Nonnull Diagnostic.Kind kind,
        @Nonnull String description)
{
    public static final @Nonnull AuditCode ENCODER_GENERATOR_EXCEPTION_CAUGHT =
            new AuditCode("E0001", ERROR, "Exception caught while " +
                    "generating encoder");

    public static final @Nonnull AuditCode ENCODER_GENERATOR_UNAVAILABLE_FOR_TYPE =
            new AuditCode("E0002", ERROR, "No encoder generator is " +
                    "available for this kind of class or interface");

    public @Nonnull AuditIssue toIssue() {
        return new AuditIssue(this);
    }
}
