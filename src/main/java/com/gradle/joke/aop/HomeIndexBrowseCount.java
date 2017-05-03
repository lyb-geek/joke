package com.gradle.joke.aop;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.gradle.joke.base.GlobalParams;

/**
 * 
 * <p>
 * Title: HomeIndexBrowseCount
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月17日
 */
@Aspect
@Configuration
public class HomeIndexBrowseCount {
	private static final Logger logger = LoggerFactory.getLogger(HomeIndexBrowseCount.class);

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Pointcut("@annotation(com.gradle.joke.anotation.NeedBrowseCount)")
	public void pointcut() {

	}

	@Before("pointcut()")
	public void doBefore(JoinPoint joinPoint) {
		String key = key(joinPoint, true);
		redisTemplate.boundValueOps(key).increment(1L);

	}

	@After("pointcut()")
	public void doAfter(JoinPoint joinPoint) {
		String key = key(joinPoint, false);

		logger.info(key + "已经被访问:{}次.", redisTemplate.boundValueOps(key).get());
	}

	private String key(JoinPoint joinPoint, Boolean isDobefore) {
		// 代理的目标对象名称
		String objName = joinPoint.getTarget().getClass().getSimpleName();
		Signature signature = joinPoint.getSignature();
		String methodName = signature.getName();
		List<Object> args = Arrays.asList(joinPoint.getArgs());
		if (isDobefore) {
			logger.info("对类:{}方法参数:{}方法:{}开启访问计数统计", objName, args, methodName);
		}

		String key = objName + GlobalParams.STR_SPLIT + methodName;

		return key;
	}

}
