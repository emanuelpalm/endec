package tech.endec.type;

import jakarta.annotation.Nonnull;

public interface MapEncoder
{
    void putNull();

    void putBoolean(boolean value);

    default void putByte(byte value) { putLong(value); }

    default void putShort(short value) { putLong(value); }

    default void putInt(int value) { putLong(value); }

    void putLong(long value);

    default void putFloat(float value) { putDouble(value); }

    void putDouble(double value);

    void putChar(char value);

    void putString(@Nonnull String value);

    void putByteArray(@Nonnull byte[] value);

    @Nonnull ListEncoder putList();

    @Nonnull MapEncoder putMap();

    void endMap();
}
