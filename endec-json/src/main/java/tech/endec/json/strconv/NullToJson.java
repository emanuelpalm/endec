package tech.endec.json.strconv;

import jakarta.annotation.Nonnull;
import tech.endec.type.EncoderOutput;

public final class NullToJson
{
    private static final byte[] STRING_NULL = {'n', 'u', 'l', 'l'};

    private NullToJson() {}

    public static void format(@Nonnull EncoderOutput output)
    {
        output.write(STRING_NULL);
    }
}
