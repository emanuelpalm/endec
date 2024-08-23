package tech.endec.example;

import tech.endec.Codable;
import tech.endec.Decodable;
import tech.endec.Encodable;

@Codable
@Decodable
@Encodable
public record Message(String text, String sender)
{
}
