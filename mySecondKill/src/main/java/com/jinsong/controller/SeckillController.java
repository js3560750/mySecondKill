package com.jinsong.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jinsong.dto.Exposer;
import com.jinsong.dto.SeckillExecution;
import com.jinsong.dto.SeckillResult;
import com.jinsong.enums.SeckillStateEnum;
import com.jinsong.exception.RepeatKillException;
import com.jinsong.exception.SeckillCloseException;
import com.jinsong.exception.SeckillException;
import com.jinsong.model.Seckill;
import com.jinsong.service.SeckillService;

@Controller	//这里没有加总的映射地址
public class SeckillController {

	// 直接调用Service，不要调用Service的实现ServiceImpl
	@Autowired
	SeckillService seckillService;

	/**
	 * 获取列表页
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		List<Seckill> seckillList = seckillService.getSeckillList();

		// model里的内容作为JSON串会自动的给到前端页面里去
		model.addAttribute("list", seckillList);

		return "list";
	}

	/**
	 * 点击列表页中的商品，跳转到详情页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, // 这里用Long 没有用long ，因为只有Long型才能判断是否==null
			Model model) {

		if (seckillId == null) {
			// 如果传入的商品ID为空，则跳转到list页
			return "redirect:/seckillId/list";
		}

		Seckill seckill = seckillService.getById(seckillId);
		if (seckill == null) {
			// 如果传入的商品ID，而数据库中没有该ID，则跳转到List页
			return "forward:/seckillId/list";
		}
		model.addAttribute("seckill", seckill);

		return "detail";
	}

	/**
	 * 获取系统时间
	 * 
	 */
	@RequestMapping(value = "/time/now", method = RequestMethod.GET)
	@ResponseBody // 返回的不是页面，使return的结果为JSON数据
	public SeckillResult<Long> time() {

		Date nowTime = new Date();
		// 泛型指定nowTime.getTime()是个Long型的对象
		return new SeckillResult<Long>(true, nowTime.getTime());
	}

	/**
	 * ajax,JSON,暴露秒杀接口
	 */
	@RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.GET, produces = {
			"application/json;charset=UTF-8" }) // produces指定返回结果是一个JSON
	@ResponseBody	//使return的结果为JSON数据
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") long seckillId) {

		SeckillResult<Exposer> result;

		try {
			// 在秒杀开启时输出秒杀接口的地址 ,秒杀未开启则输出系统时间和秒杀时间
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
			e.printStackTrace();
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}

		return result;

	}

	/**
	 * 执行秒杀
	 */
	@RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {
			"application/json;charset=UTF-8" })
	@ResponseBody	//使return的结果为JSON数据
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") long seckillId,
			@PathVariable("md5") String md5, @CookieValue(value = "userPhone", required = false) Long userPhone) {

		// 如果用户未登录、未注册就执行秒杀
		if (userPhone == null) {
			return new SeckillResult<SeckillExecution>(false, "用户未注册");
		}

		try {
			// 正常执行秒杀
			SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
			return new SeckillResult<SeckillExecution>(true, seckillExecution);

		} catch (RepeatKillException e) {
			// seckillService.executeSeckill执行过程中抛出重复秒杀异常
			SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(true, seckillExecution);

		} catch (SeckillCloseException e) {
			// seckillService.executeSeckill执行过程中抛出关闭秒杀异常
			SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.END);
			return new SeckillResult<SeckillExecution>(true, seckillExecution);

		} catch (SeckillException e) {
			// seckillService.executeSeckill执行过程中抛出其他秒杀异常
			SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(true, seckillExecution);
		}

	}
}
