package az.bassied.ms.auth.model.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class URLs {

    public static final String ROOT = "/v1/ms-auth";
    public static final String ROOT_INTERNAL = "/v1/internal/ms-auth";

    //Auth controller
    public static final String SRP1 = "/srp/step1";
    public static final String SRP2 = "/srp/step2";
    public static final String CHANGE_PASS = "/change-password";
    public static final String LOGOUT = "/logout";
    public static final String REFRESH_TOKEN = "/token/refresh";

    //Sign up controller
    public static final String SIGN_UP_ROOT = ROOT + "/sign-up";
    public static final String SIGN_UP_CONFIRM =  "/confirm/{token}";

    //Verify controller

    public static final String VERIFY_ROOT = ROOT + "/verify";

}
