package tech.endec.json.strconv;

import jakarta.annotation.Nonnull;
import tech.endec.type.ex.EncoderOutputException;

import java.io.IOException;
import java.io.OutputStream;

public class NullToJson
{
    private static final byte[] STRING_NULL = {'n', 'u', 'l', 'l'};

    private NullToJson() {}

    public static void format(@Nonnull OutputStream output)
    {
        try {
            output.write(STRING_NULL);
        } catch (IOException exception) {
            throw new EncoderOutputException(exception);
        }
    }
}
