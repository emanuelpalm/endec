package tech.endec.encoder.exception;

import jakarta.annotation.Nullable;

public class EncoderStateException extends EncoderException
{
    public EncoderStateException(@Nullable String message)
    {
        super(message);
    }
}
