package myframework.myspringsecurity.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class TokenResponseDto {

    private String grantType;

    private Long accessTokenExpiresIn;

    private String accessToken;

    private String refreshToken;

}
