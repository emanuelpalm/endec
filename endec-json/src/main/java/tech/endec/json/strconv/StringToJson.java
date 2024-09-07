package tech.endec.json.strconv;

import jakarta.annotation.Nonnull;
import tech.endec.type.ex.EncoderOutputException;

import java.io.IOException;
import java.io.OutputStream;

public final class StringToJson
{
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

    static final byte[] TABLE_HEXADECIMAL_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private StringToJson() {}

    public static void format(@Nonnull String value, @Nonnull OutputStream output)
    {
        try {
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
                                    TABLE_HEXADECIMAL_DIGITS[cp >>> 4],
                                    TABLE_HEXADECIMAL_DIGITS[cp & 0xF]});
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
        } catch (IOException exception) {
            throw new EncoderOutputException(exception);
        }
    }
}
