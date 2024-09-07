package tech.endec.json;

import jakarta.annotation.Nonnull;

import java.io.IOException;
import java.io.OutputStream;

public class JsonEncoder extends JsonBaseEncoder
{
    private boolean isUsed = false;

    public JsonEncoder(@Nonnull OutputStream output)
    {
        super(output);
    }

    @Override protected void onEncode() throws IOException
    {
        if (!isUsed) {
            isUsed = true;
            super.onEncode();
            return;
        }
        throw new IllegalStateException("encoder already used");
    }
}
