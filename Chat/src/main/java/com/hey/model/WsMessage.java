package com.hey.model;

import lombok.Data;

@Data
public abstract class WsMessage implements IWsMessage {
    protected String type;
}
