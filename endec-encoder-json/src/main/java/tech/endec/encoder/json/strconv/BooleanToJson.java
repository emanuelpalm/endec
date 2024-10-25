package tech.endec.encoder.json.strconv;

import jakarta.annotation.Nonnull;
import tech.endec.encoder.EncoderOutput;

public final class BooleanToJson
{
    private static final byte[] STRING_FALSE = {'f', 'a', 'l', 's', 'e'};
    private static final byte[] STRING_TRUE = {'t', 'r', 'u', 'e'};

    private BooleanToJson() {}

    public static void format(boolean value, @Nonnull EncoderOutput output)
    {
        output.write(value ? STRING_TRUE : STRING_FALSE);
    }
}
