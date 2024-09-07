package tech.endec.json;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import tech.endec.json.strconv.*;
import tech.endec.type.ListEncoder;
import tech.endec.type.MapEncoder;
import tech.endec.type.ex.NotEncodableException;

import java.io.IOException;
import java.io.OutputStream;

class JsonMapEncoder implements MapEncoder
{
    private final @Nonnull OutputStream output;

    private boolean isAtValue = false;
    private boolean isNotEmpty = false;

    JsonMapEncoder(@Nonnull OutputStream output) { this.output = output; }

    @Override public void encodeNull() throws IOException
    {
        writeColonIfAtValueOrThrow(null, "the null value cannot be used as a key in JSON");
        NullToJson.format(output);
    }

    @Override public void encodeBoolean(boolean value) throws IOException
    {
        writeColonIfAtValueOrThrow(value, "booleans cannot be used as keys in JSON");
        BooleanToJson.format(value, output);
    }

    @Override public void encodeLong(long value) throws IOException
    {
        writeColonIfAtValueOrThrow(value, "integers cannot be used as keys in JSON");
        LongToJson.format(value, output);
    }

    @Override public void encodeDouble(double value) throws IOException
    {
        writeColonIfAtValueOrThrow(value, "floats cannot be used as keys in JSON");
        DoubleToJson.format(value, output);
    }

    @Override public void encodeChar(char value)
    {
        throw new NotEncodableException(value, "plain characters cannot be represented as JSON");
    }

    @Override public void encodeString(@Nonnull String value) throws IOException
    {
        writeColonIfAtValueOrCommaIfNotEmpty();
        StringToJson.format(value, output);
    }

    @Override public void encodeByteArray(@Nonnull byte[] value)
    {
        throw new NotEncodableException(value, "plain byte arrays cannot be represented as JSON");
    }

    @Nonnull @Override public ListEncoder encodeList() throws IOException
    {
        writeColonIfAtValueOrThrow(null, "lists cannot be used as keys in JSON");
        output.write((byte) '[');
        return new JsonListEncoder(output);
    }

    @Nonnull @Override public MapEncoder encodeMap() throws IOException
    {
        writeColonIfAtValueOrThrow(null, "maps cannot be used as keys in JSON");
        output.write((byte) '{');
        return new JsonMapEncoder(output);
    }

    private void writeColonIfAtValueOrCommaIfNotEmpty() throws IOException
    {
        if (isAtValue) {
            isAtValue = false;
            output.write((byte) ':');
        } else {
            isAtValue = true;
            if (isNotEmpty) {
                output.write((byte) ',');
            } else {
                isNotEmpty = true;
            }
        }
    }

    private void writeColonIfAtValueOrThrow(@Nullable Object value, @Nonnull String message) throws IOException
    {
        if (isAtValue) {
            output.write((byte) ':');
        } else {
            throw new NotEncodableException(value, message);
        }
    }

    @Override public void close() throws IOException
    {
        output.write((byte) '}');
    }
}
