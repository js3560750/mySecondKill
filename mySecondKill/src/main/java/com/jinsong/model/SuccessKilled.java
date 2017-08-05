package com.jinsong.model;

import java.util.Date;

/**
 * 数据库mysecondkill
 * 中的success_killed表模型
 * @author 188949420@qq.com
 *
 */
public class SuccessKilled {

	private long seckillId;
	private long userPhone;
	private int state;
	private Date createTime;
	
	//多对一,因为一件商品在库存中有很多数量，对应的购买明细也有很多。
	private Seckill seckill;
	
	
	public Seckill getSeckill() {
		return seckill;
	}
	public void setSeckill(Seckill seckill) {
		this.seckill = seckill;
	}
	public long getSeckillId() {
		return seckillId;
	}
	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}
	public long getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(long userPhone) {
		this.userPhone = userPhone;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Override
	public String toString() {
		return "SuccessKilled [seckillId=" + seckillId + ", userPhone=" + userPhone + ", state=" + state
				+ ", createTime=" + createTime + "]";
	}
	
	
}
