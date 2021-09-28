package myframework.myspringsecurity.domain.account;

import lombok.RequiredArgsConstructor;
import myframework.myspringsecurity.dto.AccountResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public AccountResponseDto getAccountInfo(String userId) {
        Account findAccount = accountRepository.findByUserId(userId).orElseThrow(
                () -> new NoSuchElementException("유저 정보가 없습니다.")
        );

        AccountResponseDto responseDto = AccountResponseDto.builder()
                .userId(findAccount.getUserId())
                .userRole(findAccount.getUserRole())
                .build();

        return responseDto;
    }

    @Transactional(readOnly = true)
    public AccountResponseDto getLoginAccountInfo() {
        Account findAccount = accountRepository.findByUserId(getCurrenAccountRealId()).orElseThrow(
                () -> new NoSuchElementException("로그인 유저 정보가 없습니다.")
        );

        AccountResponseDto responseDto = AccountResponseDto.builder()
                .userId(findAccount.getUserId())
                .userRole(findAccount.getUserRole())
                .build();

        return responseDto;
    }


    private String getCurrenAccountRealId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new NoSuchElementException("Security Context에 인증 정보가 없습니다.");
        }

        return authentication.getName();
    }

}
