package tech.endec.example;

import tech.endec.Decodable;
import tech.endec.Encodable;

@Encodable
@Decodable
public record User(String name)
{
}
