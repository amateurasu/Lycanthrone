package io.vertx.howtos.graphql;

import lombok.Data;

import java.util.UUID;

@Data
class Task {
    String id;
    boolean completed;
    private String description;

    Task(String description) {
        id = UUID.randomUUID().toString(); // <1>
        this.description = description;
    }
}
