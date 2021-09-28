package myframework.myspringsecurity.domain.account;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_GUEST("ROLE_GUEST"),
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }
}
