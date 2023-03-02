package az.bassied.ms.auth.service;

import az.bassied.ms.auth.model.jwt.AuthTokensDTO;
import az.bassied.ms.auth.model.jwt.NewPasswordDTO;
import az.bassied.ms.auth.model.srp.SrpStep1Req;
import az.bassied.ms.auth.model.srp.SrpStep1Res;
import az.bassied.ms.auth.model.srp.SrpStep2Req;
import az.bassied.ms.auth.model.srp.SrpStep2Res;
import org.springframework.data.util.Pair;

public interface AuthService {

    SrpStep1Res srpAuthStep1(SrpStep1Req request);

    Pair<SrpStep2Res, AuthTokensDTO> srpAuthStep2(SrpStep2Req request);

    AuthTokensDTO changePassword(String accessToken, NewPasswordDTO request);

    void logout(String accessToken);
}
