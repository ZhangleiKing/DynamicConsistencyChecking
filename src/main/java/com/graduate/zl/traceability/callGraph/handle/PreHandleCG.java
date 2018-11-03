package com.graduate.zl.traceability.callGraph.handle;

import com.graduate.zl.traceability.callGraph.codeParse.CallGraphMainEntry;
import com.graduate.zl.traceability.common.LocConfConstant;
import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 预处理
 */
public class PreHandleCG {

    private Map<String, String> locConf;

    private String methodCallFilePath;

    @Getter
    private Map<String, List<String>> methodCallMap;

    @Getter
    private Map<String, Integer> methodCallNodes;

    private AtomicInteger count = new AtomicInteger(1);



    private void init() {
        this.locConf = LocConfConstant.getLocConf();
        this.methodCallFilePath = this.locConf.get("methodCallFilePath") + this.locConf.get("methodCallFileName");
        this.methodCallMap = new HashMap<>();
        this.methodCallNodes = new HashMap<>();

    }

    public PreHandleCG() {
        init();
        preHandleCG();
    }

    public void preHandleCG() {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(this.methodCallFilePath);
            br = new BufferedReader(fr);
            String logContent;
            while ((logContent = br.readLine()) != null) {
                String methodCaller = logContent.split("CALL")[0].trim();
                String methodCallee = logContent.split("CALL")[1].trim();
                if(!this.methodCallMap.containsKey(methodCaller)) {
                    this.methodCallMap.put(methodCaller, new ArrayList<>());
                }
                if(!this.methodCallMap.get(methodCaller).contains(methodCallee)) {
                    this.methodCallMap.get(methodCaller).add(methodCallee);
                }
                if(!this.methodCallNodes.containsKey(methodCaller)) {
                    this.methodCallNodes.put(methodCaller, count.getAndIncrement());
                }
                if(!this.methodCallNodes.containsKey(methodCallee)) {
                    this.methodCallNodes.put(methodCallee, count.getAndIncrement());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PreHandleCG ph = new PreHandleCG();
        for(String key : ph.getMethodCallNodes().keySet()) {
            System.out.println(key + ": "+ ph.getMethodCallNodes().get(key));
        }
    }
}