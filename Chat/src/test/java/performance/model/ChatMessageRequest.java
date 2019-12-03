package performance.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatMessageRequest extends WsMessage implements Serializable {
    private String message;
    private String sessionId;
    private List<String> usernames;
    private boolean groupChat;
}
