package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.type.EncoderOutput;
import tech.endec.type.ex.EncoderArgumentException;
import tech.endec.type.ex.EncoderStateException;

class JsonListEncoder extends JsonEncoder
{
    private final @Nonnull EncoderOutput output;

    private boolean isEnded = false;
    private boolean isNotEmpty = false;

    private final int expectedSize;
    private int remainingItems;

    JsonListEncoder(@Nonnull EncoderOutput output, int expectedSize)
    {
        if (expectedSize < 0) {
            throw new EncoderArgumentException("expectedSize < 0");
        }
        this.output = output;
        this.expectedSize = expectedSize;
        remainingItems = expectedSize;
    }

    @Override protected EncoderOutput getOutput()
    {
        return output;
    }

    void end()
    {
        if (isEnded) {
            throw new EncoderStateException("list encoder already ended");
        }
        if (remainingItems > 0) {
            throw new EncoderStateException("expected " + remainingItems + " " +
                    "additional items to be added to encoded list");
        }
        isEnded = true;
    }

    @Override protected void onEncode()
    {
        if (isEnded) {
            throw new EncoderStateException("attempting to add value to " +
                    "ended list encoder");
        }

        if (remainingItems <= 0) {
            throw new EncoderStateException(expectedSize + " items already " +
                    "added to encoded list");
        }
        remainingItems -= 1;

        if (isNotEmpty) {
            output.write((byte) ',');
        } else {
            isNotEmpty = true;
        }
    }
}
