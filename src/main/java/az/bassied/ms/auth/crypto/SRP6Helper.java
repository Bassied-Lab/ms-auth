package az.bassied.ms.auth.crypto;

import az.bassied.ms.auth.model.common.UserDTO;
import az.bassied.ms.auth.model.srp.SrpStep1Res;
import az.bassied.ms.auth.model.srp.SrpStep2Req;
import az.bassied.ms.auth.model.srp.SrpStep2Res;
import com.nimbusds.srp6.BigIntegerUtils;
import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6Exception;
import com.nimbusds.srp6.SRP6ServerSession;
import com.nimbusds.srp6.SRP6VerifierGenerator;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class SRP6Helper {

    public static final SRP6CryptoParams CRYPTO_PARAMS =
            new SRP6CryptoParams(SRP6CryptoParams.N_256, SRP6CryptoParams.g_common, "SHA-256");

    public static SRP6VerifierGenerator generateSRP6VerifierGenerator() {
        SRP6VerifierGenerator srp6VerifierGenerator = new SRP6VerifierGenerator(CRYPTO_PARAMS);
        srp6VerifierGenerator.setXRoutine(SRP6ThinbusRoutines.getXRoutine());
        return srp6VerifierGenerator;
    }

    public SRP6ServerSession generateSRP6ServerSession() {
        SRP6ServerSession srp6ServerSession = new SRP6ServerSession(CRYPTO_PARAMS);
        srp6ServerSession.setClientEvidenceRoutine(SRP6ThinbusRoutines.getClientEvidenceRoutine());
        srp6ServerSession.setHashedKeysRoutine(SRP6ThinbusRoutines.getURoutine());
        srp6ServerSession.setServerEvidenceRoutine(SRP6ThinbusRoutines.getServerEvidenceRoutine());
        return srp6ServerSession;
    }

    public BigInteger generateRandomSalt() {
        SRP6VerifierGenerator gen = generateSRP6VerifierGenerator();
        return new BigInteger(1, gen.generateRandomSalt());
    }

    public SrpStep1Res doRealSrp1(SRP6ServerSession srpSession, UserDTO user) {
        // Retrieve user verifier 'v' + salt 's' from database
        BigInteger v = BigIntegerUtils.fromHex(user.verifier());
        BigInteger salt = BigIntegerUtils.fromHex(user.salt());

        // Compute the public server value 'B'
        BigInteger srp1B = srpSession.step1(user.email(), salt, v);
        return new SrpStep1Res(BigIntegerUtils.toHex(srp1B), BigIntegerUtils.toHex(salt));
    }

    public SrpStep1Res doFakeSrp1(String email, SRP6ServerSession srpSession) {
        BigInteger salt = generateRandomSalt();
        BigInteger srp1B = srpSession.mockStep1(email, salt, BigInteger.ZERO);
        return new SrpStep1Res(BigIntegerUtils.toHex(srp1B), BigIntegerUtils.toHex(salt));
    }

    public SrpStep2Res doRealSrp2(SrpStep2Req req, SRP6ServerSession srpSession, Long userId) throws SRP6Exception {
        BigInteger a = BigIntegerUtils.fromHex(req.srpA());
        BigInteger m1 = BigIntegerUtils.fromHex(req.srpM1());
        BigInteger spr2M2 = srpSession.step2(a, m1);
        return new SrpStep2Res(BigIntegerUtils.toHex(spr2M2), userId);
    }

}