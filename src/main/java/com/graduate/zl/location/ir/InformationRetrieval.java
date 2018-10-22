package com.graduate.zl.location.ir;

import com.graduate.zl.common.util.CommonFunc;
import com.graduate.zl.location.common.LocConfConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取制定项目的包名、类名、方法名
 */
public class InformationRetrieval {

    private String[] keyATSWords; //可靠性策略关键词

    @Getter @Setter
    //依靠可靠性策略检测出的项目相关包路径
    private Map<String, List<String>> atsRelatedPackage;

    @Getter @Setter
    //依靠可靠性策略检测出的项目相关类名
    private Map<String, List<String>> atsRelatedClass;

    @Getter @Setter
    //依靠可靠性策略检测出的项目相关方法名
    private Map<String, List<String>> atsRelatedMethod;

    @Getter @Setter
    //依靠模型对象名称检测出的项目相关包路径
    private Map<String, List<String>> modelObjRelatedPackage;

    @Getter @Setter
    //依靠模型对象名称检测出的项目相关类名
    private Map<String, List<String>> modelObjRelatedClass;

    @Getter @Setter
    //依靠模型对象名称检测出的项目相关方法名
    private Map<String, List<String>> modelObjRelatedMethod;

    @Getter @Setter
    //依靠模型中的消息名称检测出的项目相关包路径
    private Map<String, List<String>> modelMsgRelatedPackage;

    @Getter @Setter
    //依靠模型模型中的消息名称检测出的项目相关类名
    private Map<String, List<String>> modelMsgRelatedClass;

    @Getter @Setter
    //依靠模型模型中的消息名称检测出的项目相关方法名
    private Map<String, List<String>> modelMsgRelatedMethod;

    private Map<String, String> locConf;

    private int matchLevel;

    private ModelInfo modelInfo;

    public void init() {
        this.locConf = LocConfConstant.getLocConf();
        this.matchLevel = Integer.parseInt(this.locConf.get("module_match_level"));
        this.keyATSWords = this.locConf.get("keyATWords").split("&");
        this.atsRelatedPackage = new HashMap<>();
        this.atsRelatedClass = new HashMap<>();
        this.atsRelatedMethod = new HashMap<>();
        this.modelInfo = new ModelInfo();
    }

    public InformationRetrieval() {
        init();
    }

