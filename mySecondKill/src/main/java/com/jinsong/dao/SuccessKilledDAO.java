package com.jinsong.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jinsong.model.SuccessKilled;

@Mapper
public interface SuccessKilledDAO {

	/**
	 * 插入购买明细,可过滤重复
	 * 
	 * @param seckillId
	 * @param userPhone
	 * @return 插入的行数，有主键冲突（重复秒杀）时返回0
	 */
	int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

	/**
	 * 根据秒杀商品的id查询明细SuccessKilled对象(该对象携带了Seckill秒杀产品对象)
	 * 
	 * @param seckillId
	 * @return
	 */
	SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
