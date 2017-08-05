package com.jinsong.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.jinsong.model.SuccessKilled;

/**
 * SpringBoot中进行单元测试
 * 需要添加以下两行注释
 * @RunWith(SpringRunner.class)  ：告诉Junit运行使用Spring 的单元测试支持，SpringRunner是SpringJunit4ClassRunner新的名称，只是视觉上看起来更简单了
 * @SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)	：该注解可以在一个测试类指定运行Spring Boot为基础的测试
 * 
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class SuccessKilledDAOTest {

    @Autowired
    private SuccessKilledDAO successKilledDAO;

    @Test
    public void insertSuccessKilled() throws Exception {

        long seckillId=1000L;
        long userPhone=13476191877L;
        int insertCount=successKilledDAO.insertSuccessKilled(seckillId,userPhone);
        System.out.println("insertCount="+insertCount);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long seckillId=1000L;
        long userPhone=13476191877L;
        SuccessKilled successKilled=successKilledDAO.queryByIdWithSeckill(seckillId,userPhone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());


    }

}
