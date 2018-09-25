package com.graduate.zl.sd2Lts.transform;

import com.graduate.zl.sd2Lts.common.Constants;
import com.graduate.zl.common.model.Lts.LNode;
import com.graduate.zl.common.model.Lts.LTS;
import com.graduate.zl.common.model.Lts.LTransition;
import com.graduate.zl.common.model.Lts.LTransitionLabel;
import com.graduate.zl.sd2Lts.model.SeqDiagram.*;
import com.graduate.zl.sd2Lts.parse.ParseXmi;

import java.util.List;
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
        Map<String, Message> messageMap = sd.getMessageMap();
        LNode pre = root;
        int nextNumber;
        //每一条消息对应两个LNode和一个LTransition，但实际上只用创建一个LNode和一个LTransition（root节点除外）
        for(int k=0, size=sd.getMessageList().size(); k<size; k++) {
            Message message = sd.getMessageList().get(k);
            //如果当前消息属于CF片段
            String[] belongCFAndIO = message.getBelongedCF(sd);
            if(belongCFAndIO[0] != null) {
                CombinedFragment curCF = sd.getCombinedFragments().get(belongCFAndIO[0]);
                String cfType = curCF.getType();
                if(this.root == null) {
                    nextNumber = count.getAndIncrement();
                    LNode firstNode = new LNode(nextNumber, message.getSenderOrReceiverName(sd, true));
                    if (cfType.equals(Constants.CF_TYPE_OPT)) {
                        pre = handleOptCF(firstNode, curCF, sd, k);
                    } else if(cfType.equals(Constants.CF_TYPE_ALT)) {
                        pre = handleAltCF(firstNode, curCF, sd, k);
                    }
                    this.root = firstNode;
                }else {
                    if (cfType.equals(Constants.CF_TYPE_OPT)) {
                        pre = handleOptCF(pre, curCF, sd, k); //OPT片段处理后返回尾节点，并使pre指向当前尾节点
                    } else if(cfType.equals(Constants.CF_TYPE_ALT)) {
                        pre = handleAltCF(pre, curCF, sd, k);
                    }
                    Message nextMessage = sd.getMessageList().get(k+1);
                    LNode gdFin = new LNode(count.getAndIncrement(), nextMessage.getSenderOrReceiverName(sd, true));
                    pre.getNext().put(gdFin, new LTransition(new LTransitionLabel("FIN", cfType, null, true)));
                    pre = gdFin;
                }
            }else {
                if(this.root == null) {
                    nextNumber = count.getAndIncrement();
                    LNode firstNode = new LNode(nextNumber, message.getSenderOrReceiverName(sd, true));
                    this.root = firstNode;
                    nextNumber = count.getAndIncrement();
                    LNode secondNode = new LNode(nextNumber, message.getSenderOrReceiverName(sd, false));
                    firstNode.getNext().put(secondNode, new LTransition(new LTransitionLabel(message.getName(), Constants.MESSAGE_TYPE, null, false)));
                    pre = secondNode;
                }else {
                    nextNumber = count.getAndIncrement();
                    LNode nextNode = new LNode(nextNumber, message.getSenderOrReceiverName(sd, false));
                    pre.getNext().put(nextNode, new LTransition(new LTransitionLabel(message.getName(), Constants.MESSAGE_TYPE, null, false)));
                    pre = nextNode;
                }
            }
        }
        return this.root;
    }

    /**
     * 将OPT片段处理为LTS
     * @param cfStart
     * @param cf
     * @param sd
     * @param cdMessageStartPos
     * @return
     */
    private LNode handleOptCF(LNode cfStart, CombinedFragment cf, SequenceDiagram sd, int cdMessageStartPos) {
        List<Message> messageList = sd.getMessageList();

        InteractionOperand iaOpe = cf.getOperandList().get(0); //OPT片段只有1个InteractionOperand
        List<OccurrenceSpecificationFragment> curOSF = iaOpe.getOsFragments();
        int messageNumber = curOSF.size() / 2;
        LNode gd1 = new LNode(count.getAndIncrement(), cfStart.getLabel()), mv = gd1;
        cfStart.getNext().put(gd1, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_OPT, "TRUE", true)));

        for(int i=cdMessageStartPos; i<messageNumber+cdMessageStartPos; i++) {
            Message curMessage = messageList.get(i);
            OccurrenceSpecificationFragment receiveOsf = curOSF.get((i-cdMessageStartPos)*2+1);
            String receiveName = sd.getLifelines().get(receiveOsf.getCoveredId()).getName();
            LNode curNode = new LNode(count.getAndIncrement(), receiveName);
            mv.getNext().put(curNode, new LTransition(new LTransitionLabel(curMessage.getName(), Constants.MESSAGE_TYPE, null, false)));
            mv = curNode;
        }
        LNode gd2 = new LNode(count.getAndIncrement(), cfStart.getLabel());
        cfStart.getNext().put(gd2, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_OPT, "FALSE", true)));
        gd2.getNext().put(mv, new LTransition(new LTransitionLabel(null, null, null, false)));
        return mv;
    }

    /**
     * 处理Alt片段
     * @param cfStart
     * @param cf
     * @param sd
     * @param cdMessageStartPos
     * @return
     */
    private LNode handleAltCF(LNode cfStart, CombinedFragment cf, SequenceDiagram sd, int cdMessageStartPos) {
        List<Message> messageList = sd.getMessageList();
        List<InteractionOperand> curIaOpe = cf.getOperandList(); // ALT片段有2个InteractionOperand

        int osf1MsgNumber = curIaOpe.get(0).getOsFragments().size()/2,
                osf2Msg2Number = curIaOpe.get(1).getOsFragments().size()/2;
        LNode gd1 = new LNode(count.getAndIncrement(), cfStart.getLabel()), mv = gd1;
        cfStart.getNext().put(gd1, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_ALT, curIaOpe.get(0).getGuard().getBody(), true)));
        Message curMessage; LNode curNode;
        for(int i=cdMessageStartPos; i<cdMessageStartPos+osf1MsgNumber; i++) {
            curMessage = messageList.get(i);
            OccurrenceSpecificationFragment receiveOsf = curIaOpe.get(0).getOsFragments().get((i-cdMessageStartPos)*2+1);
            String receiveName = sd.getLifelines().get(receiveOsf.getCoveredId()).getName();
            curNode = new LNode(count.getAndIncrement(), receiveName);
            mv.getNext().put(curNode, new LTransition(new LTransitionLabel(curMessage.getName(), Constants.MESSAGE_TYPE, null, false)));
            mv = curNode;
        }
        LNode altTail = new LNode(count.getAndIncrement(), "ALT_CF_END");
        mv.getNext().put(altTail, new LTransition(new LTransitionLabel(null, Constants.CF_TYPE_ALT, null, true)));

        LNode gd2 = new LNode(count.getAndIncrement(), cfStart.getLabel()); mv = gd2;
        cfStart.getNext().put(gd2, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_ALT, curIaOpe.get(1).getGuard().getBody(), true)));
        for(int i=cdMessageStartPos+osf1MsgNumber; i<cdMessageStartPos+osf1MsgNumber+osf2Msg2Number; i++) {
            curMessage = messageList.get(i);
            OccurrenceSpecificationFragment receiveOsf = curIaOpe.get(1).getOsFragments().get((i-cdMessageStartPos)*2+1);
            String receiveName = sd.getLifelines().get(receiveOsf.getCoveredId()).getName();
            curNode = new LNode(count.getAndIncrement(), receiveName);
            mv.getNext().put(curNode, new LTransition(new LTransitionLabel(curMessage.getName(), Constants.MESSAGE_TYPE, null, false)));
            mv = curNode;
        }
        mv.getNext().put(altTail, new LTransition(new LTransitionLabel(null, Constants.CF_TYPE_ALT, null, true)));
        return altTail;
    }

    public LTS getLTS(LNode node) {
        LTS lts = new LTS();
        lts.buildLts(node);
        return lts;
    }
}
