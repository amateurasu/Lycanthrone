package com.hey.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class NewChatSessionResponse extends WsMessage implements Serializable {
    private String sessionId;
}
