package com.graduate.zl.common.model.Lts;

import com.graduate.zl.sd2Lts.common.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * LTS模型数据结构
 */
public class LTS {

    @Getter @Setter
    private LNode start;

    public LTS() {
        this.start = new LNode(0, Constants.LTS_START_NODE);
    }

    public LNode buildLts(LNode node) {
        this.start.getNext().put(node, new LTransition(new LTransitionLabel(null, null, null, true)));
        return this.start;
    }
}
