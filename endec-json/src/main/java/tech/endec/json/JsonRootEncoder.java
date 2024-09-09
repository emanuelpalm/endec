package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.type.EncoderOutput;
import tech.endec.type.ex.EncoderStateException;

class JsonRootEncoder extends JsonEncoder
{
    private final @Nonnull EncoderOutput output;

    private boolean isUsed = false;

    JsonRootEncoder(@Nonnull EncoderOutput output) { this.output = output; }

    @Override protected EncoderOutput getOutput()
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
