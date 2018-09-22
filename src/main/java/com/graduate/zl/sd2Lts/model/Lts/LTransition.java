package com.graduate.zl.sd2Lts.model.Lts;

import lombok.Getter;
import lombok.Setter;

public class LTransition {

    @Getter @Setter
    private String tid;

    @Getter @Setter
    private String transName;

    @Getter @Setter
    private long timestamp;

    public LTransition() {

    }

    public LTransition(String tid, String transName, long timestamp) {
        this.tid = tid;
        this.transName = transName;
        this.timestamp = timestamp;
    }
}
