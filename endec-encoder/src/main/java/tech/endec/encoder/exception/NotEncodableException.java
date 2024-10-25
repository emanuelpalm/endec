package tech.endec.encoder.exception;

import jakarta.annotation.Nullable;

public class NotEncodableException extends EncoderException
{
    private final @Nullable Object unencodable;

    public NotEncodableException(@Nullable Object unencodable, @Nullable String message)
    {
        super(message);
        this.unencodable = unencodable;
    }

    @Override public String getMessage()
    {
        if (unencodable == null) {
            return super.getMessage();
        }

        var builder = new StringBuilder("failed to encode ").append(unencodable);
        var message = super.getMessage();
        if (message != null) {
            builder.append("; ").append(message);
        }
        return builder.toString();
    }

    public @Nullable Object getUnencodable()
    {
        return unencodable;
    }
}
