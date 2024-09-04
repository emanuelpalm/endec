package tech.endec.ex;

import jakarta.annotation.Nullable;

public class EncoderException extends RuntimeException
{
    public EncoderException(@Nullable String message)
    {
        super(message);
    }

    public EncoderException(@Nullable String message, @Nullable Throwable cause)
    {
        super(message, cause);
    }

    public EncoderException(@Nullable Throwable cause)
    {
        super(cause);
    }
}
