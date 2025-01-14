package tech.endec.encoder;

import jakarta.annotation.Nonnull;

public class EncoderByteArray implements EncoderOutput
{
    private final @Nonnull byte[] inner;
    private int index;

    public EncoderByteArray(@Nonnull byte[] inner) { this.inner = inner; }

    @Override public void write(byte b)
    {
        inner[index] = b;
        index += 1;
    }

    @Override public void write(@Nonnull byte[] source, int offset, int length)
    {
        System.arraycopy(source, offset, inner, index, length);
        index += length;
    }

    public int capacity() { return inner.length; }

    public int length() { return index; }

    public @Nonnull byte[] unwrap() { return inner; }
}
