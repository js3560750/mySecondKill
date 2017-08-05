package com.jinsong.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jinsong.dto.Exposer;
import com.jinsong.dto.SeckillExecution;
import com.jinsong.exception.RepeatKillException;
import com.jinsong.exception.SeckillCloseException;
import com.jinsong.exception.SeckillException;
import com.jinsong.model.Seckill;

@Service
public interface SeckillService {

	/**
	 * 查询全部的秒杀记录
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * 根据商品种类的ID查询商品
	 * @param seckillId
	 * @return
	 */
	Seckill getById(long seckillId);
	
	/**
     * 在秒杀开启时输出秒杀接口的地址，否则输出系统时间和秒杀时间
     * @param seckillId
     */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
     * 执行秒杀操作，有可能失败，有可能成功，所以要抛出我们允许的异常
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
            throws SeckillException,RepeatKillException,SeckillCloseException;
}
