package com.gradle.joke.task;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.service.EmailService;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Component("sendAlarmMailTask")
@Scope("prototype")
public class SendAlarmMailTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SendAlarmMailTask.class);

	@Autowired
	private EmailService emailsvc;

	private String message;

	private Integer isImgAlarm;

	@Value("${project.basePath}")
	private String basePath;

	@Autowired
	private Configuration configuration; // freeMarker configuration

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getIsImgAlarm() {
		return isImgAlarm;
	}

	public void setIsImgAlarm(Integer isImgAlarm) {
		this.isImgAlarm = isImgAlarm;
	}

	@Override
	public void run() {
		try {
			if (GlobalParams.YES == isImgAlarm) {
				sendImgAlarmEmail();
			} else {
				sendAudioAlarmEmail();
			}
		} catch (Exception e) {
			logger.error("告警邮件发送异常：" + e.getMessage(), e);
		}

	}

	private void sendImgAlarmEmail() throws Exception {
		String[] to = GlobalParams.reminderEmailCache.get(GlobalParams.IMAGE_REMINDER_EMAIL_CACHE);
		Map<String, Object> model = new HashMap<>();
		model.put("title", "趣图文件丢失");
		model.put("fileName", message);
		model.put("url", basePath + "admin/index");
		String subject = "主题：趣图文件丢失";
		sendHtmlMailUsingFreeMarker(to, model, subject);

	}

	private void sendAudioAlarmEmail() throws Exception {
		String[] to = GlobalParams.reminderEmailCache.get(GlobalParams.AUDIO_REMINDER_EMAIL_CACHE);
		Map<String, Object> model = new HashMap<>();
		model.put("title", "音频文件丢失");
		model.put("fileName", message);
		model.put("url", basePath + "admin/index");
		String subject = "主题：音频文件丢失";
		sendHtmlMailUsingFreeMarker(to, model, subject);

	}

	private void sendHtmlMailUsingFreeMarker(String[] to, Map<String, Object> model, String subject) throws Exception {

		Template t = configuration.getTemplate("template/email/alarm.ftl"); // freeMarker template
		String content = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

		logger.info("emailContent:{}", content);
		emailsvc.sendAlarmMail(to, subject, content);
	}

}
