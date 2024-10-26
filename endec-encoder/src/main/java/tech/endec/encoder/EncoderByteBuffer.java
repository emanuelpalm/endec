package tech.endec.encoder;

import jakarta.annotation.Nonnull;

import java.nio.ByteBuffer;

public class EncoderByteBuffer implements EncoderOutput
{
    private final @Nonnull ByteBuffer inner;

    public EncoderByteBuffer(@Nonnull ByteBuffer inner) { this.inner = inner; }

    @Override public void write(byte b) { inner.put(b); }

    @Override public void write(byte[] source, int offset, int length)
    {
        inner.put(source, offset, length);
    }

    public @Nonnull ByteBuffer unwrap() { return inner; }
}
