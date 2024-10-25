package tech.endec.annotation.internal.source;

import jakarta.annotation.Nonnull;

public record SourceFile(
        @Nonnull String name,
        @Nonnull String body)
{}
