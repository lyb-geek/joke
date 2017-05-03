package com.gradle.joke.base;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gradle.joke.sdk.servlet.StartCaptchaServlet;
import com.gradle.joke.sdk.servlet.VerifyLoginServlet;

/**
 * 
 * <p>
 * Title: GeetServletConfig
 * </p>
 * <p>
 * Description: 极验证码
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月17日
 */
@Configuration
public class GeetServletPcConfig {

	@Bean
	public ServletRegistrationBean startCaptchaServletRegistrationBean() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new StartCaptchaServlet());
		registration.setEnabled(true);
		registration.addUrlMappings("/pc-geetest/register");
		return registration;
	}

	@Bean
	public ServletRegistrationBean verifyLoginServletRegistrationBean() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new VerifyLoginServlet());
		registration.setEnabled(true);
		registration.addUrlMappings("/pc-geetest/validate");
		return registration;
	}
}
