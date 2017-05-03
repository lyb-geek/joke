package com.gradle.joke.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * <p>
 * Title:TestController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月10日
 */
@Controller
@RequestMapping(value = "/test/")
public class TestController {

	@RequestMapping(value = "jsp")
	public String testJsp() {
		System.out.println("jsp");
		return "test";
	}

}
