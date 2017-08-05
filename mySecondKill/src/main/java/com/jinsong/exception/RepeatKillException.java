package com.jinsong.exception;

/**
 * 重复秒杀异常，是一个运行期异常，不需要我们手动try catch
 * Mysql只支持运行期异常的回滚操作
 * Created by codingBoy on 16/11/27.
 */
public class RepeatKillException extends SeckillException{

	//构造方法：这个异常抛出的信息由我们定义
	public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
