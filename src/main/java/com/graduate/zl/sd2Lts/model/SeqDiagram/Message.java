package com.graduate.zl.sd2Lts.model.SeqDiagram;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Vincent on 2018/7/30.
 */
public class Message {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String sendEvent;

    @Getter @Setter
    private String receiveEvent;

    public Message(String id, String name, String sendEvent, String receiveEvent) {
        this.id = id;
        this.name = name;
        this.sendEvent = sendEvent;
        this.receiveEvent = receiveEvent;
    }
}
