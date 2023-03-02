package az.bassied.ms.auth.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TokenIssuer {
    BS("bs");
    @Getter
    private final String issuer;

    public static TokenIssuer getEnum(String value) {
        for (TokenIssuer v : values())
            if (v.getIssuer().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }

}
