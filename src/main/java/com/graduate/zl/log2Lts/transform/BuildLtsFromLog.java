package com.graduate.zl.log2Lts.transform;

import com.graduate.zl.common.model.Lts.LNode;
import com.graduate.zl.common.model.Lts.LTS;

/**
 * Created by Vincent on 2018/9/25.
 */
public class BuildLtsFromLog {

    private String logFilePath = null;

    private LNode root = null;

    public BuildLtsFromLog(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public LTS getLTS() {
        LTS ret = new LTS();
        ret.buildLts(this.root);
        return ret;
    }

    public LNode buildLtsFromLog() {
        LNode pre = this.root;
        
        return this.root;
    }
}
