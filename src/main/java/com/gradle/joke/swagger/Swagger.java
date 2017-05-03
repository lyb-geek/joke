package com.gradle.joke.swagger;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger {
	// @Bean
	// public Docket createRestApi() {
	// return new Docket(DocumentationType.SWAGGER_2).ignoredParameterTypes(Model.class).apiInfo(homeApiInfo())
	// .select().apis(RequestHandlerSelectors.basePackage("com.gradle.joke.controller"))
	// .paths(PathSelectors.any()).build();
	// }

	@Bean
	public Docket homeApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("home").genericModelSubstitutes(DeferredResult.class)
				// .genericModelSubstitutes(ResponseEntity.class)
				.useDefaultResponseMessages(false).forCodeGeneration(true).pathMapping("/")// base，最终调用接口后会和paths拼接在一起
				.select().paths(or(regex("/home/.*")))// 过滤的接口
				.build().apiInfo(homeApiInfo());
	}

	private ApiInfo homeApiInfo() {
		return new ApiInfoBuilder().title("joke前台").description("joke").termsOfServiceUrl("http://joke.admin.com/")
				.contact(new Contact("lyb", "www.lyb.com", "410480180@qq.com")).version("1.0").build();
	}

	@Bean
	public Docket adminApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("admin").genericModelSubstitutes(DeferredResult.class)
				// .genericModelSubstitutes(ResponseEntity.class)
				.useDefaultResponseMessages(false).forCodeGeneration(true).pathMapping("/")// base，最终调用接口后会和paths拼接在一起
				.select().paths(or(regex("/admin/.*")))// 过滤的接口
				.build().apiInfo(adminApiInfo());
	}

	private ApiInfo adminApiInfo() {
		return new ApiInfoBuilder().title("joke后台管理").description("joke平台管理")
				.termsOfServiceUrl("http://joke.admin.com/")
				.contact(new Contact("lyb", "www.lyb.com", "410480180@qq.com")).version("1.0").build();
	}
}
