package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.json.strconv.*;
import tech.endec.type.Encoder;
import tech.endec.type.ListEncoder;
import tech.endec.type.MapEncoder;
import tech.endec.type.ex.NotEncodableException;

import java.io.IOException;
import java.io.OutputStream;

class JsonBaseEncoder implements Encoder
{
    protected final @Nonnull OutputStream output;

    private boolean hasOpenChild;

    public JsonBaseEncoder(@Nonnull OutputStream output) { this.output = output; }

    @Override public void encodeNull() throws IOException
    {
        onEncode();
        NullToJson.format(output);
    }

    @Override public void encodeBoolean(boolean value) throws IOException
    {
        onEncode();
        BooleanToJson.format(value, output);
    }

    @Override public void encodeLong(long value) throws IOException
    {
        onEncode();
        LongToJson.format(value, output);
    }

    @Override public void encodeDouble(double value) throws IOException
    {
        onEncode();
        DoubleToJson.format(value, output);
    }

    @Override public void encodeChar(char value) throws IOException
    {
        onEncode();
        throw new NotEncodableException(value, "plain characters cannot be represented as JSON");
    }

    @Override public void encodeString(@Nonnull String value) throws IOException
    {
        onEncode();
        StringToJson.format(value, output);
    }

    @Override public void encodeByteArray(@Nonnull byte[] value) throws IOException
    {
        onEncode();
        throw new NotEncodableException(value, "plain byte arrays cannot be represented as JSON");
    }

    @Override public @Nonnull ListEncoder encodeList() throws IOException
    {
        onEncode();
        hasOpenChild = true;
        output.write((byte) '[');
        return new JsonListEncoder(output, this);
    }

    @Override public @Nonnull MapEncoder encodeMap() throws IOException
    {
        onEncode();
        hasOpenChild = true;
        output.write((byte) '{');
        return new JsonMapEncoder(output, this);
    }

    protected void onChildClose() {
        if (!hasOpenChild) {
            throw new IllegalStateException("no child encoder is open");
        }
        hasOpenChild = false;
    }

    protected void onEncode() throws IOException {
        if (hasOpenChild) {
            throw new IllegalStateException("child encoder not yet closed");
        }
    }
}
