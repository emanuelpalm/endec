package tech.endec.type;

import java.io.IOException;

public interface ListEncoder extends Encoder, AutoCloseable
{
    @Override void close() throws IOException;
}
