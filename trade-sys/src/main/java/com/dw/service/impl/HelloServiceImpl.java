package com.dw.service.impl;

import com.dw.dao.HelloDao;
import com.dw.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService {

    @Autowired
    private HelloDao helloDao;

    @Override
    public String getHello() {
        return helloDao.findHello();
    }
}
