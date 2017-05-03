package com.gradle.joke.base;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;

/**
 * 
 * <p>
 * Title: AuthTagFreeMarkerConfig
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月14日
 */
@Component
public class AuthTagFreeMarkerConfig implements BeanPostProcessor {
	@Autowired
	private Configuration configuration;// freeMarker configuration

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		// 加上这句后，可以在页面上使用自定义标签
		configuration.setSharedVariable(GlobalParams.AUTH, new AuthDirective());
		return bean;
	}

}
