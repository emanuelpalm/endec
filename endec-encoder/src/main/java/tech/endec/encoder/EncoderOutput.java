package tech.endec.encoder;

import jakarta.annotation.Nonnull;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public interface EncoderOutput
{
    static @Nonnull EncoderByteArray wrap(@Nonnull byte[] byteArray)
    {
        return new EncoderByteArray(byteArray);
    }

    static @Nonnull EncoderByteBuffer wrap(@Nonnull ByteBuffer buffer)
    {
        return new EncoderByteBuffer(buffer);
    }

    static @Nonnull EncoderOutputStream wrap(@Nonnull OutputStream stream)
    {
        return new EncoderOutputStream(stream);
    }

    void write(byte b);

    default void write(byte[] source) { write(source, 0, source.length); }

    void write(byte[] source, int offset, int length);
}
