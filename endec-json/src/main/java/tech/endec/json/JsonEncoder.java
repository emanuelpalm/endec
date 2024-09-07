package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.json.strconv.*;
import tech.endec.type.Encoder;
import tech.endec.type.ListEncoder;
import tech.endec.type.MapEncoder;
import tech.endec.type.ex.NotEncodableException;

import java.io.IOException;
import java.io.OutputStream;

public class JsonEncoder implements Encoder
{
    private final @Nonnull OutputStream output;

    public JsonEncoder(@Nonnull OutputStream output) { this.output = output; }

    @Override public void encodeNull() throws IOException
    {
        NullToJson.format(output);
    }

    @Override public void encodeBoolean(boolean value) throws IOException
    {
        BooleanToJson.format(value, output);
    }

    @Override public void encodeLong(long value) throws IOException
    {
        LongToJson.format(value, output);
    }

    @Override public void encodeDouble(double value) throws IOException
    {
        DoubleToJson.format(value, output);
    }

    @Override public void encodeChar(char value)
    {
        throw new NotEncodableException(value, "plain characters cannot be represented as JSON");
    }

    @Override public void encodeString(@Nonnull String value) throws IOException
    {
        StringToJson.format(value, output);
    }

    @Override public void encodeByteArray(@Nonnull byte[] value)
    {
        throw new NotEncodableException(value, "plain byte arrays cannot be represented as JSON");
    }

    @Nonnull @Override public ListEncoder encodeList() throws IOException
    {
        output.write((byte) '[');
        return new JsonListEncoder(output);
    }

    @Nonnull @Override public MapEncoder encodeMap() throws IOException
    {
        output.write((byte) '{');
        return new JsonMapEncoder(output);
    }
}
