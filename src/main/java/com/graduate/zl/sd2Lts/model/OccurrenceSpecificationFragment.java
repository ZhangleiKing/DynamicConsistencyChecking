package com.graduate.zl.sd2Lts.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Vincent on 2018/7/30.
 */
public class OccurrenceSpecificationFragment {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String coveredId;

    public OccurrenceSpecificationFragment(String id, String coveredId) {
        this.id = id;
        this.coveredId = coveredId;
    }
}
