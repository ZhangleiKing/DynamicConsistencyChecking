package com.graduate.zl.CaseCheck.ATM;

import com.graduate.zl.checkConsistency.LtsConsistency;
import com.graduate.zl.checkConsistency.ModelMappingClass;
import com.graduate.zl.common.model.Lts.LTS;
import com.graduate.zl.common.model.LtsPath.LTSNodePath;
import com.graduate.zl.common.util.LtsUtil;
import com.graduate.zl.log2Lts.LtsBasedLog;
import com.graduate.zl.sd2Lts.LtsBasedSD;

import java.util.List;
import java.util.Map;

/**
 * 验证ATM的PingEcho策略的一致性
 */
public class CheckATM {

    private static LTS codeLTS;

    private static LTS modelLTS;

    private static Map<String, List<String>> modelMapClass;

    private static void init() {
        modelLTS = LtsBasedSD.process();
        codeLTS = LtsBasedLog.process();
        modelMapClass = ModelMappingClass.getModelMappingClass();
    }

    public static void checkPingEcho() {
        init();

        List<List<LTSNodePath>> modelPaths = LtsUtil.getAllPath(modelLTS.getStart());
        System.out.println("##### Model Paths #####");
        LtsUtil.printAllPath(modelPaths);
        System.out.println("##### Code Paths #####");
        List<List<LTSNodePath>> codePaths = LtsUtil.getAllPath(codeLTS.getStart());
        LtsUtil.printAllPath(codePaths);
        boolean ret = false;
        for(List<LTSNodePath> modelPath : modelPaths) {
            if(LtsConsistency.backtrackingCheck(modelPath, codeLTS.getStart(), modelMapClass, 0)) {
                ret = true;
                break;
            }
        }
        if(ret) {
            System.out.println("Consistent!!!");
        } else {
            System.out.println("Not Consistent!!!");
        }
    }

    public static void main(String[] args) {
        checkPingEcho();
    }
}
