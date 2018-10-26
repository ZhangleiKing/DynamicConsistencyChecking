package com.graduate.zl.traceability.map;

import com.graduate.zl.common.util.CommonFunc;
import com.graduate.zl.sd2Lts.common.TransformConstant;
import com.graduate.zl.sd2Lts.model.SeqDiagram.Message;
import com.graduate.zl.sd2Lts.parse.ParseXmi;
import com.graduate.zl.traceability.callGraph.handle.CallDistance;
import com.graduate.zl.traceability.common.LocConfConstant;
import com.graduate.zl.traceability.ir.InformationRetrieval;
import lombok.Getter;

import java.io.*;
import java.util.*;

/**
 * 映射
 */
public class Mapping {

    private Map<String, String> locConf;

    private Map<String, String> transformConf;

    private String locationResultFilePath;

    private String mappingResultFilePath;

    private InformationRetrieval ir;

    private CallDistance cd;

    @Getter
    private List<String> modelObjectList;

    @Getter
    private Map<String, List<String>> modelObjMapClass;

    @Getter
    private Map<String, List<String>> modelObjRelatedMsg;

    @Getter
    private Set<String> locationResult;

    private ParseXmi parseXmi;

    private void init() {
        this.locConf = LocConfConstant.getLocConf();
        this.transformConf = TransformConstant.getTransformConf();
        this.locationResultFilePath = this.locConf.get("locationResultFilePath")+this.locConf.get("correctLocationResultFileName");
        this.mappingResultFilePath = this.locConf.get("mappingResultFilePath")+this.locConf.get("mappingResultFileName");
        this.ir = new InformationRetrieval();
        this.cd = new CallDistance();
        this.modelObjectList = this.ir.getModelInfo().getObjectNameList();
        this.modelObjMapClass = new HashMap<>();
        this.locationResult = new HashSet<>();
        setLocationResult();
        this.parseXmi = new ParseXmi(transformConf.get("sequenceDiagramXmiPath") + transformConf.get("sequenceDiagramXmiName") + ".xml");
        this.modelObjRelatedMsg = new HashMap<>();
        setObjectRelatedMessage();
    }

    private void setLocationResult() {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(this.locationResultFilePath);
            br = new BufferedReader(fr);
            String content;
            while((content = br.readLine()) != null) {
                this.locationResult.add(content);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setObjectRelatedMessage() {
        this.parseXmi.parseXmi();
        for(Message msg : this.parseXmi.getSequenceDiagram().getMessageList()) {
            String sender = msg.getSenderOrReceiverName(this.parseXmi.getSequenceDiagram(), true);
            if(!this.getModelObjRelatedMsg().containsKey(sender)) {
                this.getModelObjRelatedMsg().put(sender, new ArrayList<>());
            }
            this.getModelObjRelatedMsg().get(sender).add(msg.getName());
        }
    }

    public Mapping() {
        init();
    }

    public void process() {
        //先添加信息检索的结果，依靠model对象名和消息名
        for(String objName : this.ir.getModelObjRelatedPackage().keySet()) {
            List<String> relatedPackageList = this.ir.getModelObjRelatedPackage().get(objName);
            if(relatedPackageList != null) {
                int maxMatch = Integer.MIN_VALUE;
                String bestMatch = null;
                for(String relatedPackage : relatedPackageList) {
                    int tmp = CommonFunc.longestCommonSubstring(relatedPackage.replace(".", ""), objName.replace(" ", ""));
                    if(tmp > maxMatch) {
                        maxMatch = tmp;
                        bestMatch = relatedPackage;
                    }
                }
                if(bestMatch != null) {
                    if(!this.modelObjMapClass.containsKey(objName)) {
                        this.modelObjMapClass.put(objName, new ArrayList<>());
                    }
                    List<String> clazzes = this.ir.getCodeInfo().getPackageMapClazzs().get(bestMatch);
                    if(clazzes != null) {
                        for(String clazz : clazzes) {
                            String tmp = bestMatch+"."+clazz;
                            // mapping的结果需要基于locate的结果
                            if(this.locationResult.contains(tmp)) {
                                this.modelObjMapClass.get(objName).add(tmp);
                            }
                        }
                    }
                }
            }
        }
        for(String objName : this.ir.getModelObjRelatedClass().keySet()) {
            List<String> relatedClassList = this.ir.getModelObjRelatedClass().get(objName);
            for(String clazz : relatedClassList) {
                if(!this.getModelObjMapClass().containsKey(objName)) {
                    this.getModelObjMapClass().put(objName, new ArrayList<>());
                }
                if(!this.getModelObjMapClass().get(objName).contains(clazz)) {
                    if(this.locationResult.contains(clazz)) {
                        this.getModelObjMapClass().get(objName).add(clazz);
                    }
                }
            }
        }

        for(String objName : this.ir.getModelObjRelatedMethod().keySet()) {
            List<String> relatedMethodList = this.ir.getModelObjRelatedClass().get(objName);
            for(String fullMethod : relatedMethodList) {
                String clazz = fullMethod.split(":")[0];
                if(!this.getModelObjMapClass().containsKey(objName)) {
                    this.getModelObjMapClass().put(objName, new ArrayList<>());
                }
                if(!this.getModelObjMapClass().get(objName).contains(clazz)) {
                    if(this.locationResult.contains(clazz)) {
                        this.getModelObjMapClass().get(objName).add(clazz);
                    }
                }
            }
        }
    }

    public void recordMappingResult() {
        File mappingFile = new File(this.mappingResultFilePath);
        FileWriter writer = null;
        BufferedWriter br = null;
        try {
            if(!mappingFile.exists()) {
                mappingFile.createNewFile();
            }
            writer = new FileWriter(mappingFile);
            br = new BufferedWriter(writer);
            for(String objName : this.getModelObjMapClass().keySet()) {
                br.write("<"+objName+">"+"\r\n");
                List<String> relatedClasses = this.getModelObjMapClass().get(objName);
                for(String clazz : relatedClasses) {
                    br.write(clazz+"\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("The result of mapping is done!");
    }


    public static void main(String[] args) {
        Mapping mapping = new Mapping();
//        for(String str : mapping.getLocationResult()) {
//            System.out.println("dd: "+str);
//        }
        mapping.process();
        for (String objName : mapping.getModelObjMapClass().keySet()) {
            System.out.println("modelObject: " + objName);
            for(String clazz : mapping.getModelObjMapClass().get(objName)) {
                System.out.println(clazz);
            }
        }
        mapping.recordMappingResult();
//        for(String objName : mapping.getModelObjRelatedMsg().keySet()) {
//            System.out.println("objName: "+objName);
//            for(String msg : mapping.getModelObjRelatedMsg().get(objName)) {
//                System.out.println(msg);
//            }
//        }
    }
}
