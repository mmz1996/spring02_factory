首先我们自己建立一个Client类，来模拟表现层

```java
package com.mmz.ui;

import com.mmz.service.AccountService;
import com.mmz.service.impl.AccountServiceImpl;

/**
 * @Classname Client
 * @Description 模拟表现层用于调用业务层
 * @Date 2020/5/7 21:37
 * @Created by mmz
 */
public class Client {
    public static void main(String[] args) {
        AccountService accountService = new AccountServiceImpl();
        accountService.saveAccount();
    }
}

```
可以看到表现层调用了业务层。
业务层代码如下

```java
package com.mmz.service.impl;

import com.mmz.dao.AccountDao;
import com.mmz.dao.impl.AccountDaoImpl;
import com.mmz.service.AccountService;

/**
 * @Classname AccountServiceImpl
 * @Description 账户的业务层实现类
 * @Date 2020/5/7 21:34
 * @Created by mmz
 */
public class AccountServiceImpl implements AccountService {
    private AccountDao accountDao = new AccountDaoImpl();

    public void saveAccount(){
        accountDao.saveAccount();
    }
}

```

可以看到业务层代码调用了持久层。

```java
package com.mmz.dao.impl;

import com.mmz.dao.AccountDao;

/**
 * @Classname AccountDaoImpl
 * @Description TODO
 * @Date 2020/5/7 21:36
 * @Created by mmz
 */
public class AccountDaoImpl implements AccountDao {
    public void saveAccount() {
        System.out.println("保存了账户");
    }
}

```
所以当我们运行Client的主方法的时候。相当于最后调用了持久层的saveAccount方法。

**问题所在**
上一篇博客讲到了关于耦合的问题。
那么在表现层和业务层都出现了一个问题。就是用到了关键字new

```java
AccountService accountService = new AccountServiceImpl();
private AccountDao accountDao = new AccountDaoImpl();
```

这种依赖关系，具有很强的耦合性，代码独立性很差。


**问题解决**
涉及到了Bean 与 JavaBean的概念

> * Bean：在计算机英语中，有可重用组件的含义。例如，一个Dao是不是可以被诸多的Service层去调用？
>  * JavaBean:  *  javaBean == 实体类  ？ 并不是  * 
> javaBean远大于实体类，javabean是用java语言编的可重用组件。

解决独立性差的方案

>  *  第一个：需要一个配置文件来配置我们的service和dao
>  *  第二个：通过配置文件中配置的内容，反射出对象

配置文件的内容：唯一标志 = 全限定类名（key-value）的形式
可以是xml或者是properties


**接下来**
创建一个Bean的工厂
在resources中创建一个bean.properties文件，存放key=value这种形式

```java
accountService=com.mmz.service.impl.AccountServiceImpl
accountDao=com.mmz.dao.impl.AccountDaoImpl
```


在beanFactort的类中

```java
package com.mmz.factory;

import java.io.IOException;
import java.io.InputStream;
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

    //使用静态代码块给Properties对象赋值
    static{

        try {
            //实体化对象
            props = new Properties();
            //获取properties文件的流对象
            InputStream inputStream = BeanFactory.class.getClassLoader().getResourceAsStream("bean.properties");
            props.load(inputStream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("初始化properties失败");
        }
    }

    //根据bean的名称获取bean对象
    public static Object getBean(String beanName){
        Object bean = null;
        try {
            String beanPath  = props.getProperty(beanName);
            bean = Class.forName(beanPath).newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        return bean;
    }
}

```
通过getBean，通过传入bean的名字，根据bean.properties可以得到想要的全限定类名，最后通过反射的方式来得到实例对象


**单例/多例？**
问题又出现了，当我们用for循环去生产

```java
        AccountService accountService =(AccountService)BeanFactory.getBean("accountService");
```
发现每个对象都是不同的。
我们想要改成单例模式
修改beanFactory代码
相当于在beanFactory建立了一个map类型的的beans变量，在static静态块中，我们直接创建好所有根据在在bean.properties的文件中的key与value，key为名字，value为对应的对象，都存放在beans的HashMap中。实现了单例模式

```java
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

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200507232703822.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2MzQ0Nzcx,size_16,color_FFFFFF,t_70)
**总结**
当我们用new关键字产生对象的时候，我们要主动地创建这个对象。
可是当我们用所谓的工厂创建对象的时候，找到工厂去获取这个对象。工厂对资源进行联系，解耦，解除了依赖关系。

上面我们所了解到的就是IOC的思想。
把创建对象的权利交给框架，是框架的重要特征，并非面向对象编程的专用术语，他包括依赖注入DI 和依赖查找DL
