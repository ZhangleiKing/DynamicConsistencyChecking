package com.graduate.zl.log2Lts.transform;

import com.graduate.zl.common.model.Lts.LNode;
import com.graduate.zl.common.model.Lts.LTS;
import com.graduate.zl.common.model.Lts.LTransition;
import com.graduate.zl.common.model.Lts.LTransitionLabel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Vincent on 2018/9/25.
 */
public class BuildLtsFromLog {

    private String logFilePath = null;

    private LNode root = null;

    private AtomicInteger count = new AtomicInteger(1);

    public BuildLtsFromLog(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void buildLtsFromLog() {
        LNode preNode = this.root;
        String preTransitionName = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(this.logFilePath);
            br = new BufferedReader(fr);
            String logContent;
            while((logContent = br.readLine()) != null) {
                String[] parts = parseLogLine(logContent);
                LNode node = new LNode(count.getAndIncrement(), parts[1]); //以Class名为LNode名称
                if(this.root == null) {
                    this.root = node;
                    preNode = this.root;
                    preTransitionName = parts[2];
                } else {
                    preNode.getNext().put(node, new LTransition(new LTransitionLabel(preTransitionName, null, null, false), Long.parseLong(parts[3])));
                    preNode = node;
                    preTransitionName = parts[2];
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析每行log的内容
     * @param str
     * @return
     */
    private String[] parseLogLine(String str) {
        String[] parts = str.split(",");
        String[] ret = new String[4];
        for(int i=0; i<4; i++) {
            ret[i] = parts[i].split(":")[1].trim();
        }
        return ret;
    }

    public LTS getLTS() {
        LTS ret = new LTS();
        buildLtsFromLog();
        ret.buildLts(this.root);
        return ret;
    }
}
