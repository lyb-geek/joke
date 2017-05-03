package com.gradle.joke.base;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

/**
 * 
 * <p>
 * Title: AuthDirective
 * </p>
 * <p>
 * Description: 自定义权限标签,只要满足其中一项权限就通过
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月14日
 */
public class AuthDirective implements TemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		TemplateModel paramValue = (TemplateModel) params.get(GlobalParams.AUTH_NAME);

		String authName = ((TemplateScalarModel) paramValue).getAsString();
		if (StringUtils.isNotBlank(authName)) {
			Subject subject = SecurityUtils.getSubject();
			boolean pass = false;
			if (authName.contains(GlobalParams.AUTH_NAME_SPLIT)) {
				String[] authArray = StringUtils.split(authName, GlobalParams.AUTH_NAME_SPLIT);
				// int passIndex = 0;
				for (String auth : authArray) {
					boolean singlePass = subject.isPermitted(auth);
					if (singlePass) {
						pass = true;
						break;
					}
				}

				// if (passIndex == authArray.length) {
				// pass = true;
				// }
			} else {
				pass = subject.isPermitted(authName);

			}

			if (pass) {
				body.render(env.getOut());
			}

		}

	}

}
