package com.gradle.joke.base;

import java.lang.reflect.Method;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle.joke.pubsub.AudioSubscribe;
import com.gradle.joke.pubsub.ImgSubscribe;

/**
 * 
 * <p>
 * Title: RedisConfig
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月16日
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	@Bean
	RedisMessageListenerContainer audioContainer(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter audioListenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(audioListenerAdapter, new PatternTopic(GlobalParams.TOPIC_AUDIO));

		return container;
	}

	@Bean("audioListenerAdapter")
	MessageListenerAdapter audioListenerAdapter(AudioSubscribe audioSubscribe) {
		return new MessageListenerAdapter(audioSubscribe, "receiveMessage");
	}

	@Bean("audioSubscribe")
	AudioSubscribe audioSubscribe() {
		return new AudioSubscribe();
	}

	@Bean
	RedisMessageListenerContainer imgContainer(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter imgListenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(imgListenerAdapter, new PatternTopic(GlobalParams.TOPIC_IMAGE));

		return container;
	}

	@Bean("imgListenerAdapter")
	MessageListenerAdapter imgListenerAdapter(ImgSubscribe imgSubscribe) {
		return new MessageListenerAdapter(imgSubscribe, "receiveMessage");
	}

	@Bean("imgSubscribe")
	ImgSubscribe imgSubscribe() {
		return new ImgSubscribe();
	}

	@Bean
	public KeyGenerator wiselyKeyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getName());
				sb.append(method.getName());
				for (Object obj : params) {
					sb.append(obj.toString());
				}
				return sb.toString();
			}
		};

	}

	@Bean
	public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
		return new RedisCacheManager(redisTemplate);
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
		StringRedisTemplate template = new StringRedisTemplate(factory);
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}
}
