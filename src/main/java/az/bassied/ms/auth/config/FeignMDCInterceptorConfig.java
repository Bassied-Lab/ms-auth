package az.bassied.ms.auth.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;

import java.util.Map;

public class FeignMDCInterceptorConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        if (mdc != null) {
            for (Map.Entry<String, String> entry : mdc.entrySet()) {
                template.header(entry.getKey(), entry.getValue());
            }
        }
    }
}