package com.hey.model;

import lombok.Data;

import java.util.List;

@Data
public class ChatListResponse {
    private List<ChatListItem> items;
}
