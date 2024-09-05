package tech.endec.json.strconv;

import jakarta.annotation.Nonnull;

import java.io.IOException;
import java.io.OutputStream;

public class NullToJson
{
    private static final byte[] STRING_NULL = {'n', 'u', 'l', 'l'};

    private NullToJson() { }

    public static void format(@Nonnull OutputStream output) throws IOException
    {
        output.write(STRING_NULL);
    }
}
