package com.messageapp.global.config;

import com.messageapp.global.auth.LoginMember;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("===== SwaggerConfig customOpenAPI Bean 생성 =====");
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        Components components = new Components()
                .addSecuritySchemes(jwt, new SecurityScheme()
                        .name(jwt)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
                .title("Message App API")
                .description("랜덤 메시지 매칭 애플리케이션 API 문서")
                .version("1.0.0");
    }

    @Bean
    public OperationCustomizer customizeOperation() {
        return (operation, handlerMethod) -> {
            // @LoginMember 어노테이션이 붙은 파라미터를 Swagger에서 제외
            Arrays.stream(handlerMethod.getMethodParameters())
                    .filter(param -> param.hasParameterAnnotation(LoginMember.class))
                    .forEach(param -> {
                        if (operation.getParameters() != null) {
                            operation.getParameters().removeIf(p ->
                                p.getName() != null && p.getName().equals(param.getParameterName()));
                        }
                    });
            return operation;
        };
    }
}
