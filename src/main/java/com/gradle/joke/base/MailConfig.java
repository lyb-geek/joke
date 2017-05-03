package com.gradle.joke.base;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.gradle.joke.util.MailUtil;

/**
 * 
 * <p>
 * Title:MailConfig
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月13日
 */
@Configuration
public class MailConfig {
	@Autowired
	private MailUtil mailUtil;

	/**
	 * 特地自己在写一个注入。是因为怕自己的配置引入空格，而没有发现
	 * 
	 * @return
	 */
	@Bean("sender")
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();

		sender.setHost(mailUtil.getHost());
		sender.setPassword(mailUtil.getPassword());
		sender.setPort(mailUtil.getPort());
		sender.setUsername(mailUtil.getUsername());
		sender.setDefaultEncoding("UTF-8");
		Properties javaMailProperties = new Properties();

		javaMailProperties.setProperty("mail.smtp.auth", mailUtil.getAuth());
		javaMailProperties.setProperty("mail.smtp.timeout", mailUtil.getTimeout());
		javaMailProperties.setProperty("mail.smtp.starttls.enable", mailUtil.getStarttlsEnable());
		javaMailProperties.setProperty("mail.smtp.socketFactory.fallback", "false");
		javaMailProperties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		sender.setJavaMailProperties(javaMailProperties);

		return sender;
	}

}
