package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.lang.model.element.TypeElement;

public interface EncoderGenerator
{
    boolean tryGenerate(@Nonnull TypeElement element, @Nonnull ProcessorContext context);
}
