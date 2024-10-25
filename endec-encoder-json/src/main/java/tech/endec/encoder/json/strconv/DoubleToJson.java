package tech.endec.encoder.json.strconv;

import jakarta.annotation.Nonnull;
import tech.endec.encoder.EncoderOutput;
import tech.endec.encoder.exception.NotEncodableException;

import java.nio.charset.StandardCharsets;

public final class DoubleToJson
{
    private DoubleToJson() {}

    public static void format(double value, @Nonnull EncoderOutput output)
    {
        if (Double.isFinite(value)) {
            var string = Double.toString(value);
            output.write(string.getBytes(StandardCharsets.US_ASCII));
        } else {
            throw new NotEncodableException(value, "Only finite floating " +
                    "point numbers can be represented as JSON");
        }
    }
}
