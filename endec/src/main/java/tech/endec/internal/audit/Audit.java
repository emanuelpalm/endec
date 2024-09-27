package tech.endec.internal.audit;

import jakarta.annotation.Nonnull;

import javax.annotation.processing.Messager;

public class Audit
{
    private final @Nonnull Messager messager;

    public Audit(@Nonnull Messager messager) { this.messager = messager; }

    public void report(@Nonnull AuditIssue issue) {
        issue.printTo(messager);
    }
}
