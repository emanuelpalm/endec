package tech.endec.encoder;

import jakarta.annotation.Nonnull;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public interface EncoderOutput
{
    static @Nonnull EncoderOutputBuffer wrap(@Nonnull ByteBuffer buffer)
    {
        return new EncoderOutputBuffer(buffer);
    }

    static @Nonnull EncoderOutputByteArray wrap(@Nonnull byte[] byteArray)
    {
        return new EncoderOutputByteArray(byteArray);
    }

    static @Nonnull EncoderOutputStream wrap(@Nonnull OutputStream stream)
    {
        return new EncoderOutputStream(stream);
    }

    void write(byte b);

    default void write(byte[] source) { write(source, 0, source.length); }

    void write(byte[] source, int offset, int length);
}
