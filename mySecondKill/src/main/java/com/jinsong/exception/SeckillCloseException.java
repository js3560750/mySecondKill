package com.jinsong.exception;

/**
 * 秒杀关闭异常，当秒杀结束时用户还要进行秒杀就会出现这个异常 Created by codingBoy on 16/11/27.
 */
public class SeckillCloseException extends SeckillException {

	// 构造方法：这个异常抛出的信息由我们定义
	public SeckillCloseException(String message) {
		super(message);
	}

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
	}

}
