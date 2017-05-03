package com.gradle.joke.base;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.jagregory.shiro.freemarker.ShiroTags;

import freemarker.template.Configuration;

/**
 * 
 * <p>
 * Title: ShiroTagFreeMarkerConfig
 * </p>
 * <p>
 * Description: 集成shiro标签
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月12日
 */
@Component
public class ShiroTagFreeMarkerConfig implements InitializingBean {

	@Autowired
	private Configuration configuration;// freeMarker configuration

	@Autowired
	private FreeMarkerViewResolver resolver;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 加上这句后，可以在页面上使用shiro标签
		configuration.setSharedVariable("shiro", new ShiroTags());
		// 加上这句后，可以在页面上用${context.contextPath}获取contextPath
		resolver.setRequestContextAttribute("context");
	}

}
