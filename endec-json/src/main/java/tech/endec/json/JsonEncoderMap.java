package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.json.strconv.CharSequenceToJson;
import tech.endec.type.Encoder;
import tech.endec.type.EncoderOutput;
import tech.endec.type.ex.EncoderArgumentException;
import tech.endec.type.ex.EncoderStateException;

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

    @Override public @Nonnull Encoder next(@Nonnull CharSequence key, int ordinal)
    {
        if (currentPairCount >= expectedPairCount) {
            throw new EncoderStateException("the " + expectedPairCount + " " +
                    "declared map pairs have already been encoded");
        }
        currentPairCount += 1;

        if (isNotEmpty) {
            output.write((byte) ',');
        } else {
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
