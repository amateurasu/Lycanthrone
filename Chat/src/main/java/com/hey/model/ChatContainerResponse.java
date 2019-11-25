package com.hey.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatContainerResponse extends WsMessage {
    private boolean isChatGroup;
    private String sessionId;
    private List<ChatItem> chatItems;
}
