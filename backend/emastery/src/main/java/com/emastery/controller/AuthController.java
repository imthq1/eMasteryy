package com.emastery.controller;

import com.emastery.config.SecurityUtil;
import com.emastery.domain.User;
import com.emastery.domain.reqDTO.LoginRequest;
import com.emastery.domain.reqDTO.ReqDTO;
import com.emastery.domain.resDTO.LoginResponse;
import com.emastery.domain.resDTO.ResLoginDTO;
import com.emastery.service.AuthService;
import com.emastery.service.HealthcheckService;
import com.emastery.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final HealthcheckService healthcheckService;
    public AuthController(AuthService authService, UserService userService,
                          AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil
                        , HealthcheckService healthcheckService) {
        this.authService = authService;
        this.userService = userService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.healthcheckService = healthcheckService;
    }
    @Value("${imthang.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @PostMapping("/register")
    public LoginResponse register(@RequestBody LoginRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@RequestBody ReqDTO user) throws Exception{
        User currentUserDB=this.userService.getUserByEmail(user.getUsername());
        if(currentUserDB==null)
        {
            throw new Exception("User hasn't exists!");
        }
        UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        //xac thuc
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();

        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId()
                    , currentUserDB.getUsername()
                    , currentUserDB.getFullname()
            ,currentUserDB.getGender(),currentUserDB.getAge(),currentUserDB.getLevel());

            resLoginDTO.setUserLogin(userLogin);
        }

        //create a token => can viet ham loadUserByUsername
        String AccessToken = this.securityUtil.createAcessToken(authentication.getName(), resLoginDTO);

        resLoginDTO.setAccessToken(AccessToken);

        //create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(currentUserDB.getUsername(), resLoginDTO);
        //set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token1", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().
                header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }
}