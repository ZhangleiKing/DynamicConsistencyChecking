package com.graduate.zl.sd2Lts.model.Lts;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class LNode {

    @Getter @Setter
    private int number;

    @Getter @Setter
    private String label;

    @Getter @Setter
    private Map<LNode, LTransition> next;

    public LNode (int number, String label) {
        this.number = number;
        this.label = label;
        this.next = new HashMap<>();
    }
}
