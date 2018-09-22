package com.graduate.zl.sd2Lts.model.Lts;

import com.graduate.zl.sd2Lts.common.Constants;
import lombok.Getter;

import java.util.Map;

public class LTS {

    @Getter
    private LNode start;

    public LTS() {
        this.start = new LNode(0, Constants.LTS_START_NODE);
    }

    public LNode buildLts(LNode node) {
        Map<LNode, LTransition> map = this.start.getNext();
        map.put(node, new LTransition());
        this.start.setNext(map);
        return this.start;
    }
}
