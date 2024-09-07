package tech.endec.type;

import jakarta.annotation.Nonnull;

@SuppressWarnings("unused")
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

    void encodeList(int size, @Nonnull Consumer consumer);

    void encodeMap(int size, @Nonnull Consumer consumer);

    interface Consumer {
        void encode(@Nonnull Encoder encoder);
    }
}