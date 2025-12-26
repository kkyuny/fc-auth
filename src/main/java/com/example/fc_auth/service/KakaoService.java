package com.example.fc_auth.service;

import com.example.fc_auth.model.KakaoTokenRespDto;
import com.example.fc_auth.model.KakaoUserInfoRespDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoService {
    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @Value("${kakao.client_secret}")
    private String clientSecret;

    private final String KAKAO_AUTH_URL = "https://kauth.kakao.com";
    private final String KAKAO_USER_URL = "https://kapi.kakao.com";

    public KakaoUserInfoRespDto getUserFromKakao(String accessToken){
        return WebClient.create(KAKAO_USER_URL)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .header(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(KakaoUserInfoRespDto.class)
                .block();
    }

    public String getAccessTokenFromKakao(String code){
        KakaoTokenRespDto kakaoTokenRespDto =
                WebClient.create(KAKAO_AUTH_URL)
                        .post()
                        .uri("/oauth/token")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                                .with("client_id", clientId)
                                .with("redirect_uri", redirectUri)
                                .with("client_secret", clientSecret)
                                .with("code", code)
                        )
                        .retrieve()
                        .bodyToMono(KakaoTokenRespDto.class)
                        .block();


        return kakaoTokenRespDto.getAccessToken();
    }
}
