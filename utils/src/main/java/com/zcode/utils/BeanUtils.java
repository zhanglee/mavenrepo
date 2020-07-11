package com.zcode.utils;


import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 扩展 BeanUtils
 * @author zhanglei
 * @date 2020/5/26 17:43
 */
public class BeanUtils {

    /** 
     * 将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性 
     *  
     * @param dest 
     *            目标对象，标准的JavaBean 
     * @param orig 
     *            源对象，可为Map、标准的JavaBean
     */  
    @SuppressWarnings("rawtypes")  
    public static void applyIf(Object dest, Object orig) throws Exception {  
        try {  
            if (orig instanceof Map) {  
                Iterator names = ((Map) orig).keySet().iterator();  
                while (names.hasNext()) {  
                    String name = (String) names.next();  
                    if (PropertyUtils.isWriteable(dest, name)) {
                        Object value = ((Map) orig).get(name);  
                        if (value != null) {  
                            PropertyUtils.setSimpleProperty(dest, name, value);
                        }  
                    }  
                }  
            } else {  
                Field[] fields = orig.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {  
                    String name = fields[i].getName();
                    Field destField= ReflectionUtils.findField(dest.getClass(), name);
                    if(destField==null){
                        continue;
                    }
                    if (PropertyUtils.isReadable(orig, name) && PropertyUtils.isWriteable(dest, name)) {
                        Object value = PropertyUtils.getSimpleProperty(orig, name);
                        if (value != null) {  
                            PropertyUtils.setSimpleProperty(dest, name, value);
                        }  
                    }  
                }  
            }  
        } catch (Exception e) {  
            throw new Exception("将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性", e);  
        }  
    }  
  

    public static boolean checkObjProperty(Object orig, Map<?, ?> dest) throws Exception {  
        try {  
            Field[] fields = orig.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {  
                String name = fields[i].getName();  
                if (!dest.containsKey(name)) {  
                    if (PropertyUtils.isReadable(orig, name)) {
                        Object value = PropertyUtils.getSimpleProperty(orig, name);
                        if (value == null) {  
                            return true;  
                        }  
                    }  
                }  
            }  
            return false;  
        } catch (Exception e) {  
            throw new Exception("检查对象中的属性值为NULL异常", e);
        }  
    }

    /**
     * 属性复制
     * @param source 源对象
     * @param target 新对象类型
     * @param ignoreProperties 可忽略字段 e.g. ["id", "name"] 表示id、name字段不会被复制
     */
    public static void copyProperties(Object target, Object source, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * 根据源对象复制出一个新对象，字段类型且字段名相同的会被复制
     * @param source 源对象
     * @param clazz 新对象类型
     * @param ignoreProperties 可忽略字段 e.g. ["id", "name"] 表示id、name字段不会被复制
     * @return clazz类型的一个实例
     */
    public static <T> T clone(Object source, Class<T> clazz, String... ignoreProperties) {
        try {
            T target = clazz.newInstance();
            copyProperties(target,source,ignoreProperties);
            return target;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 根据源对象复制出一个新对象，字段类型且字段名相同的会被复制
     * @param source 源对象
     * @param clazz 新对象类型
     * @return clazz类型的一个实例
     */
    public static <T> T clone(Object source, Class<T> clazz) {
        return clone(source, clazz, (String[]) null);
    }
    
    /**
     * 获取对象字段值为null的所有字段
     * @param source 对象
     * @return 值为null的字段数组. e.g: ["id", "name"],  如没有为null的字段，返回空数组[].
     */
    public static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}