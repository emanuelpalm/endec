package tech.endec.type;

import java.io.IOException;

public interface MapEncoder extends Encoder, AutoCloseable
{
    @Override void close() throws IOException;
}
