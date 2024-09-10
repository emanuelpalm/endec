package tech.endec.type;

import jakarta.annotation.Nonnull;
import tech.endec.type.ex.EncoderOutputException;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class EncoderOutputStream implements Closeable, EncoderOutput
{
    private final @Nonnull OutputStream inner;

    public EncoderOutputStream(@Nonnull OutputStream inner) { this.inner = inner; }

    @Override public void write(byte b)
    {
        try {
            inner.write(b);
        } catch (IOException exception) {
            throw new EncoderOutputException(exception);
        }
    }

    @Override public void write(byte[] source, int offset, int length)
    {
        try {
            inner.write(source, offset, length);
        } catch (IOException exception) {
            throw new EncoderOutputException(exception);
        }
    }

    public void flush()
    {
        try {
            inner.flush();
        } catch (IOException e) {
            throw new EncoderOutputException(e);
        }
    }

    @Override public void close()
    {
        try {
            inner.close();
        } catch (IOException e) {
            throw new EncoderOutputException(e);
        }
    }

    public @Nonnull OutputStream unwrap() { return inner; }
}
