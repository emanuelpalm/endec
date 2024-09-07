package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.type.ListEncoder;

import java.io.IOException;
import java.io.OutputStream;

class JsonListEncoder extends JsonBaseEncoder implements ListEncoder
{
    private final @Nonnull JsonBaseEncoder parent;

    private boolean isNotEmpty = false;

    JsonListEncoder(@Nonnull OutputStream output, @Nonnull JsonBaseEncoder parent)
    {
        super(output);
        this.parent = parent;
    }

    @Override protected void onEncode() throws IOException
    {
        super.onEncode();
        if (isNotEmpty) {
            output.write((byte) ',');
        } else {
            isNotEmpty = true;
        }
    }

    @Override public void close() throws IOException
    {
        super.onEncode();
        output.write((byte) ']');
        parent.onChildClose();
    }
}
