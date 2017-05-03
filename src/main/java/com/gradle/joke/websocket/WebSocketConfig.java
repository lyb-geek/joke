package com.gradle.joke.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * 
 * <p>
 * Title: WebSocketConfig
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月20日
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 分别表示在客户端上主题和个人的消息订阅
		// queue将消息推送给个人。推送给多个人的时候在外边执行循环。
		// 推送消息给订阅了某已主题的所有人
		registry.enableSimpleBroker("/topic", "/queue");
		// 客户端向系统发送消息
		registry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 前端可以通过此链接接入消息推送通道
		registry.addEndpoint("/notify")
				// 这个设置主要为了解决跨域问题。
				.setAllowedOrigins("*").withSockJS().setInterceptors(new HttpSessionHandshakeInterceptor());
	}

}
