package tech.endec;

import jakarta.annotation.Nonnull;

public interface Encoder
{
    void encodeNull();

    void encodeBoolean(boolean value);

    default void encodeByte(byte value) { encodeLong(value); }

    default void encodeShort(short value) { encodeLong(value); }

    default void encodeInt(int value) { encodeLong(value); }

    void encodeLong(long value);

    default void encodeFloat(float value) { encodeDouble(value); }

    void encodeDouble(double value);

    void encodeChar(char value);

    void encodeString(@Nonnull String value);

    void encodeByteArray(@Nonnull byte[] value);

    @Nonnull ListEncoder encodeList();

    @Nonnull MapEncoder encodeMap();
}