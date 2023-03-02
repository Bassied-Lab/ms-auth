package az.bassied.ms.auth.config;

import az.bassied.ms.auth.error.handler.CustomErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "az.bassied.ms.auth.client")
public class FeignDefaultConfig {
    @Bean
    public ErrorDecoder restErrorResponseDecoder(ObjectMapper objectMapper) {
        return new CustomErrorDecoder(objectMapper);
    }
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignMDCInterceptorConfig();
    }
}