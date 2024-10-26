package tech.endec.encoder.json;

import jakarta.annotation.Nonnull;
import tech.endec.encoder.json.strconv.CharSequenceToJson;
import tech.endec.encoder.Encoder;
import tech.endec.encoder.EncoderOutput;
import tech.endec.encoder.exception.EncoderArgumentException;
import tech.endec.encoder.exception.EncoderStateException;

class JsonEncoderMap implements Encoder.Map
{
    private final @Nonnull EncoderOutput output;

    private boolean isEnded = false;
    private boolean isNotEmpty = false;

    private final int expectedPairCount;
    private int currentPairCount = 0;

    JsonEncoderMap(@Nonnull EncoderOutput output, int expectedPairCount)
    {
        if (expectedPairCount < 0) {
            throw new EncoderArgumentException("expectedPairCount < 0");
        }
        this.output = output;
        this.expectedPairCount = expectedPairCount;
    }

    @Override public @Nonnull Encoder next(int ordinal, @Nonnull CharSequence key)
    {
        if (isEnded) {
            throw new EncoderStateException("adding pair to ended map encoder");
        }
        if (currentPairCount >= expectedPairCount) {
            throw new EncoderStateException("the " + expectedPairCount + " " +
                    "declared map pairs have already been encoded");
        }
        currentPairCount += 1;

        if (isNotEmpty) {
            output.write((byte) ',');
        }
        else {
            isNotEmpty = true;
        }

        CharSequenceToJson.format(key, output);

        output.write((byte) ':');

        return new JsonEncoder(output)
        {
            private final int sequenceNumber = currentPairCount;

            @Override protected void beforeEncode()
            {
                super.beforeEncode();

                if (sequenceNumber != currentPairCount) {
                    throw new EncoderStateException("value out of sequence");
                }
                if (isEnded) {
                    throw new EncoderStateException("adding value to ended " +
                            "map encoder");
                }
            }
        };
    }

    @Override public void end()
    {
        if (currentPairCount < expectedPairCount) {
            throw new EncoderStateException(expectedPairCount + " pairs were " +
                    "declared, but only " + currentPairCount + " were encoded");
        }
        if (isEnded) {
            throw new EncoderStateException("map encoder already ended");
        }
        isEnded = true;

        output.write((byte) '}');
    }
}
