package com.graduate.zl.sd2Lts.model.Lts;

import com.graduate.zl.sd2Lts.common.util.RandomId;
import lombok.Getter;
import lombok.Setter;

public class LTransition {

    @Getter @Setter
    private String tid;

    @Getter @Setter
    private LTransitionLabel transLabel;

    @Getter @Setter
    private long timestamp;

    public LTransition() {
        this(null);
    }

    public LTransition(LTransitionLabel tLabel) {
        this(RandomId.getRandomId(), tLabel, System.currentTimeMillis());
    }

    public LTransition(String tid, LTransitionLabel tLabel, long timestamp) {
        this.tid = tid;
        this.transLabel = tLabel;
        this.timestamp = timestamp;
    }
}
