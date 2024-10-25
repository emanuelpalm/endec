package tech.endec.encoder;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@SuppressWarnings("unused")
public interface Encoder
{
    void encodeNull();

    void encodeBoolean(boolean value);

    void encodeByte(byte value);

    void encodeShort(short value);

    void encodeInt(int value);

    void encodeLong(long value);

    void encodeChar(char value);

    void encodeFloat(float value);

    void encodeDouble(double value);

    void encodeCharSequence(@Nonnull CharSequence value);

    void encodeByteArray(@Nonnull byte[] value);

    @Nonnull List encodeList(@Nullable Object prototype, int size);

    @Nonnull Map encodeMap(@Nullable Object prototype, int size);

    interface List
    {
        @Nonnull Encoder next();

        void end();
    }

    interface Map
    {
        @Nonnull Encoder next(@Nonnull CharSequence key, int ordinal);

        void end();
    }
}