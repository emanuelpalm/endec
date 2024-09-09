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

    private final int expectedSize;
    private int remainingPairs;

    JsonEncoderMap(@Nonnull EncoderOutput output, int expectedSize)
    {
        if (expectedSize < 0) {
            throw new EncoderArgumentException("expectedSize < 0");
        }
        this.output = output;
        this.expectedSize = expectedSize;
        remainingPairs = expectedSize;
    }

    @Override public @Nonnull Encoder key()
    {
        return new JsonEncoder()
        {
            private boolean isUsed = false;

            @Override protected @Nonnull EncoderOutput getOutput() {
                return output;
            }

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
                if (isUsed || isAtValue) {
                    throw new EncoderStateException("key already encoded");
                }
                isUsed = true;
                isAtValue = true;

                if (isEnded) {
                    throw new EncoderStateException("attempting to add key " +
                            "to ended map encoder");
                }

                if (remainingPairs <= 0) {
                    throw new EncoderStateException(expectedSize + " pairs " +
                            "already added to encoded map");
                }
                remainingPairs -= 1;

                if (isNotEmpty) {
                    output.write((byte) ',');
                } else {
                    isNotEmpty = true;
                }
            }
        };
    }

    @Override public @Nonnull Encoder val()
    {
        return new JsonEncoder()
        {
            private boolean isUsed = false;

            @Override protected @Nonnull EncoderOutput getOutput() {
                return output;
            }

            @Override protected void beforeEncode()
            {
                if (isUsed || !isAtValue) {
                    throw new EncoderStateException("value already encoded");
                }
                isUsed = true;
                isAtValue = false;

                if (isEnded) {
                    throw new EncoderStateException("attempting to add value " +
                            "to ended map encoder");
                }

                output.write((byte) ':');
            }
        };
    }

    @Override public void end()
    {
        if (isEnded) {
            throw new EncoderStateException("list encoder already ended");
        }
        if (isAtValue) {
            throw new EncoderStateException("attempting to end map encoder " +
                    "with dangling key");
        }
        if (remainingPairs > 0) {
            throw new EncoderStateException("expected " + remainingPairs + " " +
                    "additional pairs to be added to encoded map");
        }
        isEnded = true;

        output.write((byte) '}');
    }
}
