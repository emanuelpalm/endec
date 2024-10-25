package tech.endec.encoder.json;

import jakarta.annotation.Nonnull;
import tech.endec.encoder.Encoder;
import tech.endec.encoder.EncoderOutput;
import tech.endec.encoder.exception.EncoderArgumentException;
import tech.endec.encoder.exception.EncoderStateException;

class JsonEncoderList implements Encoder.List
{
    private final @Nonnull EncoderOutput output;

    private boolean isEnded = false;
    private boolean isNotEmpty = false;

    private final int expectedItemCount;
    private int currentItemCount = 0;

    JsonEncoderList(@Nonnull EncoderOutput output, int expectedItemCount)
    {
        if (expectedItemCount < 0) {
            throw new EncoderArgumentException("expectedItemCount < 0");
        }
        this.output = output;
        this.expectedItemCount = expectedItemCount;
    }

    @Nonnull @Override public Encoder next()
    {
        if (currentItemCount >= expectedItemCount) {
            throw new EncoderStateException("the " + expectedItemCount + " " +
                    "declared list items have already been encoded");
        }
        currentItemCount += 1;

        return new JsonEncoder(output)
        {
            private final int id = currentItemCount;

            @Override protected void beforeEncode()
            {
                super.beforeEncode();

                if (id != currentItemCount) {
                    throw new EncoderStateException("item out of sequence");
                }

                if (isEnded) {
                    throw new EncoderStateException("attempting to add value " +
                            "to ended list encoder");
                }

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
        if (currentItemCount < expectedItemCount) {
            throw new EncoderStateException(expectedItemCount + " items were " +
                    "declared, but only " + currentItemCount + " were encoded");
        }
        if (isEnded) {
            throw new EncoderStateException("list encoder already ended");
        }
        isEnded = true;

        output.write((byte) ']');
    }
}