    /**
     * 执行信息检索
     */
    public void executeIR() {
        CodeInfo info = new CodeInfo();
        info.buildMapInfo();

        for(String moduleName : info.getModuleMapPackages().keySet()) {
            List<String> packageNames = info.getModuleMapPackages().get(moduleName);
            for(String pkName : packageNames) {
                String packageName = pkName.toLowerCase();
                for(String kaw : this.keyATSWords) {
                    String keyATWord = kaw.toLowerCase();
                    if(packageName.contains(keyATWord)) {
                        if(!this.atsRelatedPackage.containsKey(keyATWord)) {
                            this.atsRelatedPackage.put(keyATWord, new ArrayList<>());
                        }
                        boolean needPut = true;
                        for(int i=0;i<this.atsRelatedPackage.get(keyATWord).size();i++) {
                            String str = this.atsRelatedPackage.get(keyATWord).get(i);
                            if(packageName.indexOf(str) == 0) {
                                needPut = false;
                                break;
                            }else if(str.indexOf(packageName) == 0) {
                                this.atsRelatedPackage.get(keyATWord).remove(str);
                                i--;
                            }
                        }
                        if(needPut) {
                            this.atsRelatedPackage.get(keyATWord).add(packageName);
                        }
                    }
                }
                for(String mon : this.modelInfo.getObjectNameList()) {
                    String modelObjName = mon.toLowerCase();
                    if(CommonFunc.match(packageName, modelObjName, this.matchLevel)) {
                        if(!this.modelObjRelatedPackage.containsKey(modelObjName)) {
                            this.modelObjRelatedPackage.put(modelObjName, new ArrayList<>());
                        }
                        this.modelObjRelatedPackage.get(modelObjName).add(packageName);
                    }
                }
                for(String mmn : this.modelInfo.getMessageNameList()) {
                    String modelMsgName = mmn.toLowerCase();
                    if(CommonFunc.match(packageName, modelMsgName, this.matchLevel)) {
                        if(!this.modelMsgRelatedPackage.containsKey(modelMsgName)) {
                            this.modelMsgRelatedPackage.put(modelMsgName, new ArrayList<>());
                        }
                        this.modelMsgRelatedPackage.get(modelMsgName).add(packageName);
                    }
                }
            }
        }

        for(String packageName : info.getPackageMapClazzs().keySet()) {
            List<String> classNames = info.getPackageMapClazzs().get(packageName);
            for(String cn : classNames) {
                String className = cn.toLowerCase();
                for(String kaw : this.keyATSWords) {
                    String keyATWord = kaw.toLowerCase();
                    // if(className.toLowerCase().contains(keyATWord.toLowerCase()))
                    if(CommonFunc.match(className, keyATWord, this.matchLevel)) {
                        if(!this.atsRelatedClass.containsKey(keyATWord)) {
                            this.atsRelatedClass.put(keyATWord, new ArrayList<>());
                        }
                        this.atsRelatedClass.get(keyATWord).add(packageName+"@"+className);
                    }
                }
                for(String mon : modelInfo.getObjectNameList()) {
                    String modelObjectName = mon.toLowerCase();
                    if(CommonFunc.match(className, modelObjectName, this.matchLevel)) {
                        if(!this.modelObjRelatedClass.containsKey(modelObjectName)) {
                            this.modelObjRelatedClass.put(modelObjectName, new ArrayList<>());
                        }
                        this.modelObjRelatedClass.get(modelObjectName).add(className);
                    }
                }
                for(String mmn : modelInfo.getMessageNameList()) {
                    String modelMessageName = mmn.toLowerCase();
                    if(CommonFunc.match(className, modelMessageName, this.matchLevel)) {
                        if(!this.modelMsgRelatedClass.containsKey(modelMessageName)) {
                            this.modelMsgRelatedClass.put(modelMessageName, new ArrayList<>());
                        }
                        this.modelMsgRelatedClass.get(modelMessageName).add(className);
                    }
                }

                if(info.getClazzMapMethods().containsKey(className)) {
                    List<String> methodNames = info.getClazzMapMethods().get(className);
                    for(String mn : methodNames) {
                        String methodName = mn.toLowerCase();
                        for(String kaw : this.keyATSWords) {
                            String keyATWord = kaw.toLowerCase();
                            // if(methodName.toLowerCase().contains(keyATWord.toLowerCase()))
                            if(CommonFunc.match(methodName, keyATWord, this.matchLevel)) {
                                if(!this.atsRelatedMethod.containsKey(keyATWord)) {
                                    this.atsRelatedMethod.put(keyATWord, new ArrayList<>());
                                }
                                this.atsRelatedMethod.get(keyATWord).add(packageName+"@"+className+"@"+methodName);
                            }
                        }
                        for(String mon : modelInfo.getObjectNameList()) {
                            String modelObjectName = mon.toLowerCase();
                            if(CommonFunc.match(methodName, modelObjectName, this.matchLevel)) {
                                if(!this.modelObjRelatedMethod.containsKey(modelObjectName)) {
                                    this.modelObjRelatedMethod.put(modelObjectName, new ArrayList<>());
                                }
                                this.modelObjRelatedMethod.get(modelObjectName).add(className);
                            }
                        }
                        for(String mmn : modelInfo.getMessageNameList()) {
                            String modelMessageName = mmn.toLowerCase();
                            if(CommonFunc.match(methodName, modelMessageName, this.matchLevel)) {
                                if(!this.modelMsgRelatedMethod.containsKey(modelMessageName)) {
                                    this.modelMsgRelatedMethod.put(modelMessageName, new ArrayList<>());
                                }
                                this.modelMsgRelatedMethod.get(modelMessageName).add(className);
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

        for(String keyWord : ir.getAtsRelatedPackage().keySet()) {
            System.out.println("#Package Level# <keyWord: "+keyWord+">");
            List<String> packageNames = ir.getAtsRelatedPackage().get(keyWord);
            if(packageNames != null) {
                for(String packageName : packageNames) {
                    System.out.println(packageName);
                }
            }
        }

        for(String keyWord : ir.getAtsRelatedClass().keySet()) {
            System.out.println("#Class Level#<keyWord: "+keyWord+">");
            List<String> classNames = ir.getAtsRelatedClass().get(keyWord);
            if(classNames != null) {
                for(String className : classNames) {
                    System.out.println(className);
                }
            }
        }

        for(String keyWord : ir.getAtsRelatedMethod().keySet()) {
            System.out.println("#Method Level#<keyWord: "+keyWord+">");
            List<String> methodNames = ir.getAtsRelatedMethod().get(keyWord);
            if(methodNames != null) {
                for(String methodName : methodNames) {
                    System.out.println(methodName);
                }
            }
        }
    }
}
