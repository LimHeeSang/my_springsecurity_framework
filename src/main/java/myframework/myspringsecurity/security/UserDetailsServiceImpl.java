package myframework.myspringsecurity.security;

import lombok.RequiredArgsConstructor;
import myframework.myspringsecurity.domain.account.Account;
import myframework.myspringsecurity.domain.account.AccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "은 등록된 사용자가 아닙니다."));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(account.getUserRole().toString());

        return new User(
                account.getUserId(),
                account.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }


    // DB 에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(Account account) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(account.getUserRole().toString());

        return new User(
                String.valueOf(account.getId()), //getId() -> getUserId()
                account.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
