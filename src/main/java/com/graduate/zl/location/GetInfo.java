package com.graduate.zl.location;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取指定目录下所有的包名、类名与方法名
 */
public class GetInfo {

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
}
