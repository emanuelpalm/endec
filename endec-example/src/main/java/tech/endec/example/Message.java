package tech.endec.example;

import tech.endec.Codable;

@Codable
public record Message(String text, String sender, String timestamp) {
}
