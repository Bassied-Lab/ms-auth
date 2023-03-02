package az.bassied.ms.auth.config;

import az.bassied.ms.auth.model.consts.Headers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;


@Configuration
public class HandlerInterceptorConfig implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HandlerInterceptorConfig.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.trace("Action.preHandle.start");

        String requestId = request.getHeader(Headers.REQUEST_ID);
        if (!StringUtils.hasText(requestId)) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(Headers.REQUEST_ID, requestId);
        MDC.put(Headers.USER_ID, request.getHeader(Headers.USER_ID));
        MDC.put(Headers.USER_IP, getHeaderValue(Headers.USER_IP, request));
        MDC.put(Headers.USER_AGENT, getHeaderValue(Headers.USER_AGENT, request));
        MDC.put(Headers.OPERATION, request.getRequestURI());

        logger.trace("Action.preHandle.end");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        MDC.clear();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }


    private static String getHeaderValue(String headerName, HttpServletRequest servletRequest) {
        if (servletRequest.getHeaderNames() == null) return null;
        return Collections.list(servletRequest.getHeaderNames())
                .stream()
                .filter(Objects::nonNull)
                .filter(headerName::equalsIgnoreCase)
                .findFirst()
                .map(servletRequest::getHeader)
                .orElse(null);
    }

}
