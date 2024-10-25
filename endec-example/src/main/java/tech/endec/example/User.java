package tech.endec.example;

import tech.endec.annotation.Decodable;
import tech.endec.annotation.Encodable;

@Encodable
@Decodable
public record User(String name, int id)
{
}
