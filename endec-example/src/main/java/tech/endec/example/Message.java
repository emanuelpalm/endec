package tech.endec.example;

import tech.endec.Decodable;
import tech.endec.Encodable;

@Encodable
@Decodable
public record Message(User sender, String text, String timestamp) {}
