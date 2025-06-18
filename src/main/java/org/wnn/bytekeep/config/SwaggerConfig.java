package org.wnn.bytekeep.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author NanNan Wang
 */
@Configuration
@EnableSwagger2WebMvc
@Profile({"dev","test"})
public class SwaggerConfig {

    // 如果有配置，则使用配置的包路径，否则使用默认的包路径
    @Value("${swagger.base-packages:}")
    private String[] controllerPackages;

    @Bean
    public Docket docket(Environment environment) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(getApiSelector())
                .build()
                .apiInfo(apiInfo());
    }

    /**
     * 获取 API 选择器：如果未配置 `swagger.base-packages`，则扫描所有 `@Controller` 和 `@RestController`。
     */
    private Predicate<RequestHandler> getApiSelector() {
        if (controllerPackages == null || controllerPackages.length == 0 || ObjectUtils.isEmpty(controllerPackages[0])) {
            return RequestHandlerSelectors.withClassAnnotation(org.springframework.stereotype.Controller.class)
                    .or(RequestHandlerSelectors.withClassAnnotation(org.springframework.web.bind.annotation.RestController.class));
        }
        return Arrays.stream(controllerPackages)
                .filter(pkg -> !ObjectUtils.isEmpty(pkg))
                .map(RequestHandlerSelectors::basePackage)
                .reduce(Predicate::or)
                .orElse(RequestHandlerSelectors.any());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API文档")
                .description("这是我的API文档")
                .contact(new Contact("王楠楠", "","bjcpwnn@126.com"))
                .version("v1.0")
                .build();
    }
}
