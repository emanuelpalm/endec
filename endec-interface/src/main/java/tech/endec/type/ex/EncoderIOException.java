package tech.endec.type.ex;

import java.io.IOException;

public class EncoderIOException extends EncoderException
{
    public EncoderIOException(IOException cause)
    {
        super(cause);
    }
}
