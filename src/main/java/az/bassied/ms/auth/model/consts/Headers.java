package az.bassied.ms.auth.model.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Headers {
    public static final String REQUEST_ID = "Request-ID";
    public static final String USER_ID = "User-ID";
    public static final String USER_ROLE = "User-Role";
    public static final String USER_EMAIL = "User-Email";

    public static final String USER_IP = "X-Forwarded-For";
    public static final String USER_AGENT = "User-Agent";
    public static final String OPERATION = "operation";

    public static final String REFRESH_TOKEN = "refresh-token";
    public static final String ACCESS_TOKEN = "access-token";
    public static final String COOKIE_IDENTITY = "identity";

    public static final String LANGUAGE = "lang";
}
