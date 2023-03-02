package az.bassied.ms.auth.model.srp;

public record SrpStep2Req(String email, String srpA, String srpM1) {
}
