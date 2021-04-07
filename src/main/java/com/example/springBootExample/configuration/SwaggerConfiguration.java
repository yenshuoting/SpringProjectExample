package com.example.springBootExample.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

	public SwaggerConfiguration() {}

	/**
	 *  new Docket() 可以進行設定，DocumentationType 有幾個類型可以選擇：
	 *  DocumentationType.SWAGGER_12：swagger 1.2
	 *  DocumentationType.SWAGGER_2：swagger 2.0
	 *  DocumentationType.OAS_30：openApi 3.0
	 * @return
	 */
	@Bean
	public Docket swaggerSetting() {
		return new Docket(DocumentationType.OAS_30)
				.apiInfo(apiInfo())
				.select()
				.paths(PathSelectors.any())
				.build();
	}
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("Management System Documentation")
				.description("learning Spring Boot")
				.version("0.0")
				.build();
	}
}