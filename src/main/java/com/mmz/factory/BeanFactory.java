package com.mmz.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Classname BeanFactory
 * @Description 是一个创建Bean对象的工厂
 *
 * Bean：在计算机英语中，有可重用组件的含义。例如，一个Dao是不是可以被诸多的Service层去调用？
 * JavaBean:
 *  javaBean == 实体类  ？ 并不是
 *  javaBean远大于实体类，javabean是用java语言编的可重用组件。
 *
 *  第一个：需要一个配置文件来配置我们的service和dao
 *  第二个：通过配置文件中配置的内容，反射出对象
 * @Date 2020/5/7 21:48
 * @Created by mmz
 */
public class BeanFactory {

    //定义一个Properties对象
    private static Properties props;

    //定义一个Map，用于存放我们要创建的对象，我们称之为容器
    private static Map<String,Object> beans ;

    //使用静态代码块给Properties对象赋值
    static{

        try {
            //实体化对象
            props = new Properties();
            //获取properties文件的流对象
            InputStream inputStream = BeanFactory.class.getClassLoader().getResourceAsStream("bean.properties");
            props.load(inputStream);

            //实例化容器
            beans = new HashMap<String, Object>();
            //取出配置中所有的key
            Enumeration keys =  props.keys();
            //遍历枚举
            while(keys.hasMoreElements()){
                //取出每个key
                String key  = keys.nextElement().toString();
                //根据key获取value
                String beanPath = props.getProperty(key);
                //反射创建对象
                Object value = Class.forName(beanPath).newInstance();
                //把key和value存入容器中
                beans.put(key,value);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("初始化properties失败");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //根据bean的名称获取bean对象
//    public static Object getBean(String beanName){
//        Object bean = null;
//        try {
//            String beanPath  = props.getProperty(beanName);
//            bean = Class.forName(beanPath).newInstance();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return bean;
//    }
    public static Object getBean(String beanName){
        return beans.get(beanName);
    }
}
