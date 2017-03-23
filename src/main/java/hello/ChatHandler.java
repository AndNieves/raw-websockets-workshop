package hello;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private List<WebSocketSession> webSocketSessions = new ArrayList<>();

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Pattern pattern = Pattern.compile("@\\d", Pattern.CASE_INSENSITIVE);

    public void broadcastMessage(ChatMessage message) {
        System.out.println("Trying to send:" + message.getText());
        for (WebSocketSession session : webSocketSessions) {
            if (session != null && session.isOpen()) {
                try {
                    System.out.println("Now sending:" + message.getText());
                    session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Session is closed " + session.getId());
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        System.out.println("Connection established: " + session.getId());
        webSocketSessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Connection closed " + session.getId());
        webSocketSessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        if ("CLOSE".equalsIgnoreCase(message.getPayload())) {
            session.close();
        } else {
            ChatMessage chatMessage = mapper.readValue(message.getPayload(), ChatMessage.class);

            System.out.println("Received:" + message.getPayload());
            chatMessage.setUser(chatMessage.getUser() + "(" + session.getId() + ")");

            Matcher matcher = pattern.matcher(chatMessage.getText());

            if (matcher.find()) {
                System.out.println("Private message");
                String idRecipient = matcher.group(0).replace("@", "");
                for (WebSocketSession s : webSocketSessions) {
                    if (s.getId().equals(idRecipient)) {
                        chatMessage.setText("(private from " + chatMessage.getUser() + ")" + chatMessage.getText());
                        s.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessage)));
                    }
                }
            } else {
                broadcastMessage(chatMessage);
            }
        }
    }

}
