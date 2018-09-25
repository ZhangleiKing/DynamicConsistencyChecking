package com.graduate.zl.common.model.Lts;

import lombok.Getter;
import lombok.Setter;

public class LTransitionLabel {

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private String condition;

    @Getter @Setter
    //特殊过渡标识，用于CF开始节点和START节点的transition
    private boolean isSpecTrans;

    public LTransitionLabel () {
        this(null);
    }

    public LTransitionLabel(String name) {
        this(name, null);
    }

    public LTransitionLabel(String name, String type) {
        this(name, type, null, false);
    }

    public LTransitionLabel(String name, String type, String condition, boolean isSpecTrans) {
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.isSpecTrans = isSpecTrans;
    }

    @Override
    public String toString() {
        return "LTransitionLabel{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", condition='" + condition + '\'' +
                ", isSpecTrans=" + isSpecTrans +
                '}';
    }
}
