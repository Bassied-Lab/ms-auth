package az.bassied.ms.auth.model.jwt;

import az.bassied.ms.auth.model.enums.TokenIssuer;


public record TokensRequest(String email, TokenIssuer tokenIssuer, Integer count) {
}
