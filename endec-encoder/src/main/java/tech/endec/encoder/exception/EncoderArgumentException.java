package tech.endec.encoder.exception;

import jakarta.annotation.Nullable;

public class EncoderArgumentException extends EncoderException
{
    public EncoderArgumentException(@Nullable String message)
    {
        super(message);
    }
}
