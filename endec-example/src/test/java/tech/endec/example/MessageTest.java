package tech.endec.example;

import org.junit.jupiter.api.Test;
import tech.endec.encoder.json.JsonEncoder;
import tech.endec.encoder.EncoderOutput;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest
{
    @Test
    void encode_producesExpectedOutput()
    {
        var u = new User("Endec", 1234);
        var m = new Message(u, "Hello, World!", "2024-09-04T22:54:42Z");
        var o = EncoderOutput.wrap(new byte[256]);
        var e = new JsonEncoder(o);
        MessageEncoder.encode(m, e);

        var expected = "{\"sender\":{\"name\":\"Endec\",\"id\":1234},\"text\":\"Hello, World!\",\"timestamp\":\"2024-09-04T22:54:42Z\"}";
        var actual = new String(o.unwrap(), 0, o.length(), StandardCharsets.UTF_8);
        assertEquals(expected, actual);
    }
}
