package com.gradle.joke.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gradle.joke.base.PageHelper;
import com.gradle.joke.base.SearchForm;
import com.gradle.joke.dao.EmailLogDao;
import com.gradle.joke.entity.EmailLog;
import com.gradle.joke.util.DateUtil;
import com.gradle.joke.util.MailUtil;

/**
 * 
 * <p>
 * Title:EmailService
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月12日
 */
@Service
@Transactional
public class EmailService {
	private final Logger logger = LoggerFactory.getLogger(EmailService.class);
	@Autowired
	private EmailLogDao emailLogDao;
	@Autowired
	private JavaMailSender sender;

	@Autowired
	private MailUtil mailUtil;

	@Value("${spring.mail.username}")
	private String from;

	@Value("${mail.trySendEamilMaxTimes}")
	private Integer trySendEamilMaxNum;

	public void persist(EmailLog emailLog) {
		if (emailLog.getId() == null) {
			emailLog.setCreateTime(DateUtil.getNow());
		}

		emailLogDao.save(emailLog);
	}

	public boolean sendEmail(EmailLog emailLog, int trySendEamilNum) {
		boolean isSendOk = true;
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		// 自己发给自己吧，因为业务考虑错了，哎！！！
		message.setTo(from);
		message.setSubject(emailLog.getSubject());
		message.setText(emailLog.getMessage());

		try {
			sender.send(message);
			logger.info("邮件已经发送。");
		} catch (Exception e) {
			logger.error("发送邮件时发生异常！" + e.getMessage(), e);
			isSendOk = false;
		}

		if (!isSendOk) {
			trySendEamilNum++;
			if (trySendEamilNum > trySendEamilMaxNum) {
				return false;
			}
			logger.info("邮件没有发送成功，正在进行第" + trySendEamilNum + "次重试");
			return sendEmail(emailLog, trySendEamilNum);
		} else {
			this.persist(emailLog);
		}

		return isSendOk;
	}

	public void sendAlarmMail(String[] to, String subject, String content) {
		MimeMessage message = sender.createMimeMessage();

		try {
			// true表示需要创建一个multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true);
			sender.send(message);
			logger.info("告警邮件已经发送。");
		} catch (MessagingException e) {
			logger.error("发送告警邮件时发生异常！", e);
		}
	}

	/**
	 * 发送html格式的邮件
	 * 
	 * @param to
	 * @param subject
	 * @param content
	 */
	public void sendHtmlMail(String to, String subject, String content) {
		MimeMessage message = sender.createMimeMessage();

		try {
			// true表示需要创建一个multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true);
			sender.send(message);
			logger.info("html邮件已经发送。");
		} catch (MessagingException e) {
			logger.error("发送html邮件时发生异常！", e);
		}
	}

	/**
	 * 发送带附件的邮件
	 * 
	 * @param to
	 * @param subject
	 * @param content
	 * @param filePath
	 */
	public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
		MimeMessage message = sender.createMimeMessage();

		try {
			// true表示需要创建一个multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true);

			FileSystemResource file = new FileSystemResource(new File(filePath));
			String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
			helper.addAttachment(fileName, file);

			sender.send(message);
			logger.info("带附件的邮件已经发送。");
		} catch (MessagingException e) {
			logger.error("发送带附件的邮件时发生异常！", e);
		}
	}

	/**
	 * 发送嵌入静态资源（一般是图片）的邮件
	 * 
	 * @param to
	 * @param subject
	 * @param content
	 *            邮件内容，需要包括一个静态资源的id，比如：<img src=\"cid:rscId01\" >
	 * @param rscPath
	 *            静态资源路径和文件名
	 * @param rscId
	 *            静态资源id
	 */
	public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId) {
		MimeMessage message = sender.createMimeMessage();

		try {
			// true表示需要创建一个multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true);

			FileSystemResource res = new FileSystemResource(new File(rscPath));
			helper.addInline(rscId, res);

			sender.send(message);
			logger.info("嵌入静态资源的邮件已经发送。");
		} catch (MessagingException e) {
			logger.error("发送嵌入静态资源的邮件时发生异常！", e);
		}
	}

	@Deprecated
	public boolean sendEmailByMailUtil(EmailLog emailLog, int trySendEamilNum) {
		boolean isSendOk = mailUtil.sendMail(from, from, emailLog.getSubject(), emailLog.getMessage(), 0);
		if (isSendOk) {
			this.persist(emailLog);
		}

		return isSendOk;
	}

	public Long getTotalCount() {
		return emailLogDao.count();

	}

	public Page<EmailLog> getPageList(Pageable pageable) {
		return emailLogDao.findAll(pageable);
	}

	public EmailLog getEmailLogById(Long id) {
		return emailLogDao.findOne(id);
	}

	public void deleteEmailLogById(Long id) {
		emailLogDao.delete(id);
	}

	public void batchDelByIds(Long[] ids) {
		for (Long id : ids) {
			emailLogDao.delete(id);
		}
	}

	public PageHelper<EmailLog> getPages(Integer pageNo, Integer pageSize, final SearchForm searchForm) {
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "createTime");
		PageHelper<EmailLog> pageHelper = null;

		Page<EmailLog> page = emailLogDao.findAll(new Specification<EmailLog>() {

			@Override
			public Predicate toPredicate(Root<EmailLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return prepareCondition(root, cb, searchForm);

			}
		}, pageable);

		Long totalRecord = emailLogDao.count(new Specification<EmailLog>() {

			@Override
			public Predicate toPredicate(Root<EmailLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return prepareCondition(root, cb, searchForm);
			}
		});

		pageHelper = new PageHelper<>(Integer.valueOf(totalRecord.toString()), pageSize);
		pageHelper.setResult(page.getContent());

		return pageHelper;
	}

	private Predicate prepareCondition(Root<EmailLog> root, CriteriaBuilder cb, final SearchForm searchForm) {
		List<Predicate> list = new ArrayList<Predicate>();
		if (StringUtils.isEmpty(searchForm.getEndDate()) && !StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.greaterThanOrEqualTo(root.get("createTime").as(String.class), searchForm.getStartDate()));
		} else if (!StringUtils.isEmpty(searchForm.getEndDate()) && !StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.between(root.get("createTime").as(String.class), searchForm.getStartDate(),
					searchForm.getEndDate()));
		} else if (!StringUtils.isEmpty(searchForm.getEndDate()) && StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.lessThanOrEqualTo(root.get("createTime").as(String.class), searchForm.getEndDate()));
		}

		if (!StringUtils.isEmpty(searchForm.getKeyword())) {
			Predicate usernamePredicate = cb.like(root.get("username").as(String.class),
					"%" + searchForm.getKeyword() + "%");
			Predicate emailPredicate = cb.like(root.get("email").as(String.class), "%" + searchForm.getKeyword() + "%");
			Predicate subjectPredicate = cb.like(root.get("subject").as(String.class),
					"%" + searchForm.getKeyword() + "%");
			list.add(cb.or(usernamePredicate, emailPredicate, subjectPredicate));

		}
		Predicate[] p = new Predicate[list.size()];
		return cb.and(list.toArray(p));
	}

}
