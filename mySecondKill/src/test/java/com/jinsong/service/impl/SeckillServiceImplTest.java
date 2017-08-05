package com.jinsong.service.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.jinsong.dto.Exposer;
import com.jinsong.dto.SeckillExecution;
import com.jinsong.exception.RepeatKillException;
import com.jinsong.exception.SeckillCloseException;
import com.jinsong.model.Seckill;
import com.jinsong.service.SeckillService;

/**
 * SpringBoot中进行单元测试 需要添加以下两行注释
 * 
 * @RunWith(SpringRunner.class) ：告诉Junit运行使用Spring
 *                              的单元测试支持，SpringRunner是SpringJunit4ClassRunner新的名称，只是视觉上看起来更简单了
 * @SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT) ：该注解可以在一个测试类指定运行Spring
 *                                                            Boot为基础的测试
 * 
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SeckillServiceImplTest {

	// 日志
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	@Test
	public void getSeckillList() throws Exception {
		List<Seckill> seckills = seckillService.getSeckillList();
		System.out.println(seckills);

	}

	@Test
	public void getById() throws Exception {

		long seckillId = 1000;
		Seckill seckill = seckillService.getById(seckillId);
		System.out.println(seckill);
	}

	@Test // 完整逻辑代码测试，注意可重复执行
	public void testSeckillLogic() throws Exception {
		long seckillId = 1000;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed()) {

			System.out.println(exposer);

			long userPhone = 134761909L;
			String md5 = exposer.getMd5();

			try {
				SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
				System.out.println(seckillExecution);
			} catch (RepeatKillException e) {
				e.printStackTrace();
			} catch (SeckillCloseException e1) {
				e1.printStackTrace();
			}
		} else {
			// 秒杀未开启
			System.out.println(exposer);
		}
	}

	@Test
	public void executeSeckill() throws Exception {

		long seckillId = 1000;
		String md5 = "bf204e2683e7452aa7db1a50b5713bae";

	}

}
