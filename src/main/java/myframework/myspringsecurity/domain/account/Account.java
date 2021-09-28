package myframework.myspringsecurity.domain.account;

import lombok.*;
import myframework.myspringsecurity.domain.refresh.RefreshToken;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "refresh_id")
    private RefreshToken refreshToken;

    private String userId;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    /**
     * refresh token 할당
     */
    public RefreshToken setRefreshToken(String tokenValue) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshValue(tokenValue);
        this.refreshToken = refreshToken;

        return refreshToken;
    }

    /**
     * refresh token
     */
    public Optional<RefreshToken> getRefreshToken() {
        return Optional.ofNullable(refreshToken);
    }
}
