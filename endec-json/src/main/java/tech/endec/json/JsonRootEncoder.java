package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.type.ex.EncoderStateException;

import java.io.OutputStream;

class JsonRootEncoder extends JsonEncoder
{
    private final @Nonnull OutputStream output;

    private boolean isUsed = false;

    JsonRootEncoder(@Nonnull OutputStream output) { this.output = output; }

    @Override protected OutputStream getOutput()
    {
        return output;
    }

    @Override protected void onEncode()
    {
        if (isUsed) {
            throw new EncoderStateException("root encoder already used");
        }
        isUsed = true;
    }
}
