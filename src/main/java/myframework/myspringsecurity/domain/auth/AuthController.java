package myframework.myspringsecurity.domain.auth;

import lombok.RequiredArgsConstructor;
import myframework.myspringsecurity.dto.AccountRequestDto;
import myframework.myspringsecurity.dto.AccountResponseDto;
import myframework.myspringsecurity.dto.TokenRequestDto;
import myframework.myspringsecurity.dto.TokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AccountResponseDto> signup(@RequestBody AccountRequestDto requestDto) {
        return ResponseEntity.ok(authService.signup(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody AccountRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@RequestBody TokenRequestDto requestDto) {
        return ResponseEntity.ok(authService.reissue(requestDto));
    }

}
