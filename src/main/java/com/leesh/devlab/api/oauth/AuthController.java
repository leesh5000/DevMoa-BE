package com.leesh.devlab.api.oauth;

import com.leesh.devlab.api.oauth.dto.OauthLoginDto;
import com.leesh.devlab.api.oauth.dto.RefreshTokenDto;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.resolver.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.leesh.devlab.util.HttpHeaderUtils.extractAuthorization;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    /**
     * 소셜 계정 로그인 API
     * @param request
     * @return
     */
    @PostMapping(path = "/oauth-login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OauthLoginDto.Response> oauthLogin(@RequestBody OauthLoginDto.Request request) {

        OauthLoginDto.Response response = authService.oauthLogin(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 액세스 토큰 갱신 API
     */
    @GetMapping(path = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefreshTokenDto> refreshToken(HttpServletRequest request) {

        String refreshToken = extractAuthorization(request);

        RefreshTokenDto refreshTokenDto = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(refreshTokenDto);
    }

    @GetMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(@LoginMember MemberInfo memberInfo) {

        authService.logout(memberInfo);

        return ResponseEntity.noContent().build();
    }

}