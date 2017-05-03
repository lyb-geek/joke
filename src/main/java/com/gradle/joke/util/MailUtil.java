package com.gradle.joke.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.gradle.joke.base.GlobalParams;
import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 
 * <p>
 * Title:MailUtil
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
@Component
public class MailUtil {
	private final Logger logger = LoggerFactory.getLogger(MailUtil.class);

	private Session mailSession;

	@Value("${spring.mail.host}")
	private String host;
	@Value("${spring.mail.port}")
	private Integer port;
	@Value("${spring.mail.username}")
	private String username;
	@Value("${spring.mail.password}")
	private String password;
	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String auth;
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String starttlsEnable;
	@Value("${spring.mail.properties.mail.smtp.timeout}")
	private String timeout;
	@Value("${mail.session.debug}")
	private Integer seesionDebug;

	@Value("${mail.trySendEamilMaxTimes}")
	private Integer trySendEamilMaxTimes;

	public MailUtil() {
	}

	public void initMailSession() throws Exception {

		// 配置发送邮件的环境属性
		final Properties props = new Properties();
		/*
		 * 可用的属性： mail.store.protocol / mail.transport.protocol / mail.host / mail.user / mail.from
		 */
		// 表示SMTP发送邮件，需要进行身份验证
		props.put("mail.smtp.auth", StringUtils.trimAllWhitespace(auth));
		props.put("mail.smtp.host", StringUtils.trimAllWhitespace(host));
		// 发件人的账号
		props.put("mail.user", StringUtils.trimAllWhitespace(username));
		// 访问SMTP服务时需要提供的密码
		props.put("mail.password", StringUtils.trimAllWhitespace(password));

		props.put("mail.port", port);

		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		props.put("mail.smtp.ssl.enable", StringUtils.trimAllWhitespace(starttlsEnable));
		props.put("mail.smtp.ssl.socketFactory", sf);

		// 构建授权信息，用于进行SMTP进行身份验证
		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// 用户名、密码
				String userName = props.getProperty("mail.user");
				String password = props.getProperty("mail.password");
				return new PasswordAuthentication(userName, password);
			}
		};
		// 使用环境属性和授权信息，创建邮件会话
		mailSession = Session.getInstance(props, authenticator);

		mailSession.setDebug(isSeesionDebug());// 设置debug模式 在控制台看到交互信息

	}

	public boolean sendMail(String from, String to, String subject, String content, int trySendEamilTimes) {
		if (from == null) {
			from = username;
		}
		boolean isSendOk = true;
		try {
			// 创建邮件消息
			MimeMessage message = new MimeMessage(mailSession);
			// 设置发件人
			InternetAddress sendMail = new InternetAddress(from);
			message.setFrom(sendMail);

			// 设置收件人
			InternetAddress receiveMail = new InternetAddress(to);
			message.setRecipient(RecipientType.TO, receiveMail);

			// 设置邮件标题
			message.setSubject(subject);

			// 设置邮件的内容体
			message.setContent(content, "text/html;charset=UTF-8");

			// 发送邮件
			Transport.send(message);
		} catch (Exception e) {
			isSendOk = false;
			logger.error(e.getMessage(), e);
		}

		if (!isSendOk) {
			trySendEamilTimes++;
			if (trySendEamilTimes > trySendEamilMaxTimes) {
				return false;
			}
			logger.info("邮件没有发送成功，正在进行第" + trySendEamilTimes + "次重试");
			return sendMail(from, to, subject, content, trySendEamilTimes);
		}

		return isSendOk;

	}

	public Session getMailSession() {
		return mailSession;
	}

	public boolean isSeesionDebug() {
		if (GlobalParams.DEBUG_YES == seesionDebug) {
			return true;
		}
		return false;
	}

	public String getHost() {
		return StringUtils.trimAllWhitespace(host);
	}

	public Integer getPort() {
		return port;
	}

	public String getUsername() {
		return StringUtils.trimAllWhitespace(username);
	}

	public String getPassword() {
		return StringUtils.trimAllWhitespace(password);
	}

	public String getAuth() {
		return StringUtils.trimAllWhitespace(auth);
	}

	public String getStarttlsEnable() {
		return StringUtils.trimAllWhitespace(starttlsEnable);
	}

	public String getTimeout() {
		return StringUtils.trimAllWhitespace(timeout);
	}

	public Integer getTrySendEamilMaxTimes() {
		return trySendEamilMaxTimes;
	}

}
