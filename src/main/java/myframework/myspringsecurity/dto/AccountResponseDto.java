package myframework.myspringsecurity.dto;

import lombok.Builder;
import lombok.Getter;
import myframework.myspringsecurity.domain.account.UserRole;

@Getter
@Builder
public class AccountResponseDto {
    private String userId;

    private UserRole userRole;
}
