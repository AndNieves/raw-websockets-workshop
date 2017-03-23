package hello;

public class ChatMessage {
    private String user;
    private String text;
    private String time;

    public String getUser() {
        return user;
    }

    public ChatMessage setUser(String user) {
        this.user = user;
        return this;
    }

    public String getText() {
        return text;
    }

    public ChatMessage setText(String text) {
        this.text = text;
        return this;
    }

    public String getTime() {
        return time;
    }

    public ChatMessage setTime(String time) {
        this.time = time;
        return this;
    }
}
