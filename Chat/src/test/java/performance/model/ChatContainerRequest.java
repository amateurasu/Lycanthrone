package performance.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatContainerRequest extends WsMessage {
    private String sessionId;
}
