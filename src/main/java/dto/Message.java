package dto;

public record Message(String text, Chat chat, Voice voice, Long message_id, From from) {
}
