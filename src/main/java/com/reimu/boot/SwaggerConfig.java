package com.reimu.boot;

import org.apache.commons.compress.utils.Sets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static springfox.documentation.builders.PathSelectors.ant;


@Configuration
@EnableOpenApi
public class SwaggerConfig {

    private String API_PATH = "localhost:8535";

    @Bean
    public Docket loginApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户登录与注销")
                .host(API_PATH)
                .produces(Sets.newHashSet("application/json"))
                .consumes(Sets.newHashSet("application/json"))
                .protocols(Sets.newHashSet("http", "https"))
                .apiInfo(apiInfo())
                .forCodeGeneration(true)
                .select()
                .paths(ant("/login/**"))
                .build();
    }


    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("ReimuOA_SpringBoot")
                .description("请在上方选择相应的模块进行查看")
                .termsOfServiceUrl("https://github.com/moons7/ReimuOA_SpringBoot")
                .contact(new Contact("moons7","","lingzero@foxmail.com"))
                .license("MIT")
                .version("1.0")
                .build();
    }

}