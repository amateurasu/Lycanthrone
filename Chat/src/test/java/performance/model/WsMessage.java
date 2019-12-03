package performance.model;

import lombok.*;

@Data
public abstract class WsMessage implements IWsMessage {
    protected String type;
}
