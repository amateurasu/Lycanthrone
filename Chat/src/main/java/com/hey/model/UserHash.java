package com.hey.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserHash {
    private String userId;
    private String fullName;
}
