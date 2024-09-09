package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.type.Encoder;
import tech.endec.type.EncoderOutput;
import tech.endec.type.ex.EncoderArgumentException;
import tech.endec.type.ex.EncoderStateException;

class JsonEncoderList implements Encoder.List
{
    private final @Nonnull EncoderOutput output;

    private boolean isEnded = false;
    private boolean isNotEmpty = false;

    private final int expectedSize;
    private int remainingItems;

    JsonEncoderList(@Nonnull EncoderOutput output, int expectedSize)
    {
        if (expectedSize < 0) {
            throw new EncoderArgumentException("expectedSize < 0");
        }
        this.output = output;
        this.expectedSize = expectedSize;
        remainingItems = expectedSize;
    }

    @Nonnull @Override public Encoder item()
    {
        return new JsonEncoder() {
            private boolean isUsed = false;

            @Nonnull @Override protected EncoderOutput getOutput() { return output; }

            @Override protected void beforeEncode()
            {
                if (isUsed) {
                    throw new EncoderStateException("item already encoded");
                }
                isUsed = true;

                if (isEnded) {
                    throw new EncoderStateException("attempting to add value " +
                            "to ended list encoder");
                }

                if (remainingItems <= 0) {
                    throw new EncoderStateException(expectedSize + " items " +
                            "already added to encoded list");
                }
                remainingItems -= 1;

                if (isNotEmpty) {
                    output.write((byte) ',');
                } else {
                    isNotEmpty = true;
                }
            }
        };
    }

    @Override public void end()
    {
        if (isEnded) {
            throw new EncoderStateException("list encoder already ended");
        }
        if (remainingItems > 0) {
            throw new EncoderStateException("expected " + remainingItems + " " +
                    "additional items to be added to encoded list");
        }
        isEnded = true;

        output.write((byte) ']');
    }
}
