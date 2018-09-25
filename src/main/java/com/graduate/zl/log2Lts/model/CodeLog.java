package com.graduate.zl.log2Lts.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Vincent on 2018/9/25.
 */
public class CodeLog {

    @Getter @Setter
    private String threadName;

    @Getter @Setter
    private String className;

    @Getter @Setter
    private String methodName;

    @Getter @Setter
    private long timestamp;

    public CodeLog() {

    }
    public CodeLog(String threadName, String className, String methodName) {
        this(threadName, className, methodName, System.currentTimeMillis());
    }

    public CodeLog(String threadName, String className, String methodName, long timestamp) {
        this.threadName = threadName;
        this.className = className;
        this.methodName = methodName;
        this.timestamp = timestamp;
    }
}
