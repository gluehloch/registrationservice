package de.awtools.registration.config;

import javax.servlet.ServletContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    // base url: 'http://localhost:8080/registrationservice/api/'
    // http://localhost:8080/registrationservice/swagger-ui.html#/

    @Bean
    public Docket postsApi(ServletContext servletContext) {
        return new Docket(DocumentationType.SWAGGER_2)
                // .host("http://localhost:8080")
                // .pathMapping("/registrationservice/api")
                .groupName("public-api")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();//.host("http://localhost:8080").pathMapping("/registrationservice/api");
    }

    private Predicate<String> postPaths() {
        return PathSelectors.regex("/api/registration/ping");
        // return Predicates.or(
        // PathSelectors.regex("/api/registration/ping"),
        // PathSelectors.regex("/api/registration/register"),
        // PathSelectors.regex("/api/registration/validate"),
        // PathSelectors.regex("/api/registration/confirm"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SpringMVC Example API")
                .description("SpringMVC Example API reference for developers")
                .termsOfServiceUrl("http://hantsy.blogspot.com")
                .contact("Hantsy Bai")
                .license("Apache License Version 2.0")
                .licenseUrl(
                        "https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("2.0")
                .build();
    }

}
