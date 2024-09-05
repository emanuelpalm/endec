package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.json.strconv.*;
import tech.endec.type.Encoder;
import tech.endec.type.ListEncoder;
import tech.endec.type.MapEncoder;
import tech.endec.type.ex.EncoderIOException;
import tech.endec.type.ex.NotEncodableException;

import java.io.IOException;
import java.io.OutputStream;

public class JsonEncoder implements Encoder, ListEncoder, MapEncoder
{
    private static final byte STATE_FLAG_IN_LIST = 0b0000_0001;
    private static final byte STATE_FLAG_IN_MAP = 0b0000_0010;
    private static final byte STATE_FLAG_AT_TAIL = 0b0000_0100;
    private static final byte STATE_FLAG_AT_VALUE = 0b0000_1000;

    private final @Nonnull OutputStream output;

    private byte[] stack = new byte[0];
    private int stackIndex = -1;

    public JsonEncoder(@Nonnull OutputStream output) { this.output = output; }

    @Override public void encodeNull()
    {
        try {
            registerRoot();
            NullToJson.format(output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void encodeBoolean(boolean value)
    {
        try {
            registerRoot();
            BooleanToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void encodeLong(long value)
    {
        try {
            registerRoot();
            LongToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void encodeDouble(double value)
    {
        try {
            registerRoot();
            DoubleToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void encodeChar(char value)
    {
        registerRootAndThrow("Plain characters cannot be represented as JSON");
    }

    @Override public void encodeString(@Nonnull String value)
    {
        try {
            registerRoot();
            StringToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void encodeByteArray(@Nonnull byte[] value)
    {
        registerRootAndThrow("Plain byte arrays cannot be represented as JSON");
    }

    @Nonnull @Override public ListEncoder encodeList()
    {
        try {
            registerRoot();

            ensureStackCapacity();
            stack[++stackIndex] = STATE_FLAG_IN_LIST;

            output.write((byte) '[');
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }

        return this;
    }

    @Nonnull @Override public MapEncoder encodeMap()
    {
        try {
            registerRoot();

            ensureStackCapacity();
            stack[++stackIndex] = STATE_FLAG_IN_MAP;

            output.write((byte) '{');
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }

        return this;
    }

    @Override public void addNull()
    {
        try {
            registerItem();
            NullToJson.format(output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void addBoolean(boolean value)
    {
        try {
            registerItem();
            BooleanToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void addLong(long value)
    {
        try {
            registerItem();
            LongToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void addDouble(double value)
    {
        try {
            registerItem();
            DoubleToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void addChar(char value)
    {
        registerItemAndThrow("Plain characters cannot be represented as JSON");
    }

    @Override public void addString(@Nonnull String value)
    {
        try {
            registerItem();
            StringToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void addByteArray(@Nonnull byte[] value)
    {
        registerItemAndThrow("Plain byte arrays cannot be represented as JSON");
    }

    @Nonnull @Override public ListEncoder addList()
    {
        try {
            registerItem();

            ensureStackCapacity();
            stack[++stackIndex] = STATE_FLAG_IN_LIST;

            output.write((byte) '[');
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }

        return this;
    }

    @Nonnull @Override public MapEncoder addMap()
    {
        try {
            registerItem();

            ensureStackCapacity();
            stack[++stackIndex] = STATE_FLAG_IN_MAP;

            output.write((byte) '{');
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }

        return this;
    }

    @Override public void endList()
    {
        assert (stack[stackIndex] & STATE_FLAG_IN_LIST) != 0;

        try {
            output.write((byte) ']');
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
        stackIndex--;
    }

    @Override public void putNull()
    {
        try {
            registerValueOrThrow("The null value cannot be used as a key in JSON");
            NullToJson.format(output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void putBoolean(boolean value)
    {
        try {
            registerValueOrThrow("Booleans cannot be used as keys in JSON");
            BooleanToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void putLong(long value)
    {
        try {
            registerValueOrThrow("Integers cannot be used as keys in JSON");
            LongToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void putDouble(double value)
    {
        try {
            registerValueOrThrow("Floats cannot be used as keys in JSON");
            DoubleToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void putChar(char value)
    {
        registerKeyOrValueAndThrow("Plain characters cannot be represented as JSON");
    }

    @Override public void putString(@Nonnull String value)
    {
        try {
            registerKeyOrValue();
            StringToJson.format(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void putByteArray(@Nonnull byte[] value)
    {
        registerKeyOrValueAndThrow("Plain byte arrays cannot be represented as JSON");
    }

    @Nonnull @Override public ListEncoder putList()
    {
        try {
            registerValueOrThrow("Lists cannot be used as keys in JSON");

            ensureStackCapacity();
            stack[++stackIndex] = STATE_FLAG_IN_LIST;

            output.write((byte) '[');
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }

        return this;
    }

    @Nonnull @Override public MapEncoder putMap()
    {
        try {
            registerValueOrThrow("Maps cannot be used as keys in JSON");

            ensureStackCapacity();
            stack[++stackIndex] = STATE_FLAG_IN_MAP;

            output.write((byte) '{');
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }

        return this;
    }

    @Override public void endMap()
    {
        assert (stack[stackIndex] & STATE_FLAG_IN_MAP) != 0;
        assert (stack[stackIndex] & STATE_FLAG_AT_VALUE) == 0;

        try {
            output.write((byte) '}');
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
        stackIndex--;
    }

    private void ensureStackCapacity()
    {
        if (stackIndex + 1 >= stack.length) {
            var newCapacity = Math.max(Math.multiplyExact(stack.length, 2), 16);
            var newStack = new byte[newCapacity];
            System.arraycopy(stack, 0, newStack, 0, stack.length);
            stack = newStack;
        }
    }

    private void registerRoot()
    {
        assert stackIndex == -1;
    }

    private void registerRootAndThrow(@Nonnull String message)
    {
        assert stackIndex == -1;

        throw new NotEncodableException(message);
    }

    private void registerItem() throws IOException
    {
        assert (stack[stackIndex] & STATE_FLAG_IN_LIST) != 0;

        if ((stack[stackIndex] & STATE_FLAG_AT_TAIL) != 0) {
            output.write((byte) ',');
        } else {
            stack[stackIndex] |= STATE_FLAG_AT_TAIL;
        }
    }

    private void registerItemAndThrow(@Nonnull String message)
    {
        assert (stack[stackIndex] & STATE_FLAG_IN_LIST) != 0;

        throw new NotEncodableException(message);
    }

    private void registerKeyOrValue() throws IOException
    {
        assert (stack[stackIndex] & STATE_FLAG_IN_MAP) != 0;

        if ((stack[stackIndex] & STATE_FLAG_AT_VALUE) == 0) {
            if ((stack[stackIndex] & STATE_FLAG_AT_TAIL) != 0) {
                output.write((byte) ',');
            }
        } else {
            output.write((byte) ':');
            stack[stackIndex] |= STATE_FLAG_AT_TAIL;
        }
        stack[stackIndex] ^= STATE_FLAG_AT_VALUE;
    }

    private void registerKeyOrValueAndThrow(@Nonnull String message)
    {
        assert (stack[stackIndex] & STATE_FLAG_IN_MAP) != 0;

        throw new NotEncodableException(message);
    }

    private void registerValueOrThrow(@Nonnull String message) throws IOException
    {
        assert (stack[stackIndex] & STATE_FLAG_IN_MAP) != 0;

        if ((stack[stackIndex] & STATE_FLAG_AT_VALUE) == 0) {
            throw new NotEncodableException(message);
        } else {
            output.write((byte) ':');
            stack[stackIndex] |= STATE_FLAG_AT_TAIL;
        }
        stack[stackIndex] ^= STATE_FLAG_AT_VALUE;
    }
}
