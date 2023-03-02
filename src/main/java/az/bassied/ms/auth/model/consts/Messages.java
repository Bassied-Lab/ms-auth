package az.bassied.ms.auth.model.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Messages {
    public static final String PATH_NOT_FOUND = "client.path.not.found";
    public static final String SERVER_ERROR = "Server got an invalid response from client";

    public static final String TOKEN_NOT_FOUND = "verify.token.found";
    public static final String TOKEN_NOT_FOUND_MSG = "Provided token not found or expired";


}
