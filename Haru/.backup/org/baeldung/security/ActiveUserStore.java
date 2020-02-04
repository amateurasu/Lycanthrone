package org.baeldung.security;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
public class ActiveUserStore {

    public List<String> users = new ArrayList<>();

    public ActiveUserStore() { }
}
