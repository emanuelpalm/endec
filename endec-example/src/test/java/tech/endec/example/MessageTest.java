package tech.endec.example;

import org.junit.jupiter.api.Test;
import tech.endec.json.JsonEncoder;
import tech.endec.type.EncoderOutput;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest
{
    @Test
    void encode_producesExpectedOutput()
    {
        var u = new User("Endec");
        var m = new Message(u, "Hello, World!", "2024-09-04T22:54:42Z");
        var b = ByteBuffer.allocate(2048);
        var o = EncoderOutput.wrap(b);
        var e = new JsonEncoder(o);
        MessageEncoder.encode(m, e);

        var expected = "{\"sender\":{\"name\":\"Endec\"},\"text\":\"Hello, World!\",\"timestamp\":\"2024-09-04T22:54:42Z\"}";
        var actual = new String(b.array(), 0, b.position(), StandardCharsets.UTF_8);
        assertEquals(expected, actual);
    }
}
