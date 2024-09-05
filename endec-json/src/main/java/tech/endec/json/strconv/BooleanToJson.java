package tech.endec.json.strconv;

import jakarta.annotation.Nonnull;

import java.io.IOException;
import java.io.OutputStream;

public final class BooleanToJson
{
    private static final byte[] STRING_FALSE = {'f', 'a', 'l', 's', 'e'};
    private static final byte[] STRING_TRUE = {'t', 'r', 'u', 'e'};

    private BooleanToJson() { }

    public static void format(boolean value, @Nonnull OutputStream output) throws IOException
    {
        output.write(value ? STRING_TRUE : STRING_FALSE);
    }
}
