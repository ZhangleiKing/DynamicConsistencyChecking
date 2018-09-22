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

    /**
     * 如果message是CF内的，则返回对应CF的id；否则返回null
     * 如果message在CF内，则其sendEvent和receiveEvent都在InteractionOperand内
     * @param sd
     * @return
     */
    public String belongCF(SequenceDiagram sd) {
        return null;
    }

    /**
     * 获取消息发送端名称
     * @param sd
     * @return
     */
    public String getSenderName(SequenceDiagram sd) {
        return null;
    }

    /**
     * 获取消息接收端名称
     * @param sd
     * @return
     */
    public String getReceiverName(SequenceDiagram sd) {
        return null;
    }
}
