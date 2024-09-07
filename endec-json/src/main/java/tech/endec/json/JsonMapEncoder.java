package tech.endec.json;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import tech.endec.type.ListEncoder;
import tech.endec.type.MapEncoder;
import tech.endec.type.ex.NotEncodableException;

import java.io.IOException;
import java.io.OutputStream;

class JsonMapEncoder extends JsonBaseEncoder implements MapEncoder
{
    private final @Nonnull JsonBaseEncoder parent;

    private boolean isAtValue = false;
    private boolean isNotEmpty = false;

    JsonMapEncoder(@Nonnull OutputStream output, @Nonnull JsonBaseEncoder parent)
    {
        super(output);
        this.parent = parent;
    }

    @Override public void encodeNull() throws IOException
    {
        writeColonIfAtValueOrThrow(null, "the null value cannot be used as a key in JSON");
        super.encodeNull();
    }

    @Override public void encodeBoolean(boolean value) throws IOException
    {
        writeColonIfAtValueOrThrow(value, "booleans cannot be used as keys in JSON");
        super.encodeBoolean(value);
    }

    @Override public void encodeLong(long value) throws IOException
    {
        writeColonIfAtValueOrThrow(value, "integers cannot be used as keys in JSON");
        super.encodeLong(value);
    }

    @Override public void encodeDouble(double value) throws IOException
    {
        writeColonIfAtValueOrThrow(value, "floats cannot be used as keys in JSON");
        super.encodeDouble(value);
    }

    @Override public void encodeString(@Nonnull String value) throws IOException
    {
        writeColonIfAtValueOrCommaIfNotEmpty();
        super.encodeString(value);
    }

    @Nonnull @Override public ListEncoder encodeList() throws IOException
    {
        writeColonIfAtValueOrThrow(null, "lists cannot be used as keys in JSON");
        return super.encodeList();
    }

    @Nonnull @Override public MapEncoder encodeMap() throws IOException
    {
        writeColonIfAtValueOrThrow(null, "maps cannot be used as keys in JSON");
        return super.encodeMap();
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
        super.onEncode();
        output.write((byte) '}');
        parent.onChildClose();
    }
}
