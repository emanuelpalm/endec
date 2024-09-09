package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.json.strconv.*;
import tech.endec.type.Encoder;
import tech.endec.type.EncoderOutput;
import tech.endec.type.ex.EncoderStateException;
import tech.endec.type.ex.NotEncodableException;

public abstract class JsonEncoder implements Encoder
{
    private boolean isAwaitingConsumer = false;

    public static @Nonnull JsonEncoder from(@Nonnull EncoderOutput output)
    {
        return new JsonRootEncoder(output);
    }

    protected abstract EncoderOutput getOutput();

    @Override public void encodeNull()
    {
        onEncode();
        throwIfAwaitingConsumer();
        NullToJson.format(getOutput());
    }

    @Override public void encodeBoolean(boolean value)
    {
        onEncode();
        throwIfAwaitingConsumer();
        BooleanToJson.format(value, getOutput());
    }

    @Override public void encodeLong(long value)
    {
        onEncode();
        throwIfAwaitingConsumer();
        LongToJson.format(value, getOutput());
    }

    @Override public void encodeDouble(double value)
    {
        onEncode();
        throwIfAwaitingConsumer();
        DoubleToJson.format(value, getOutput());
    }

    @Override public void encodeChar(char value)
    {
        onEncode();
        throwIfAwaitingConsumer();
        throw new NotEncodableException(value, "plain characters cannot be represented as JSON");
    }

    @Override public void encodeString(@Nonnull String value)
    {
        onEncode();
        throwIfAwaitingConsumer();
        StringToJson.format(value, getOutput());
    }

    @Override public void encodeByteArray(@Nonnull byte[] value)
    {
        onEncode();
        throwIfAwaitingConsumer();
        throw new NotEncodableException(value, "plain byte arrays cannot be represented as JSON");
    }

    @Override public void encodeList(int size, @Nonnull Consumer consumer)
    {
        onEncode();

        throwIfAwaitingConsumer();
        isAwaitingConsumer = true;

        var output = getOutput();
        output.write((byte) '[');

        var encoder = new JsonListEncoder(output, size);
        consumer.encode(encoder);
        encoder.end();

        output.write((byte) ']');

        isAwaitingConsumer = false;
    }

    @Override public void encodeMap(int size, @Nonnull Consumer consumer)
    {
        onEncode();

        throwIfAwaitingConsumer();
        isAwaitingConsumer = true;

        var output = getOutput();
        output.write((byte) '{');

        var encoder = new JsonMapEncoder(output, size);
        consumer.encode(encoder);
        encoder.end();

        output.write((byte) '}');

        isAwaitingConsumer = false;
    }

    protected abstract void onEncode();

    private void throwIfAwaitingConsumer()
    {
        if (isAwaitingConsumer) {
            throw new EncoderStateException("waiting for another encoding to finish");
        }
    }
}
