package tech.endec.example;

import org.junit.jupiter.api.Test;
import tech.endec.json.JsonEncoder;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest
{
    @Test
    void encode_producesExpectedOutput() throws Exception
    {
        var m = new Message("Hello, World!", "Endec", "2024-09-04T22:54:42Z");
        var o = new ByteArrayOutputStream();
        var e = new JsonEncoder(o);
        MessageEncoder.encode(m, e);
        assertEquals("{\"text\":\"Hello, World!\",\"sender\":\"Endec\",\"timestamp\":\"2024-09-04T22:54:42Z\"}", o.toString(StandardCharsets.UTF_8));
    }
}
