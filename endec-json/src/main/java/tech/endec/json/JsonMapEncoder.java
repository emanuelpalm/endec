package tech.endec.json;

import jakarta.annotation.Nonnull;
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
        writeColonIfAtValueOrThrow("The null value cannot be used as a key in JSON");
        NullToJson.format(output);
    }

    @Override public void encodeBoolean(boolean value) throws IOException
    {
        writeColonIfAtValueOrThrow("Booleans cannot be used as keys in JSON");
        BooleanToJson.format(value, output);
    }

    @Override public void encodeLong(long value) throws IOException
    {
        writeColonIfAtValueOrThrow("Integers cannot be used as keys in JSON");
        LongToJson.format(value, output);
    }

    @Override public void encodeDouble(double value) throws IOException
    {
        writeColonIfAtValueOrThrow("Floats cannot be used as keys in JSON");
        DoubleToJson.format(value, output);
    }

    @Override public void encodeChar(char value)
    {
        throw new NotEncodableException("Plain characters cannot be represented as JSON");
    }

    @Override public void encodeString(@Nonnull String value) throws IOException
    {
        writeColonIfAtValueOrCommaIfNotEmpty();
        StringToJson.format(value, output);
    }

    @Override public void encodeByteArray(@Nonnull byte[] value)
    {
        throw new NotEncodableException("Plain byte arrays cannot be represented as JSON");
    }

    @Nonnull @Override public ListEncoder encodeList() throws IOException
    {
        writeColonIfAtValueOrThrow("Lists cannot be used as keys in JSON");
        output.write((byte) '[');
        return new JsonListEncoder(output);
    }

    @Nonnull @Override public MapEncoder encodeMap() throws IOException
    {
        writeColonIfAtValueOrThrow("Maps cannot be used as keys in JSON");
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

    private void writeColonIfAtValueOrThrow(@Nonnull String message) throws IOException
    {
        if (isAtValue) {
            output.write((byte) ':');
        } else {
            throw new NotEncodableException(message);
        }
    }

    @Override public void close() throws IOException
    {
        output.write((byte) '}');
    }
}
