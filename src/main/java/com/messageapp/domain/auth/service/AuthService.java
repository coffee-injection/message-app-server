package com.messageapp.domain.auth.service;

import com.messageapp.domain.auth.dto.JwtTokenResponse;

public interface AuthService {

    JwtTokenResponse kakaoLogin(String authorizationCode);

    JwtTokenResponse googleLogin(String authorizationCode);

    JwtTokenResponse completeSignup(String token, String nickname, String islandName, Integer profileImageIndex);

    void withdrawMember(Long memberId);
}
