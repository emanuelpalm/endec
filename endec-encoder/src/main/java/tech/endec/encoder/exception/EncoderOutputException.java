package tech.endec.encoder.exception;

import jakarta.annotation.Nullable;

import java.io.IOException;

public class EncoderOutputException extends EncoderException
{
    public EncoderOutputException(@Nullable IOException cause)
    {
        super(cause);
    }

    @Override public synchronized IOException getCause()
    {
        return (IOException) super.getCause();
    }
}
