package com.gradle.joke.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * <p>
 * Title:AdminController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月15日
 */
@Controller
@RequestMapping(value = "/admin/")
public class AdminController {

	@ApiOperation(value = "joke.admin首页展示", notes = "joke.admin主要是对joke前台进行扩充")
	@RequestMapping(value = "index")
	public String index() {

		return "admin/index";
	}

	@ApiOperation(value = "joke.admin欢迎展示", notes = "")
	@RequestMapping(value = "welcome")
	public String welcome() {

		return "admin/welcome";
	}

}
