package vn.elite.fundamental.web.socket;

import lombok.Data;

@Data
public class Message {
    private String from;
    private String to;
    private String content;
}
