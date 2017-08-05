package com.jinsong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jinsong.dao.SeckillDAO;
import com.jinsong.model.Seckill;

@RestController
public class TestController {

	@Autowired
	private SeckillDAO seckillDAO;

	@GetMapping(value = "/test")
	public String myTest() {
		Seckill seckill = seckillDAO.queryById(1000);
		return seckill.toString();
	}
}
