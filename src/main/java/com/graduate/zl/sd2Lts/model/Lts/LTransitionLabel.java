package com.graduate.zl.sd2Lts.model.Lts;

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
    private boolean isCF;

    public LTransitionLabel () {
        this(null);
    }

    public LTransitionLabel(String name) {
        this(name, null);
    }

    public LTransitionLabel(String name, String type) {
        this(name, type, null, false);
    }

    public LTransitionLabel(String name, String type, String condition, boolean isCF) {
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.isCF = isCF;
    }

    @Override
    public String toString() {
        return "LTransitionLabel{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", condition='" + condition + '\'' +
                ", isCF=" + isCF +
                '}';
    }
}
