package tech.endec.json;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import tech.endec.json.strconv.*;
import tech.endec.type.Encoder;
import tech.endec.type.EncoderOutput;
import tech.endec.type.ex.EncoderStateException;
import tech.endec.type.ex.NotEncodableException;

public class JsonEncoder implements Encoder
{
    protected final @Nonnull EncoderOutput output;

    private boolean isUsed = false;

    public JsonEncoder(@Nonnull EncoderOutput output) { this.output = output; }

    @Override public void encodeNull()
    {
        beforeEncode();
        NullToJson.format(output);
    }

    @Override public void encodeBoolean(boolean value)
    {
        beforeEncode();
        BooleanToJson.format(value, output);
    }

    @Override public void encodeLong(long value)
    {
        beforeEncode();
        LongToJson.format(value, output);
    }

    @Override public void encodeDouble(double value)
    {
        beforeEncode();
        DoubleToJson.format(value, output);
    }

    @Override public void encodeChar(char value)
    {
        beforeEncode();
        throw new NotEncodableException(value, "plain characters cannot be " +
                "represented as JSON");
    }

    @Override public void encodeString(@Nonnull String value)
    {
        beforeEncode();
        StringToJson.format(value, output);
    }

    @Override public void encodeByteArray(@Nonnull byte[] value)
    {
        beforeEncode();
        throw new NotEncodableException(value, "plain byte arrays cannot be " +
                "represented as JSON");
    }

    @Override public @Nonnull Encoder.List encodeList(@Nullable Object prototype, int size)
    {
        beforeEncode();
        output.write((byte) '[');
        return new JsonEncoderList(output, size);
    }

    @Override public @Nonnull Encoder.Map encodeMap(@Nullable Object prototype, int size)
    {
        beforeEncode();
        output.write((byte) '{');
        return new JsonEncoderMap(output, size);
    }

    protected void beforeEncode()
    {
        if (isUsed) {
            throw new EncoderStateException("encoder already used");
        }
        isUsed = true;
    }
}
