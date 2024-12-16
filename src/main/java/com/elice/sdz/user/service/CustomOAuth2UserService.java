package com.elice.sdz.user.service;

import com.elice.sdz.global.exception.CustomOauth2Exception;
import com.elice.sdz.user.dto.CustomOAuth2User;
import com.elice.sdz.user.dto.Oauth2DTO;
import com.elice.sdz.user.dto.social.GoogleUserInfo;
import com.elice.sdz.user.dto.social.KakaoUserInfo;
import com.elice.sdz.user.dto.social.NaverUserInfo;
import com.elice.sdz.user.dto.social.OAuth2UserInfo;
import com.elice.sdz.user.entity.SocialUsers;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.SocialRepository;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final SocialRepository socialRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("Test - CustomOAuth2UserService OAuth2User attributes: {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo;

        switch (provider) {
            case "kakao"->
                    oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
            case "naver" ->
                    oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
            case "google" ->
                    oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            default ->
                    throw new OAuth2AuthenticationException("Unsupported Provider");
        }
        return oAuth2UserProcess(oAuth2UserInfo);
    }

    private OAuth2User oAuth2UserProcess(OAuth2UserInfo oAuth2UserInfo) {
        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getId();
        String userId = provider + "_" + providerId;

        return userRepository.findByUserId(userId)
                .map(user -> {
                    if (!user.isSocial()) {
                        log.warn("The ID is already registered as a local user");
                        throw new CustomOauth2Exception("해당 ID로 이미 일반 회원가입이 되어 있습니다.\n일반 로그인을 시도해 주세요.");
                    }
                    return loadExistingSocialUser(user, oAuth2UserInfo);
                })
                .orElseGet(() -> {
                    socialRepository.findBySocialProviderAndSocialProviderId(provider, providerId)
                            .ifPresent(socialUser -> {
                                log.warn("Social user already exists: {}", socialUser.getUserId());
                                throw new CustomOauth2Exception("해당 소셜 계정으로 이미 가입되어 있습니다.");
                            });
                    return registerNewUser(oAuth2UserInfo);
                });
    }

    private OAuth2User loadExistingSocialUser(Users user, OAuth2UserInfo oAuth2UserInfo) {
        Oauth2DTO oauth2DTO = Oauth2DTO.builder()
                .userId(user.getUserId())
                .userName(oAuth2UserInfo.getName())
                .authorities(user.getUserAuth().name())
                .build();

        return new CustomOAuth2User(oauth2DTO);
    }

    private OAuth2User registerNewUser(OAuth2UserInfo oAuth2UserInfo) {

        Users user = Users.builder()
                .userId(oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getId())
                .userPassword(null)
                .userAuth(Users.Auth.ROLE_USER)
                .userName(oAuth2UserInfo.getName())
                .contact(null)
                .email(oAuth2UserInfo.getEmail() != null && !oAuth2UserInfo.getEmail().isEmpty() ? oAuth2UserInfo.getEmail() : "socialUser")
                .profileUrl(oAuth2UserInfo.getProfileUrl() != null && !oAuth2UserInfo.getProfileUrl().isEmpty() ? oAuth2UserInfo.getProfileUrl() : null)
                .social(true)
                .build();
        userRepository.save(user);

        SocialUsers socialUser = new SocialUsers();
        socialUser.setUserId(user);
        socialUser.setSocialProvider(oAuth2UserInfo.getProvider());
        socialUser.setSocialProviderId(oAuth2UserInfo.getId());
        socialRepository.save(socialUser);

        Oauth2DTO oauth2DTO = Oauth2DTO.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .authorities(user.getUserAuth().name())
                .build();

        return new CustomOAuth2User(oauth2DTO);
    }
}