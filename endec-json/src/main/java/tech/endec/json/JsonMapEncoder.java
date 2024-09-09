package tech.endec.json;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import tech.endec.type.EncoderOutput;
import tech.endec.type.ex.EncoderArgumentException;
import tech.endec.type.ex.EncoderStateException;
import tech.endec.type.ex.NotEncodableException;

class JsonMapEncoder extends JsonEncoder
{
    private final @Nonnull EncoderOutput output;

    private boolean isAtValue = false;
    private boolean isEnded = false;
    private boolean isNotEmpty = false;

    private final int expectedSize;
    private int remainingPairs;

    JsonMapEncoder(@Nonnull EncoderOutput output, int expectedSize)
    {
        if (expectedSize < 0) {
            throw new EncoderArgumentException("expectedSize < 0");
        }
        this.output = output;
        this.expectedSize = expectedSize;
        remainingPairs = expectedSize;
    }

    @Override protected EncoderOutput getOutput()
    {
        return output;
    }

    @Override public void encodeNull()
    {
        throwIfAtKey(null, "the null value cannot be used as name in a JSON object");
        super.encodeNull();
    }

    @Override public void encodeBoolean(boolean value)
    {
        throwIfAtKey(value, "a boolean cannot be used as a name in a JSON object");
        super.encodeBoolean(value);
    }

    @Override public void encodeLong(long value)
    {
        throwIfAtKey(value, "a integer cannot be used as a name in a JSON object");
        super.encodeLong(value);
    }

    @Override public void encodeDouble(double value)
    {
        throwIfAtKey(value, "a float cannot be used as a name in a JSON object");

        super.encodeDouble(value);
    }

    @Override public void encodeList(int size, @Nonnull Consumer consumer)
    {
        throwIfAtKey(null, "a list cannot be used as a name in a JSON object");
        super.encodeList(size, consumer);
    }

    @Override public void encodeMap(int size, @Nonnull Consumer consumer)
    {
        throwIfAtKey(null, "a map cannot be used as a name in a JSON object");
        super.encodeMap(size, consumer);
    }

    private void throwIfAtKey(@Nullable Object value, @Nonnull String message)
    {
        throw new NotEncodableException(value, message);
    }

    void end()
    {
        if (isEnded) {
            throw new EncoderStateException("map encoder already ended");
        }
        if (isAtValue) {
            throw new EncoderStateException("attempting to end map encoder " +
                    "with dangling key");
        }
        if (remainingPairs > 0) {
            throw new EncoderStateException("expected " + remainingPairs + " " +
                    "additional key/value pairs to be added to encoded map");
        }
        isEnded = true;
    }

    @Override protected void onEncode()
    {
        if (isEnded) {
            throw new EncoderStateException("attempting to add key to ended " +
                    "map encoder");
        }
        if (isAtValue) {
            isAtValue = false;
            output.write((byte) ':');
        } else {
            isAtValue = true;

            if (remainingPairs <= 0) {
                throw new EncoderStateException(expectedSize + " key/value " +
                        "pairs already added to encoded map");
            }
            remainingPairs -= 1;

            if (isNotEmpty) {
                output.write((byte) ',');
            } else {
                isNotEmpty = true;
            }
        }
    }
}
