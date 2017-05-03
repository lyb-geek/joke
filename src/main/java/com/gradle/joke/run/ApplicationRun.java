package com.gradle.joke.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 
 * <p>
 * Title:ApplicationRun
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
@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = { "com.gradle.joke.*" })
@EntityScan(basePackages = "com.gradle.joke.entity")
@EnableJpaRepositories(basePackages = "com.gradle.joke.dao")
@EnableTransactionManagement
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 120)
public class ApplicationRun extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationRun.class, args);

	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/WEB-INF/views/jsp/", ".jsp");
	}

}
