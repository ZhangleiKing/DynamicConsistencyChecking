package com.graduate.zl.location;

import com.graduate.zl.location.common.LocConfConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InformationRetrieval {

    private String[] keyATSWords; //可靠性策略关键词

    @Getter @Setter
    private Map<String, List<String>> relatedPackage;

    @Getter @Setter
    private Map<String, List<String>> relatedClass;

    @Getter @Setter
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
                        for(int i=0;i<this.relatedPackage.get(keyATWord).size();i++) {
                            String str = this.relatedPackage.get(keyATWord).get(i);
                            if(packageName.indexOf(str) == 0) {
                                needPut = false;
                                break;
                            }else if(str.indexOf(packageName) == 0) {
                                this.relatedPackage.get(keyATWord).remove(str);
                                i--;
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
                if(info.getPackageMapClazzs().containsKey(className)) {
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

    public static void main(String[] args) {
        InformationRetrieval ir = new InformationRetrieval();
        ir.executeIR();

        for(String keyWord : ir.getRelatedPackage().keySet()) {
            System.out.println("#Package Level# <keyWord: "+keyWord+">");
            List<String> packageNames = ir.getRelatedPackage().get(keyWord);
            if(packageNames != null) {
                for(String packageName : packageNames) {
                    System.out.println(packageName);
                }
            }
        }

        for(String keyWord : ir.getRelatedClass().keySet()) {
            System.out.println("#Class Level#<keyWord: "+keyWord+">");
            List<String> classNames = ir.getRelatedClass().get(keyWord);
            if(classNames != null) {
                for(String className : classNames) {
                    System.out.println(className);
                }
            }
        }

        for(String keyWord : ir.getRelatedClass().keySet()) {
            System.out.println("#Method Level#<keyWord: "+keyWord+">");
            List<String> methodNames = ir.getRelatedMethod().get(keyWord);
            if(methodNames != null) {
                for(String methodName : methodNames) {
                    System.out.println(methodName);
                }
            }
        }
    }
}
