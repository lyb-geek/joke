package com.gradle.joke.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gradle.joke.base.AjaxStatusInfo;
import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.entity.User;
import com.gradle.joke.service.EmailService;
import com.gradle.joke.service.UserService;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 
 * <p>
 * Title: LoginController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月26日
 */
@Controller
@RequestMapping(value = "/")
public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	private UserService userSvc;
	@Autowired
	private EmailService emailSvc;

	@Autowired
	private Configuration configuration; // freeMarker configuration

	@GetMapping(value = "login")
	public String loginForm(Model model) {
		return "admin/login";
	}

	@SuppressWarnings("null")
	@PostMapping(value = "login")
	public String login(User user, Integer rememberMe, RedirectAttributes redirectAttributes) {
		// 获取当前的Subject
		UsernamePasswordToken token = null;
		Subject currentUser = SecurityUtils.getSubject();
		if (!currentUser.isAuthenticated() && currentUser.isRemembered()) {
			rememberedMe(currentUser, token);
		} else {
			userLogin(user, redirectAttributes, currentUser, token, rememberMe);
		}

		// 验证是否登录成功
		if (currentUser.isAuthenticated()) {
			logger.info("用户[" + user.getUsername() + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
			return "redirect:/admin/index";
		} else {
			token.clear();
			return "redirect:/login";
		}
	}

	private void userLogin(User user, RedirectAttributes redirectAttributes, Subject currentUser,
			UsernamePasswordToken token, Integer rememberMe) {
		String username = user.getUsername();
		token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
		try {
			// 在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
			// 每个Realm都能在必要时对提交的AuthenticationTokens作出反应
			// 所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
			logger.info("对用户[" + username + "]进行登录验证..验证开始");
			if (GlobalParams.YES == rememberMe) {
				token.setRememberMe(true);
			}
			currentUser.login(token);
			logger.info("对用户[" + username + "]进行登录验证..验证通过");
		} catch (UnknownAccountException uae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
			redirectAttributes.addFlashAttribute("message", "未知账户");
		} catch (IncorrectCredentialsException ice) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
			redirectAttributes.addFlashAttribute("message", "密码不正确");
		} catch (LockedAccountException lae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");
			redirectAttributes.addFlashAttribute("message", "账户已锁定");
		} catch (ExcessiveAttemptsException eae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");
			redirectAttributes.addFlashAttribute("message", "用户名或密码错误次数过多");
		} catch (AuthenticationException ae) {
			// 通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");
			ae.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "用户名或密码不正确");
		}
	}

	private void rememberedMe(Subject subject, UsernamePasswordToken token) {
		Object principal = subject.getPrincipal();
		if (null != principal) {
			User user = (User) principal;
			token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
			token.setRememberMe(true);
			subject.login(token);// 登录
			Session session = SecurityUtils.getSubject().getSession();
			session.setAttribute(GlobalParams.CURRENT_USER, user);
		}
	}

	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout(RedirectAttributes redirectAttributes) {
		// 使用权限管理工具进行用户的退出，跳出登录，给出提示信息
		SecurityUtils.getSubject().logout();
		redirectAttributes.addFlashAttribute("message", "您已安全退出");
		return "redirect:/login";
	}

	@RequestMapping("403")
	public String unauthorizedRole() {
		logger.info("------没有权限-------");
		return "/admin/403";
	}

	@RequestMapping(value = "checkRegistername")
	@ResponseBody
	public boolean checkUsername(String registername) {
		boolean isNotExist = true;
		User user = userSvc.getUserByUsername(registername);

		if (user != null) {
			isNotExist = false;
		}

		return isNotExist;

	}

	@RequestMapping(value = "register")
	@ResponseBody
	public AjaxStatusInfo<String> register(User user) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			userSvc.persist(user);
		} catch (Exception e) {
			logger.error("save fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping("userInfo")
	public String userInfo(Model model) {
		Session session = SecurityUtils.getSubject().getSession();
		User user = (User) session.getAttribute(GlobalParams.CURRENT_USER);
		model.addAttribute("user", user);
		return "/admin/user/show";
	}

	@RequestMapping(value = "checkEmail")
	@ResponseBody
	public boolean checkEmail(String email, Integer sendEmail) {
		boolean isNotExist = true;

		User user = userSvc.findByEmail(email);

		if (GlobalParams.YES == sendEmail) {
			if (user == null) {
				isNotExist = false;
			}
		} else {
			if (user != null) {
				isNotExist = false;
			}
		}

		return isNotExist;

	}

	@RequestMapping(value = "findPwdByEmail")
	@ResponseBody
	public AjaxStatusInfo<String> findPwdByEmail(String email) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			User user = userSvc.findByEmail(email);
			if (user != null) {
				sendHtmlMailUsingFreeMarker(user);
			} else {
				logger.error("findPwdByEmail fail : 邮箱: {}的用户存在", email);
				ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
				ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
			}
		} catch (Exception e) {
			logger.error("findPwdByEmail fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	private void sendHtmlMailUsingFreeMarker(User user) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		model.put("username", user.getUsername());
		model.put("password", user.getPassword());

		Template t = configuration.getTemplate("template/email/email.ftl"); // freeMarker template
		String content = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

		logger.info("emailContent:{}", content);
		emailSvc.sendHtmlMail(user.getEmail(), "主题：密码找回", content);
	}

}
