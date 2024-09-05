package tech.endec.json.strconv;

import jakarta.annotation.Nonnull;
import tech.endec.type.ex.NotEncodableException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DoubleToJson
{
    private DoubleToJson() { }

    public static void format(double value, @Nonnull OutputStream output) throws IOException
    {
        if (Double.isFinite(value)) {
            var string = Double.toString(value);
            output.write(string.getBytes(StandardCharsets.US_ASCII));
        } else {
            throw new NotEncodableException("Only finite floating point " +
                    "numbers can be represented as JSON");
        }
    }
}
