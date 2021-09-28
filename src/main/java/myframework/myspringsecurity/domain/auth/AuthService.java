package myframework.myspringsecurity.domain.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myframework.myspringsecurity.domain.account.Account;
import myframework.myspringsecurity.domain.account.AccountRepository;
import myframework.myspringsecurity.domain.account.UserRole;
import myframework.myspringsecurity.domain.refresh.RefreshToken;
import myframework.myspringsecurity.domain.refresh.RefreshTokenRepository;
import myframework.myspringsecurity.dto.AccountRequestDto;
import myframework.myspringsecurity.dto.AccountResponseDto;
import myframework.myspringsecurity.dto.TokenRequestDto;
import myframework.myspringsecurity.dto.TokenResponseDto;
import myframework.myspringsecurity.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public AccountResponseDto signup(AccountRequestDto accountRequestDto) {

        if (accountRepository.existsByUserId(accountRequestDto.getUserId())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        Account account = Account.builder()
                .userId(accountRequestDto.getUserId())
                .password(passwordEncoder.encode(accountRequestDto.getPassword()))
                .userRole(UserRole.ROLE_USER)
                .build();

        Account saveAccount = accountRepository.save(account);

        AccountResponseDto responseDto = AccountResponseDto.builder().
                userId(saveAccount.getUserId())
                .userRole(saveAccount.getUserRole())
                .build();

        return responseDto;
    }

    @Transactional
    public TokenResponseDto login(AccountRequestDto accountRequestDto) {
        // 1. login id/pw를 기반으로 authenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                accountRequestDto.getUserId(), accountRequestDto.getPassword());

        // 2. 실제로 검증(사용자 아이디와 비밀번호 체크)이 이루어지는 부분
        // authenticate 메소드가 실행이 될 때 UserDetailsServiceImpl에서 만들었던 loadUserByUsername 메소드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증객체를 기반으로 Jwt 토큰 생성
        TokenResponseDto tokenResponseDto = jwtTokenProvider.generateTokenDto(authentication);

        // 4. refreshToken 저장

        Account findAccount = accountRepository.findByUserId(authentication.getName()).orElseThrow(
                () -> new UsernameNotFoundException("등록되지 않은 Id 입니다.")
        );

        RefreshToken refreshToken = findAccount.setRefreshToken(tokenResponseDto.getRefreshToken());

        /*RefreshToken refreshToken = RefreshToken.builder()
                .account.setUserId(authentication.getName())
                .refreshValue(tokenResponseDto.getRefreshToken())
                .build();*/

        refreshTokenRepository.save(refreshToken);

        return tokenResponseDto;
    }

    @Transactional
    public TokenResponseDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. Access Token에서 Member Id 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 Member Id를 기반으로 Refresh Token 가져오기
        Account findAccount = accountRepository.findByUserId(authentication.getName()).orElseThrow(
                () -> new UsernameNotFoundException("등록된 Id가 없습니다.")
        );

        RefreshToken refreshToken = findAccount.getRefreshToken().orElseThrow(
                () -> new RuntimeException("로그아웃 된 사용자입니다.")
        );

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getRefreshValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenResponseDto tokenResponseDto = jwtTokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenResponseDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return tokenResponseDto;
    }
}
