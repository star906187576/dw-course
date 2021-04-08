package com.dw.dao.impl;

import com.dw.dao.HelloDao;
import org.springframework.stereotype.Repository;

@Repository
public class HelloDaoImpl implements HelloDao {
    @Override
    public String findHello() {
        //这里模拟从数据库中查询出配置好的欢迎语句
        return "欢迎来到网上商城";
    }
}
