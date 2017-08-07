package com.jinsong.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.jinsong.model.Seckill;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

/**
 * 通过Jedis接口使用Redis数据库
 * 
 * 注意：往Redis中放的对象一定要序列化之后再放入，参考http://www.cnblogs.com/yaobolove/p/5632891.html
 * 序列化的目的是将一个实现了Serializable接口的对象转换成一个字节序列,可以。 把该字节序列保存起来(例如:保存在一个文件里),
 * 以后可以随时将该字节序列恢复为原来的对象。
 * 
 * Redis 缓存对象时需要将其序列化，而何为序列化，实际上就是将对象以字节形式存储。这样，不管对象的属性是字符串、整型还是图片、视频等二进制类型，
 * 都可以将其保存在字节数组中。对象序列化后便可以持久化保存或网络传输。需要还原对象时，只需将字节数组再反序列化即可。
 * 
 * 因为要在项目中用到，所以要添加@Service，把这个做成一个服务
 * 
 * 因为要初始化连接池JedisPool，所以要implements InitializingBean并调用默认的
 * afterPropertiesSet()方法
 * 
 * @author 18894
 *
 */
@Service
public class RedisDAO implements InitializingBean {

	// 连接池
	private JedisPool jedisPool;

	// protostuff序列化工具用到的架构
	// 对于对象，可以用RuntimeSchema来生成schema(架构)通过反射在运行时缓存和使用
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		// 初始化连接池，连接Redis中第8个数据库，Redis端口是6379，默认配置Redis最多有16个数据库
		jedisPool = new JedisPool("redis://localhost:6379/8");

	}

	// 将seckill对象序列化后存入Redis
	public String setSeckill(Seckill seckill) {
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckill.getSeckillId();
				// protostuff工具
				// 将seckill对象序列化成字节数组
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
						LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				// 超时缓存
				int timeout = 60 * 60; // 1小时
				// 设置key对应的字符串value，并给一个超期时间
				String result = jedis.setex(key.getBytes(), timeout, bytes);

				return result;

			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	// 从Redis中得到seckill对象（反序列化之后）
	public Seckill getSeckill(long seckillId) {
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId;
				byte[] bytes = jedis.get(key.getBytes());
				// 从Redis缓存中反序列化后获取seckill对象
				if (bytes != null) {
					Seckill seckill = schema.newMessage();
					// 反序列化
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					// 返回反序列化之后的seckill对象
					return seckill;
				}
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}
