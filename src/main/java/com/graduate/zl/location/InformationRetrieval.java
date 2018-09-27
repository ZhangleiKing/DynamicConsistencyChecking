package com.graduate.zl.location;

import com.graduate.zl.location.common.LocConfConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InformationRetrieval {

    private String[] keyATSWords; //可靠性策略关键词

    private Map<String, List<String>> relatedPackage;

    private Map<String, List<String>> relatedClass;

    private Map<String, List<String>> relatedMethod;

    private Map<String, String> locConf;

    public void init() {
        this.locConf = LocConfConstant.getLocConf();
        this.keyATSWords = this.locConf.get("keyATWords").split("&");
    }

    public InformationRetrieval() {
        this.relatedPackage = new HashMap<>();
        this.relatedClass = new HashMap<>();
        this.relatedMethod = new HashMap<>();
        init();
    }

    public void executeIR() {
        GetInfo info = new GetInfo();
        info.buildMapInfo();

        for(String moduleName : info.getModuleMapPackages().keySet()) {
            List<String> packageNames = info.getModuleMapPackages().get(moduleName);
            for(String packageName : packageNames) {
                for(String keyATWord : this.keyATSWords) {
                    if(packageName.toLowerCase().contains(keyATWord.toLowerCase())) {
                        if(!this.relatedPackage.containsKey(keyATWord)) {
                            this.relatedPackage.put(keyATWord, new ArrayList<>());
                        }
                        boolean needPut = true;
                        for(String str : this.relatedPackage.get(keyATWord)) {
                            if(packageName.indexOf(str) == 0) {
                                needPut = false;
                                break;
                            }
                        }
                        if(needPut) {
                            this.relatedPackage.get(keyATWord).add(packageName);
                        }
                    }
                }
            }
        }

        for(String packageName : info.getPackageMapClazzs().keySet()) {
            List<String> classNames = info.getPackageMapClazzs().get(packageName);
            for(String className : classNames) {
                for(String keyATWord : this.keyATSWords) {
                    if(className.toLowerCase().contains(keyATWord.toLowerCase())) {
                        if(!this.relatedClass.containsKey(keyATWord)) {
                            this.relatedClass.put(keyATWord, new ArrayList<>());
                        }
                        boolean needPut = true;
                        for(String str : this.relatedClass.get(keyATWord)) {
                            if(className.indexOf(str.split("@")[1]) == 0) {
                                needPut = false;
                                break;
                            }
                        }
                        if(needPut) {
                            this.relatedClass.get(keyATWord).add(packageName+"@"+className);
                        }
                    }
                }
                List<String> methodNames = info.getPackageMapClazzs().get(className);
                for(String methodName : methodNames) {
                    for(String keyATWord : this.keyATSWords) {
                        if(methodName.toLowerCase().contains(keyATWord.toLowerCase())) {
                            if(!this.relatedMethod.containsKey(keyATWord)) {
                                this.relatedMethod.put(keyATWord, new ArrayList<>());
                            }
                            this.relatedMethod.get(keyATWord).add(packageName+"@"+className+"@"+methodName);
                        }
                    }
                }
            }
        }
    }
}
