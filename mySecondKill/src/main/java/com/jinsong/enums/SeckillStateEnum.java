package com.jinsong.enums;

/**
 * 使用枚举表示常量数据字典
 * 
 * @author 188949420@qq.com
 *
 */
public enum SeckillStateEnum {

	SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATE_REWRITE(-3,"数据篡改");

    private int state;
    private String info;
    
    //添加这个构造器，上面的代码就不报错了
	private SeckillStateEnum(int state, String info) {
		this.state = state;
		this.info = info;
	}
	
	//根据index也就是上面的-1、0、1拿到对应的值
	public static SeckillStateEnum stateOf(int index) {
		for(SeckillStateEnum state : values()) {	//枚举内部有一个values方法拿到所有数据
			if(state.getState()==index) {	//如果传入的index属于我们这里定义的-3到1之间的一个，则返回这个枚举
				
				return state;
			}
			
		}
		return null;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
    
	
    
}
