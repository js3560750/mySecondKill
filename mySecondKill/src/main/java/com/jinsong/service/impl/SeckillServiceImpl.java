package com.jinsong.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.jinsong.dao.RedisDAO;
import com.jinsong.dao.SeckillDAO;
import com.jinsong.dao.SuccessKilledDAO;
import com.jinsong.dto.Exposer;
import com.jinsong.dto.SeckillExecution;
import com.jinsong.enums.SeckillStateEnum;
import com.jinsong.exception.RepeatKillException;
import com.jinsong.exception.SeckillCloseException;
import com.jinsong.exception.SeckillException;
import com.jinsong.model.Seckill;
import com.jinsong.model.SuccessKilled;
import com.jinsong.service.SeckillService;

import ch.qos.logback.core.net.SyslogOutputStream;

@Service
public class SeckillServiceImpl implements SeckillService {

	// 日志
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillDAO seckillDAO;

	@Autowired
	private SuccessKilledDAO successKillDAO;
	
	@Autowired
	private RedisDAO redisDAO;

	// 定义MD5所用的盐
	private static final String salt = "sdfsdfds2h3iu4y98@&$Yhoihofds";

	// seckillId进行MD5加密
	private String getMD5(long seckillId) {
		String base = seckillId + "/" + salt;
		String seckillIdMD5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return seckillIdMD5;
	}

	/**
	 * 查询全部的秒杀记录
	 * 
	 * @return
	 */
	@Override
	public List<Seckill> getSeckillList() {

		// 目前数据库里只有4种商品，所以0到4
		return seckillDAO.queryAll(0, 4);
	}

	/**
	 * 根据商品种类的ID查询商品
	 * 
	 * @param seckillId
	 * @return
	 */
	@Override
	public Seckill getById(long seckillId) {
		return seckillDAO.queryById(seckillId);
	}

	/**
	 * 在秒杀开启时输出秒杀接口的地址 秒杀未开启则输出系统时间和秒杀时间
	 * 
	 * @param seckillId
	 */
	@Override
	public Exposer exportSeckillUrl(long seckillId) {

		//采用Redis缓存商品种类ID，避免频繁访问Mysql，也就是优化下面这个数据库操作
		
		//Seckill seckill = seckillDAO.queryById(seckillId);	// 获得秒杀的商品种类

		//利用Redis优化数据库操作
		//先从Redis缓存中获取seckill对象
		Seckill seckill =redisDAO.getSeckill(seckillId);
		

		if(seckill==null) {
			//如果Redis中没有，则从数据库中获取seckill对象
			seckill=seckillDAO.queryById(seckillId);
			
			if(seckill==null) {
				//如果数据库中也没有，则说明没有该商品信息，返回false
				return new Exposer(false, seckillId);
			}else {
				//如果数据库中有该商品信息，将该信息存入Redis，以便用户刷新页面后直接从Redis中获取。
				String result =redisDAO.setSeckill(seckill);
				
			}
		}else {
			System.out.println("seckil in Redis not null");
		}
		
		// 当前时间
		Date nowTime = new Date();

		// 商品属性里设定的秒杀开启时间
		Date startTime = seckill.getStartTime();

		// 商品属性里设定的秒杀关闭时间
		Date endTime = seckill.getEndTime();

		if (startTime.getTime() > nowTime.getTime() || endTime.getTime() < nowTime.getTime()) {
			// 秒杀还未开始
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}

		// 秒杀开启，返回秒杀商品的id、用给接口加密的md5
		String md5 = this.getMD5(seckillId);
		return new Exposer(true, md5, seckillId);

	}

	/**
	 * 执行秒杀操作，有可能失败，有可能成功，所以要抛出我们允许的异常
	 * 
	 * 秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
	 * 
	 * 使用注解控制事务方法的优点: 1.开发团队达成一致约定，明确标注事务方法的编程风格
	 * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部
	 * 3.不是所有的方法都需要事务，如只有一条修改操作、只读操作不要事务控制
	 * 
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	@Override
	@Transactional // spring boot默认开启了事务，在需要使用事务的地方用注解@Transactional，经过测试，确实有效！！！
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("秒杀数据被篡改了，拒绝执行秒杀操作");
		}

		// 执行秒杀逻辑:减库存+增加购买明细
		Date nowTime = new Date();

		// 这段数据库操作的代码可能在任何地方发生未知的异常，所以要整体用try/catch括起来，把所有编译期异常转化为运行期异常
		// 运行期异常一旦有错，因为开启了@Transactional，Spring会帮我们回滚
		// 之所以要把抛出的RepeatKillException等异常再catch住，是把这些具体的异常类型抛出来，
		// 否则省略这段代码的话，RepeatKillException抛出来时就显示为SeckillException，就很笼统了
		try {

			// 把用户信息插入秒杀成功表，插入成功代表秒杀成功，返回插入的行数；重复秒杀则返回0；插入失败会返回-1
			int insertCount = successKillDAO.insertSuccessKilled(seckillId, userPhone);
			if (insertCount <= 0) {
				// 重复秒杀
				throw new RepeatKillException("重复秒杀，该账号已经秒杀成功过该商品");
			} else {
				// successKilled表用户秒杀成功信息插入后，在seckill表中执行减库存操作
				// 如果返回值>1，表示更新库存的记录行数，返回值=0，表示更新失败
				int updateCount = seckillDAO.reduceNumber(seckillId, nowTime);

				if (updateCount <= 0) {
					// 没有更新库存记录，说明秒杀结束 rollback
					throw new SeckillCloseException("秒杀已经结束，插入失败");
				} else {
					// 秒杀成功,得到成功插入的明细记录,并返回成功秒杀的信息 commit
					SuccessKilled successKilled = successKillDAO.queryByIdWithSeckill(seckillId, userPhone);
					// SeckillStateEnum.SUCCESS枚举 ：SUCCESS(1,"秒杀成功"),
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
				}
			}

		} catch (RepeatKillException e1) {

			throw e1;

		} catch (SeckillCloseException e2) {

			throw e2;

		} catch (Exception e) {

			logger.error(e.getMessage());

			// 所有编译期异常转化为运行期异常
			throw new SeckillException("运行期内部错误，秒杀失败" + e.getMessage());
		}

	}

}
