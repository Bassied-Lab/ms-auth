package az.bassied.ms.auth.model.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Messages {
    public static final String PATH_NOT_FOUND = "client.path.not.found";
    public static final String SERVER_ERROR = "Server got an invalid response from client";
    public static final String TOKEN_NOT_FOUND = "verify.token.not.found";
    public static final String TOKEN_NOT_FOUND_MSG = "Provided token not found or expired";
    public static final String AUTH_EXP = "auth.failed";
    public static final String AUTH_EXP_MSG = "Authentication failed, required date is not provided";
    public static final String TOKEN_GEN_EXP = "auth.token.generationError";
    public static final String TOKEN_GEN_EXP_MSG = "Error occurred while generating token";
    public static final String TOKEN_PARSING_EXP = "auth.token.parsingError";
    public static final String TOKEN_PARSING_EXP_MSG = "Error occurred while parsing token";
    public static final String TRY_LIMIT_EXCEEDED_EXP = "try.limit.exceeded";
    public static final String TRY_LIMIT_EXCEEDED_EXP_MSG = "Try limit exceeded";
    public static final String ACCOUNT_LOCKED_EXP = "auth.account.locked";
    public static final String ACCOUNT_LOCKED_EXP_MSG = "Account was blocked";
    public static final String EMPTY_CACHE_EXP = "cache.isEmpty.error";
    public static final String EMPTY_CACHE_EXP_MSG = "Cache is empty";
    public static final String REFRESH_TOKEN_EXP = "auth.refreshToken.error";
    public static final String REFRESH_TOKEN_EXP_MSG = "Refresh token is expired";
    public static final String ACCESS_TOKEN_EXP = "auth.accessToken.error";
    public static final String ACCESS_TOKEN_EXP_MSG = "Refresh token not found or expired";
    public static final String USER_INACTIVE_EXP = "auth.user.inactive";
    public static final String USER_INACTIVE_EXP_MSG = "Provided user is inactive";
    public static final String INVALID_CREDENTIALS_EXP = "auth.credentials.incorrect";
    public static final String INVALID_CREDENTIALS_EXP_MSG = "Provided credentials is incorrect";



}
