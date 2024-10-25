package tech.endec.example;

import tech.endec.annotation.Decodable;
import tech.endec.annotation.Encodable;

@Encodable
@Decodable
public record Message(User sender, String text, String timestamp) {}
