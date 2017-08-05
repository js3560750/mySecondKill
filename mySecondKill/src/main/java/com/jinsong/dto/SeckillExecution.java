package com.jinsong.dto;

import com.jinsong.enums.SeckillStateEnum;
import com.jinsong.model.SuccessKilled;

/**
 * DTO层，相当于我们自定义的数据返回类型
 * 
 * 封装执行秒杀后的结果:是否秒杀成功 Created by codingBoy on 16/11/27.
 */
public class SeckillExecution {
	
	//秒杀商品ID
	private long seckillId;

	// 秒杀执行结果的状态
	private int state;

	// 状态的明文标识
	private String stateInfo;

	// 当秒杀成功时，需要传递秒杀成功的对象回去
	private SuccessKilled successKilled;

	//构造方法：秒杀成功返回的信息
	public SeckillExecution(long seckillId, SeckillStateEnum state, SuccessKilled successKilled) {
		super();
		this.seckillId = seckillId;
		this.state = state.getState();
		this.stateInfo = state.getInfo();
		this.successKilled = successKilled;
	}

	//构造方法：秒杀失败返回的信息
	public SeckillExecution(long seckillId, SeckillStateEnum state) {
		super();
		this.seckillId = seckillId;
		this.state = state.getState();
		this.stateInfo = state.getInfo();
	}

	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public SuccessKilled getSuccessKilled() {
		return successKilled;
	}

	public void setSuccessKilled(SuccessKilled successKilled) {
		this.successKilled = successKilled;
	}

	@Override
	public String toString() {
		return "SeckillExecution [seckillId=" + seckillId + ", state=" + state + ", stateInfo=" + stateInfo
				+ ", successKilled=" + successKilled + "]";
	}
	
	
	
	

}
