package tech.endec.json;

import jakarta.annotation.Nonnull;
import tech.endec.type.Encoder;
import tech.endec.type.EncoderOutput;
import tech.endec.type.ex.EncoderArgumentException;
import tech.endec.type.ex.EncoderStateException;
import tech.endec.type.ex.NotEncodableException;

class JsonEncoderMap implements Encoder.Map
{
    private final @Nonnull EncoderOutput output;

    private boolean isAtValue = false;
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

    @Override public @Nonnull Encoder nextKey()
    {
        if (isAtValue) {
            throw new EncoderStateException("expected value");
        }
        isAtValue = true;

        if (currentPairCount >= expectedPairCount) {
            throw new EncoderStateException("the expected " +
                    expectedPairCount + " pairs have already been added to " +
                    "the encoded map");
        }
        currentPairCount += 1;

        return new JsonEncoder(output)
        {
            private final int id = currentPairCount;

            @Override public void encodeNull()
            {
                throw new NotEncodableException(null, "the null value cannot " +
                        "be used as name in a JSON object");
            }

            @Override public void encodeBoolean(boolean value)
            {
                throw new NotEncodableException(value, "a boolean cannot be " +
                        "used as a name in a JSON object");
            }

            @Override public void encodeLong(long value)
            {
                throw new NotEncodableException(value, "an integer cannot be " +
                        "used as a name in a JSON object");
            }

            @Override public void encodeDouble(double value)
            {
                throw new NotEncodableException(value, "a float cannot be " +
                        "used as a name in a JSON object");
            }

            @Override @Nonnull public List encodeList(int size)
            {
                throw new NotEncodableException(null, "a list cannot be used " +
                        "as a name in a JSON object");
            }

            @Override @Nonnull public Map encodeMap(int size)
            {
                throw new NotEncodableException(null, "a map cannot be used " +
                        "as a name in a JSON object");
            }

            @Override protected void beforeEncode()
            {
                super.beforeEncode();

                if (id != currentPairCount) {
                    throw new EncoderStateException("key out of sequence");
                }

                if (isEnded) {
                    throw new EncoderStateException("adding key to ended " +
                            "map encoder");
                }

                if (isNotEmpty) {
                    output.write((byte) ',');
                } else {
                    isNotEmpty = true;
                }
            }
        };
    }

    @Override public @Nonnull Encoder nextValue()
    {
        if (!isAtValue) {
            throw new EncoderStateException("expected key");
        }
        isAtValue = false;

        return new JsonEncoder(output)
        {
            private final int id = currentPairCount;

            @Override protected void beforeEncode()
            {
                super.beforeEncode();

                if (id != currentPairCount) {
                    throw new EncoderStateException("value out of sequence");
                }

                if (isEnded) {
                    throw new EncoderStateException("adding value to ended " +
                            "map encoder");
                }

                output.write((byte) ':');
            }
        };
    }

    @Override public void end()
    {
        if (currentPairCount < expectedPairCount) {
            throw new EncoderStateException(expectedPairCount + " pairs were " +
                    "declared, but only " + currentPairCount + " were encoded");
        }
        if (isAtValue) {
            throw new EncoderStateException("map encoder has dangling key");
        }
        if (isEnded) {
            throw new EncoderStateException("map encoder already ended");
        }
        isEnded = true;

        output.write((byte) '}');
    }
}
