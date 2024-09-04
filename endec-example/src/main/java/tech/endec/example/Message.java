package tech.endec.example;

import tech.endec.Decodable;
import tech.endec.Encodable;

@Encodable
@Decodable
public record Message(String text, String sender, String timestamp) { }
