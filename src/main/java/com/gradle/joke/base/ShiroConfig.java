package com.gradle.joke.base;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gradle.joke.entity.PermissionConfig;
import com.gradle.joke.filter.MShiroFilterFactoryBean;
import com.gradle.joke.filter.MyAnyRolesFilter;
import com.gradle.joke.realm.MyShiroRealm;
import com.gradle.joke.service.PermissionConfigService;

/**
 * 
 * <p>
 * Title: ShiroConfig
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
@Configuration
public class ShiroConfig {
	private static final Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

	@Bean
	public EhCacheManager getEhCacheManager() {
		EhCacheManager em = new EhCacheManager();
		em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
		return em;
	}

	@Bean(name = "myShiroRealm")
	public MyShiroRealm myShiroRealm(EhCacheManager cacheManager) {
		MyShiroRealm realm = new MyShiroRealm();
		realm.setCacheManager(cacheManager);
		return realm;
	}

	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}

	/**
	 * 记住密码 cookie对象
	 */
	@Bean
	public SimpleCookie remembeeMecookie() {
		// 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
		SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
		// <!-- 记住我cookie生效时间3天 ,单位秒;-->
		simpleCookie.setMaxAge(259200);
		return simpleCookie;

	}

	/**
	 * Cookie管理对象
	 */
	@Bean
	public CookieRememberMeManager rememberMeManager() {

		CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
		rememberMeManager.setCookie(remembeeMecookie());
		rememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
		return rememberMeManager;
	}

	@Bean(name = "securityManager")
	public DefaultWebSecurityManager getDefaultWebSecurityManager(MyShiroRealm myShiroRealm) {
		DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
		dwsm.setRealm(myShiroRealm);
		// <!-- 用户授权/认证信息Cache, 采用EhCache 缓存 -->
		dwsm.setCacheManager(getEhCacheManager());

		// 注入记住我管理器;
		dwsm.setRememberMeManager(rememberMeManager());
		return dwsm;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
			DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
		aasa.setSecurityManager(securityManager);
		return aasa;
	}

	/**
	 * 加载shiroFilter权限控制规则（从数据库读取然后配置）
	 *
	 * 
	 * 
	 */
	private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean,
			PermissionConfigService permissionConfigsvc) {
		/////////////////////// 下面这些规则配置最好配置到配置文件中 ///////////////////////
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
		logger.info("##################从数据库读取权限规则，加载到shiroFilter中##################");
		// authc：该过滤器下的页面必须验证后才能访问，它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
		// filterChainDefinitionMap.put("/user", "authc");// 这里为了测试，只限制/user，实际开发中请修改为具体拦截的请求规则
		// // anon：它对应的过滤器里面是空的,什么都没做
		// filterChainDefinitionMap.put("/user/edit/**", "authc,perms[user:edit]");// 这里为了测试，固定写死的值，也可以从数据库或其他配置中读取
		//
		// filterChainDefinitionMap.put("/login", "anon");
		// filterChainDefinitionMap.put("/admin/**", "authc");
		// filterChainDefinitionMap.put("/**", "anon");// anon 可以理解为不拦截
		filterChainDefinitionMap.put("/css/**", "anon");
		filterChainDefinitionMap.put("/js/**", "anon");
		filterChainDefinitionMap.put("/img/**", "anon");
		filterChainDefinitionMap.put("/images/**", "anon");
		filterChainDefinitionMap.put("/front/**", "anon");
		// 自定义过滤器or过滤器。原生的roles过滤器roles[admin,user]是and关系
		// filterChainDefinitionMap.put("/permission/**", "authc,anyRoles[admin,manager]");

		List<PermissionConfig> permissionConfigs = permissionConfigsvc.getPermissConfigs();

		for (PermissionConfig config : permissionConfigs) {
			filterChainDefinitionMap.put(config.getUrl(), config.getPermissionPattern());
		}
		filterChainDefinitionMap.put("/admin/index", "user");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
	}

	/**
	 * ShiroFilter<br/>
	 * 注意这里参数中的 permissionConfigsvc 只是一个例子，因为我们在这里可以用这样的方式获取到相关访问数据库的对象， 然后读取数据库相关配置，配置到 shiroFilterFactoryBean 的访问规则中。实际项目中，请使用自己的Service来处理业务逻辑。
	 *
	 * @param myShiroRealm
	 * @param permissionConfigsvc
	 * @return
	 */
	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager,
			PermissionConfigService permissionConfigsvc) {

		ShiroFilterFactoryBean shiroFilterFactoryBean = new MShiroFilterFactoryBean();
		Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
		// 扩展过滤器
		filters.put("anyRoles", new MyAnyRolesFilter());
		shiroFilterFactoryBean.setFilters(filters);
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/login");
		// 登录成功后要跳转的链接，可以直接在登录逻辑里面指定
		// shiroFilterFactoryBean.setSuccessUrl("/user");
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");

		loadShiroFilterChain(shiroFilterFactoryBean, permissionConfigsvc);
		return shiroFilterFactoryBean;
	}

}
