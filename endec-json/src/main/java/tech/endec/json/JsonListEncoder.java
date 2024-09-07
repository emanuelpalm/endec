package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.type.ex.EncoderArgumentException;
import tech.endec.type.ex.EncoderOutputException;
import tech.endec.type.ex.EncoderStateException;

import java.io.IOException;
import java.io.OutputStream;

class JsonListEncoder extends JsonEncoder
{
    private final @Nonnull OutputStream output;

    private boolean isEnded = false;
    private boolean isNotEmpty = false;

    private final int expectedSize;
    private int remainingItems;

    JsonListEncoder(@Nonnull OutputStream output, int expectedSize)
    {
        if (expectedSize < 0) {
            throw new EncoderArgumentException("expectedSize < 0");
        }
        this.output = output;
        this.expectedSize = expectedSize;
        remainingItems = expectedSize;
    }

    @Override protected OutputStream getOutput()
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

        try {
            if (isNotEmpty) {
                output.write((byte) ',');
            } else {
                isNotEmpty = true;
            }
        } catch (IOException exception) {
            throw new EncoderOutputException(exception);
        }
    }
}
