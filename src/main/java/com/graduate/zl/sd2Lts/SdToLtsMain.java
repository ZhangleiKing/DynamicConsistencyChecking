package com.graduate.zl.sd2Lts;

import com.graduate.zl.common.model.Lts.LTS;
import com.graduate.zl.common.model.LtsPath.LTSNodePath;
import com.graduate.zl.common.util.LtsUtil;
import com.graduate.zl.sd2Lts.common.TransformConstant;
import com.graduate.zl.sd2Lts.transform.TransformSd2Lts;

import java.util.List;
import java.util.Map;

/**
 * 将时序图转换为LTS的主入口
 */
public class SdToLtsMain {

    private static Map<String, String> transformConf = null;

    private static void init() {
        transformConf = TransformConstant.getTransformConf();
    }

    public static void main(String[] args) {
        init();

        String sequenceDiagramFullPath = transformConf.get("sequenceDiagramXmiPath") + transformConf.get("sequenceDiagramXmiName") + ".xml";
        TransformSd2Lts trans = new TransformSd2Lts(sequenceDiagramFullPath);
        LTS lts = trans.getLTS();
        List<List<LTSNodePath>> allPaths = LtsUtil.getAllPath(lts.getStart());
        LtsUtil.printAllPath(allPaths);
    }

}
