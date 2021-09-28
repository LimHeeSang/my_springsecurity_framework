package myframework.myspringsecurity.domain.account;

import lombok.RequiredArgsConstructor;
import myframework.myspringsecurity.dto.AccountResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{userId}")
    public ResponseEntity<AccountResponseDto> getLoginAccountInfo(@PathVariable String userId) {
        return ResponseEntity.ok(accountService.getAccountInfo(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponseDto> getAccountInfo() {
        return ResponseEntity.ok(accountService.getLoginAccountInfo());
    }

    /**
     * 접근 권한 test controller
     */
    @GetMapping("/test")
    public String testController() {
        return "test controller 입니다.";
    }


}
