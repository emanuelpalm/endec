package tech.endec.type;

import jakarta.annotation.Nonnull;

@SuppressWarnings("unused")
public interface ListEncoder
{
    void addNull();

    void addBoolean(boolean value);

    default void addByte(byte value) { addLong(value); }

    default void addShort(short value) { addLong(value); }

    default void addInt(int value) { addLong(value); }

    void addLong(long value);

    default void addFloat(float value) { addDouble(value); }

    void addDouble(double value);

    void addChar(char value);

    void addString(@Nonnull String value);

    void addByteArray(@Nonnull byte[] value);

    @Nonnull ListEncoder addList();

    @Nonnull MapEncoder addMap();

    void endList();
}
