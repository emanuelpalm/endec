package tech.endec.json;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import tech.endec.json.strconv.*;
import tech.endec.type.Encoder;
import tech.endec.type.EncoderOutput;
import tech.endec.type.ex.EncoderStateException;

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

    @Override public void encodeByte(byte value)
    {
        beforeEncode();
        ByteToJson.format(value, output);
    }

    @Override public void encodeShort(short value)
    {
        beforeEncode();
        LongToJson.format(value, output);
    }

    @Override public void encodeInt(int value)
    {
        beforeEncode();
        LongToJson.format(value, output);
    }

    @Override public void encodeLong(long value)
    {
        beforeEncode();
        LongToJson.format(value, output);
    }

    @Override public void encodeFloat(float value)
    {
        beforeEncode();
        DoubleToJson.format(value, output);
    }

    @Override public void encodeDouble(double value)
    {
        beforeEncode();
        DoubleToJson.format(value, output);
    }

    @Override public void encodeChar(char value)
    {
        beforeEncode();
        CharSequenceToJson.format(String.valueOf(value), output);
    }

    @Override public void encodeCharSequence(@Nonnull CharSequence value)
    {
        beforeEncode();
        CharSequenceToJson.format(value, output);
    }

    @Override public void encodeByteArray(@Nonnull byte[] value)
    {
        var list = encodeList(null, value.length);
        for (var b : value) {
            list.next().encodeByte(b);
        }
        list.end();
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
