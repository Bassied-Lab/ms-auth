package az.bassied.ms.auth.model.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class URLs {

    public static final String ROOT = "/v1/ms-auth";
    public static final String ROOT_INTERNAL = "/v1/internal/ms-auth";

    //Sign up controller
    public static final String SIGN_UP_ROOT = ROOT + "/sign-up";


}
