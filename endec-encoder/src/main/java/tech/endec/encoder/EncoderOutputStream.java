package tech.endec.encoder;

import jakarta.annotation.Nonnull;
import tech.endec.encoder.exception.EncoderOutputException;

import java.io.IOException;
import java.io.OutputStream;

public class EncoderOutputStream extends OutputStream implements EncoderOutput
{
    private final @Nonnull OutputStream inner;

    public EncoderOutputStream(@Nonnull OutputStream inner) { this.inner = inner; }

    @Override public void write(byte b)
    {
        try {
            inner.write(b);
        }
        catch (IOException exception) {
            throw new EncoderOutputException(exception);
        }
    }

    @Override public void write(int b) throws IOException { write((byte) b); }

    @Override public void write(@Nonnull byte[] source) { write(source, 0, source.length); }

    @Override public void write(@Nonnull byte[] source, int offset, int length)
    {
        try {
            inner.write(source, offset, length);
        }
        catch (IOException exception) {
            throw new EncoderOutputException(exception);
        }
    }

    @Override public void flush()
    {
        try {
            inner.flush();
        }
        catch (IOException e) {
            throw new EncoderOutputException(e);
        }
    }

    @Override public void close()
    {
        try {
            inner.close();
        }
        catch (IOException e) {
            throw new EncoderOutputException(e);
        }
    }

    public @Nonnull OutputStream unwrap() { return inner; }
}
