package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.type.Encoder;
import tech.endec.type.ListEncoder;
import tech.endec.type.MapEncoder;
import tech.endec.type.ex.EncoderIOException;
import tech.endec.type.ex.NotEncodableException;
import tech.endec.strconv.LongToASCII;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class JsonEncoder implements Encoder, ListEncoder, MapEncoder
{
    private static final byte STATE_FLAG_IN_LIST = 0b0000_0001;
    private static final byte STATE_FLAG_IN_MAP = 0b0000_0010;
    private static final byte STATE_FLAG_AT_TAIL = 0b0000_0100;
    private static final byte STATE_FLAG_AT_VALUE = 0b0000_1000;

    private static final byte[] STRING_FALSE = {'f', 'a', 'l', 's', 'e'};
    private static final byte[] STRING_NULL = {'n', 'u', 'l', 'l'};
    private static final byte[] STRING_TRUE = {'t', 'r', 'u', 'e'};

    private final @Nonnull OutputStream output;

    private byte[] stack = new byte[0];
    private int stackIndex = -1;

    public JsonEncoder(@Nonnull OutputStream output) { this.output = output; }

    @Override public void encodeNull()
    {
        try {
            registerRoot();
            output.write(STRING_NULL);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void encodeBoolean(boolean value)
    {
        try {
            registerRoot();
            output.write(value ? STRING_TRUE : STRING_FALSE);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void encodeLong(long value)
    {
        try {
            registerRoot();
            writeLong(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void encodeDouble(double value)
    {
        try {
            registerRoot();
            writeDouble(value, output);
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
            writeString(value, output);
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
            output.write(STRING_NULL);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void addBoolean(boolean value)
    {
        try {
            registerItem();
            output.write(value ? STRING_TRUE : STRING_FALSE);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void addLong(long value)
    {
        try {
            registerItem();
            writeLong(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void addDouble(double value)
    {
        try {
            registerItem();
            writeDouble(value, output);
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
            writeString(value, output);
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
            output.write(STRING_NULL);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void putBoolean(boolean value)
    {
        try {
            registerValueOrThrow("Booleans cannot be used as keys in JSON");
            output.write(value ? STRING_TRUE : STRING_FALSE);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void putLong(long value)
    {
        try {
            registerValueOrThrow("Integers cannot be used as keys in JSON");
            writeLong(value, output);
        } catch (IOException e) {
            throw new EncoderIOException(e);
        }
    }

    @Override public void putDouble(double value)
    {
        try {
            registerValueOrThrow("Floats cannot be used as keys in JSON");
            writeDouble(value, output);
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
            writeString(value, output);
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

    private static void writeDouble(double value, @Nonnull OutputStream output) throws IOException
    {
        if (Double.isFinite(value)) {
            var string = Double.toString(value);
            output.write(string.getBytes(StandardCharsets.US_ASCII));
        } else {
            throw new NotEncodableException("Only finite floating point " +
                    "numbers can be represented as JSON");
        }
    }

    private static void writeLong(long value, @Nonnull OutputStream output) throws IOException
    {
        var buffer = new byte[LongToASCII.BUFFER_SIZE];
        var size = LongToASCII.format(value, buffer);
        output.write(buffer, LongToASCII.BUFFER_SIZE - size, size);
    }

    // Keys used to represent escaped string characters.
    static final byte BA = 'b';  // 0x08
    static final byte HT = 't';  // 0x09
    static final byte LF = 'n';  // 0x0A
    static final byte FF = 'f';  // 0x0C
    static final byte CR = 'r';  // 0x0D
    static final byte QU = '"';  // 0x22
    static final byte BS = '\\'; // 0x5C
    static final byte UU = 'u';  // 0x00..0x1F, except for the above, and 0x7F.
    static final byte __ = 0;    // Not escaped.

    // Lookup table, mapping each Unicode point in the range 0 to 127 to the
    // character used to represent it when escaped. Special treatment is given
    // to `UU` (`u`) and `__` (0). The former indicates that a four-digit escape
    // sequence is to be used (as in \u0012), while the the latter denotes that
    // escaping is not required.
    static final byte[] TABLE_ESCAPES = {
            //   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            UU, UU, UU, UU, UU, UU, UU, UU, BA, HT, LF, UU, FF, CR, UU, UU, // 0
            UU, UU, UU, UU, UU, UU, UU, UU, UU, UU, UU, UU, UU, UU, UU, UU, // 1
            __, __, QU, __, __, __, __, __, __, __, __, __, __, __, __, __, // 2
            __, __, __, __, __, __, __, __, __, __, __, __, __, __, __, __, // 3
            __, __, __, __, __, __, __, __, __, __, __, __, __, __, __, __, // 4
            __, __, __, __, __, __, __, __, __, __, __, __, BS, __, __, __, // 5
            __, __, __, __, __, __, __, __, __, __, __, __, __, __, __, __, // 6
            __, __, __, __, __, __, __, __, __, __, __, __, __, __, __, UU, // 7
    };

    static final byte[] TABLE_HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static void writeString(@Nonnull String value, @Nonnull OutputStream output) throws IOException
    {
        output.write((byte) '"');

        for (var i = 0; i < value.length(); ) {
            var cp = value.codePointAt(i);

            if (cp <= 0x7F) {
                var escape = TABLE_ESCAPES[cp];
                if (escape == 0) {
                    output.write((byte) cp);
                } else {
                    output.write(new byte[]{'\\', escape});
                    if (escape == 'u') {
                        output.write(new byte[]{'0', '0',
                                TABLE_HEX_DIGITS[cp >>> 4],
                                TABLE_HEX_DIGITS[cp & 0xF]});
                    }
                }
                i += 1;
            } else if (cp <= 0x7FF) {
                output.write(new byte[]{
                        (byte) (0xC0 | (cp >> 6)),           // 110xxxxx
                        (byte) (0x80 | (cp & 0x3F))});       // 10xxxxxx
                i += 1;
            } else if (cp <= 0xFFFF) {
                output.write(new byte[]{
                        (byte) (0xE0 | (cp >> 12)),          // 1110xxxx
                        (byte) (0x80 | ((cp >> 6) & 0x3F)),  // 10xxxxxx
                        (byte) (0x80 | (cp & 0x3F))});       // 10xxxxxx
                i += 1;
            } else {
                output.write(new byte[]{
                        (byte) (0xF0 | (cp >> 18)),          // 11110xxx
                        (byte) (0x80 | ((cp >> 12) & 0x3F)), // 10xxxxxx
                        (byte) (0x80 | ((cp >> 6) & 0x3F)),  // 10xxxxxx
                        (byte) (0x80 | (cp & 0x3F))});       // 10xxxxxx
                i += 2;
            }
        }

        output.write((byte) '"');
    }
}
