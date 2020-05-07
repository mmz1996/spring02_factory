package com.mmz.ui;

import com.mmz.factory.BeanFactory;
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
//        AccountService accountService = new AccountServiceImpl();
        for(int i = 0 ;i<5;++i){
            AccountService accountService =(AccountService)BeanFactory.getBean("accountService");
            System.out.println(accountService);
            accountService.saveAccount();
        }
        }

}
