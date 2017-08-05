package com.jinsong.dao;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import com.jinsong.model.Seckill;

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
public class SeckillDAOTest {
	
	@Autowired
	private SeckillDAO seckillDAO;

	@Test
	public void testReduceNumber() {
		
		long seckillId=1000;
        Date date=new Date();
        int updateCount=seckillDAO.reduceNumber(seckillId,date);
        System.out.println(updateCount);
	}

	@Test
	public void testQueryById() {
		long seckillId=1000;
        Seckill seckill=seckillDAO.queryById(seckillId);
		System.out.println(seckill);
		
	}

	@Test
	public void testQueryAll() {
		List<Seckill> seckills=seckillDAO.queryAll(0,100);
        for (Seckill seckill : seckills)
        {
            System.out.println(seckill);
        }
	}

}
