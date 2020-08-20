package com.reimu.boot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.resource.GzipResourceResolver;
import com.reimu.base.jackson.ReimuBeanSerializerModifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.util.concurrent.TimeUnit;


@Configuration
public class MVCConfig implements WebMvcConfigurer {


    @Value("${project.staticFile.resourceHandler}")
    private String resourceHandler;

    @Value("${project.staticFile.location}")
    private String location;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder ->  jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        /** 为objectMapper注册一个带有SerializerModifier的Factory */
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(new ReimuBeanSerializerModifier()));
        //todo 未处理Float或Integer类型的NULL情况
//        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
//        serializerProvider.setNullValueSerializer(new CustomizeNullJsonSerializer.NullObjectJsonSerializer());
        return objectMapper;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Including all static resources.
        registry.addResourceHandler(resourceHandler).addResourceLocations("file:///"+location).setCacheControl(CacheControl
                .maxAge(365, TimeUnit.DAYS)
                .cachePrivate()).resourceChain(true)
                .addResolver(new GzipResourceResolver()) //如有nginx 代理则关闭
                .addResolver(new PathResourceResolver());
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    // 返回 Hibernate validation
    @Override
    public Validator getValidator() {
        return createLocalValidatorFactoryBean();
    }

    // bean级别的校验 方法中的参数bean必须添加@Valid注解
    @Bean(name = "validator")
    public LocalValidatorFactoryBean createLocalValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    //方法级别的校验  要校验的方法所在类必须添加@Validated注解
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(createLocalValidatorFactoryBean());
        return methodValidationPostProcessor;
    }

}
