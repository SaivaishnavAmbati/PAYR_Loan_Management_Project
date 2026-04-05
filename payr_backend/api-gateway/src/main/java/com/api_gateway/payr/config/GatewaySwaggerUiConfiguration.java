package com.api_gateway.payr.config;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Ensures the notification-service OpenAPI URL appears in the gateway Swagger UI even when
 * Config Server supplies an older {@code api-gateway-dev.yml} without that entry.
 */
@Configuration
public class GatewaySwaggerUiConfiguration implements BeanPostProcessor {

    private static final String NOTIFICATION_NAME = "notification-service";
    private static final String NOTIFICATION_SPEC = "/notification-service/v3/api-docs";

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (!(bean instanceof SwaggerUiConfigProperties props)) {
            return bean;
        }
        Set<SwaggerUrl> urls = props.getUrls();
        if (urls == null) {
            urls = new LinkedHashSet<>();
            props.setUrls(urls);
        }
        boolean hasNotification = urls.stream().anyMatch(u -> NOTIFICATION_NAME.equals(u.getName()));
        if (!hasNotification) {
            Set<SwaggerUrl> copy = new LinkedHashSet<>(urls);
            copy.add(new SwaggerUrl(NOTIFICATION_NAME, NOTIFICATION_SPEC, NOTIFICATION_NAME));
            props.setUrls(copy);
        }
        return bean;
    }
}
