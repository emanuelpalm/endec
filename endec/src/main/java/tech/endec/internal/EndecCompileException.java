package tech.endec.internal;

public class EndecCompileException extends RuntimeException
{
    public EndecCompileException(String message)
    {
        super(message);
    }

    public EndecCompileException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
