package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.json.strconv.*;
import tech.endec.type.Encoder;
import tech.endec.type.EncoderOutput;
import tech.endec.type.ex.EncoderStateException;
import tech.endec.type.ex.NotEncodableException;

public abstract class JsonEncoder implements Encoder
{
    JsonEncoder() {}

    public static @Nonnull JsonEncoder from(@Nonnull EncoderOutput output)
    {
        return new JsonEncoder()
        {
            private boolean isUsed = false;

            @Override protected void beforeEncode()
            {
                if (isUsed) {
                    throw new EncoderStateException("encoder already used");
                }
                isUsed = true;
            }

            @Override protected @Nonnull EncoderOutput getOutput()
            {
                return output;
            }
        };
    }

    @Override public void encodeNull()
    {
        beforeEncode();
        NullToJson.format(getOutput());
    }

    @Override public void encodeBoolean(boolean value)
    {
        beforeEncode();
        BooleanToJson.format(value, getOutput());
    }

    @Override public void encodeLong(long value)
    {
        beforeEncode();
        LongToJson.format(value, getOutput());
    }

    @Override public void encodeDouble(double value)
    {
        beforeEncode();
        DoubleToJson.format(value, getOutput());
    }

    @Override public void encodeChar(char value)
    {
        beforeEncode();
        throw new NotEncodableException(value, "plain characters cannot be represented as JSON");
    }

    @Override public void encodeString(@Nonnull String value)
    {
        beforeEncode();
        StringToJson.format(value, getOutput());
    }

    @Override public void encodeByteArray(@Nonnull byte[] value)
    {
        beforeEncode();
        throw new NotEncodableException(value, "plain byte arrays cannot be represented as JSON");
    }

    @Override public @Nonnull Encoder.List encodeList(int size)
    {
        beforeEncode();

        var output = getOutput();
        output.write((byte) '[');

        return new JsonEncoderList(output, size);
    }

    @Override public @Nonnull Encoder.Map encodeMap(int size)
    {
        beforeEncode();

        var output = getOutput();
        output.write((byte) '{');

        return new JsonEncoderMap(output, size);
    }

    protected abstract void beforeEncode();

    protected abstract @Nonnull EncoderOutput getOutput();
}
