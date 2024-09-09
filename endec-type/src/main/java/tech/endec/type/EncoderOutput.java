package tech.endec.type;

import jakarta.annotation.Nonnull;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public interface EncoderOutput
{
    static @Nonnull EncoderOutputStream wrap(@Nonnull OutputStream stream)
    {
        return new EncoderOutputStream(stream);
    }

    static @Nonnull EncoderOutputBuffer wrap(@Nonnull ByteBuffer buffer)
    {
        return new EncoderOutputBuffer(buffer);
    }

    void write(byte b);

    default void write(byte[] source) { write(source, 0, source.length); }

    void write(byte[] source, int offset, int length);
}
