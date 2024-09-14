package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;

public record ProcessorContext(
        @Nonnull Filer filer,
        @Nonnull Messager messager,
        @Nonnull Elements elementUtils)
{}
