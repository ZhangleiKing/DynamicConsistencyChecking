package com.graduate.zl.location;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取指定目录下所有的包名、类名与方法名
 */
public class GetInfo {

    private final static String projectDirPath = "E:\\Projects\\Git\\zookeeper";

    private Map<String, List<String>> packageMapClazzs;

    private Map<String, List<String>> clazzMapMethods;

    /**
     * 获取每个package下所有的java文件名
     * @param folder
     * @param packageName
     */
    public void getPackageClazzs(File folder, String packageName) {
        File[] files = folder.listFiles();
        for(int i=0, size = files.length; i<size; i++) {
            File file = files[i];
            if(file.isDirectory()) {
                if (packageName==null || packageName.length()==0)
                    getPackageClazzs(file, packageName + file.getName());
                else
                    getPackageClazzs(file, packageName + "." + file.getName());
            } else {
                if(!packageMapClazzs.containsKey(packageName)) {
                    packageMapClazzs.put(packageName, new ArrayList<>());
                }
                String fileName = file.getName();
                packageMapClazzs.get(packageName).add(fileName.substring(0, fileName.length()-5));
            }
        }
    }

    public List<String> getMathodsName(String clazzName) {
        List<String> ret = new ArrayList<>();
        try {
            Object obj = Class.forName(clazzName);
            Method[] methods = obj.getClass().getDeclaredMethods();
            for(Method method : methods) {
                ret.add(method.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) {
        File root = new File(projectDirPath + "\\src");
        File[] files = root.listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                System.out.println("dir: "+file.getName());
            }else {
                System.out.println("file: "+file.getName());
            }
        }
    }
}
