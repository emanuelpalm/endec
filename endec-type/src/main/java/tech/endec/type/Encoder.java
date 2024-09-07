package tech.endec.type;

import jakarta.annotation.Nonnull;

import java.io.IOException;

@SuppressWarnings("unused")
public interface Encoder
{
    void encodeNull() throws IOException;

    void encodeBoolean(boolean value) throws IOException;

    default void encodeByte(byte value) throws IOException { encodeLong(value); }

    default void encodeShort(short value) throws IOException { encodeLong(value); }

    default void encodeInt(int value) throws IOException { encodeLong(value); }

    void encodeLong(long value) throws IOException;

    default void encodeFloat(float value) throws IOException { encodeDouble(value); }

    void encodeDouble(double value) throws IOException;

    void encodeChar(char value) throws IOException;

    void encodeString(@Nonnull String value) throws IOException;

    void encodeByteArray(@Nonnull byte[] value) throws IOException;

    @Nonnull ListEncoder encodeList() throws IOException;

    @Nonnull MapEncoder encodeMap() throws IOException;
}