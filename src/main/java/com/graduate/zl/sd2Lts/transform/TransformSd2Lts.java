package com.graduate.zl.sd2Lts.transform;

import com.graduate.zl.sd2Lts.model.Lts.LNode;
import com.graduate.zl.sd2Lts.model.Lts.LTS;
import com.graduate.zl.sd2Lts.model.SeqDiagram.Message;
import com.graduate.zl.sd2Lts.model.SeqDiagram.SequenceDiagram;
import com.graduate.zl.sd2Lts.parse.ParseXmi;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformSd2Lts {

    private String sequenceDiagramPath;

    private AtomicInteger count = new AtomicInteger(1);

    private LNode root = null;

    public TransformSd2Lts() {
        this.sequenceDiagramPath = "";
    }

    public LNode transform() {
        ParseXmi parseXmi = new ParseXmi(this.sequenceDiagramPath);
        parseXmi.parseXmi();
        SequenceDiagram sd = parseXmi.getSequenceDiagram();
        return transform(sd);
    }

    public LNode transform(SequenceDiagram sd) {
        Map<String, Message> messageMap = sd.getMessages();
        LNode pre = root;
        int nextNumber;
        //每一条消息对应两个LNode和一个LTransition，但实际上只用创建一个LNode和一个LTransition（root节点除外）
        for(String key : messageMap.keySet()) {
            Message value = messageMap.get(key);
            if(value.belongCF(sd) != null) {
                if(this.root == null) {

                }else {

                }
            }else {
                if(this.root == null) {
                    nextNumber = count.getAndIncrement();
                    LNode node = new LNode(nextNumber, value.getSenderName(sd));
                    this.root = node;
                    
                }else {

                }
            }
        }
        return this.root;
    }

    public LTS getLTS(LNode node) {
        LTS lts = new LTS();
        lts.buildLts(node);
        return lts;
    }
}
