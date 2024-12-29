    package com.elice.sdz.user.service;

    import com.elice.sdz.global.exception.CustomException;
    import com.elice.sdz.global.exception.ErrorCode;
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
                        throw new CustomException(ErrorCode.OAUTH2_AUTHENTICATION_FAILED);
            }
            return oAuth2UserProcess(oAuth2UserInfo);
        }

        private OAuth2User oAuth2UserProcess(OAuth2UserInfo oAuth2UserInfo) {
            String provider = oAuth2UserInfo.getProvider();
            String providerId = oAuth2UserInfo.getId();
            String email = provider + "_" + providerId;

            return userRepository.findById(email)
                    .map(user -> {
                        if (!user.isSocial()) {
                            log.warn("이미 일반 회원으로 등록된 아이디입니다.");
                            throw new CustomException(ErrorCode.SOCIAL_USER_EXISTS);
                        }
                        return loadExistingSocialUser(user, oAuth2UserInfo);
                    })
                    .orElseGet(() -> {
                        socialRepository.findBySocialProviderAndSocialProviderId(provider, providerId)
                                .ifPresent(socialUser -> {
                                    log.warn("이미 가입된 소셜 회원입니다.: {}", socialUser.getUser());
                                    throw new CustomException(ErrorCode.SOCIAL_USER_EXISTS);
                                });
                        return registerNewUser(oAuth2UserInfo);
                    });
        }

        private OAuth2User loadExistingSocialUser(Users user, OAuth2UserInfo oAuth2UserInfo) {
            Oauth2DTO oauth2DTO = Oauth2DTO.builder()
                    .email(user.getEmail())
                    .userName(oAuth2UserInfo.getName())
                    .authorities(user.getUserAuth().name())
                    .build();

            return new CustomOAuth2User(oauth2DTO);
        }

        private OAuth2User registerNewUser(OAuth2UserInfo oAuth2UserInfo) {

            Users user = Users.builder()
                    .email(oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getId())
                    .userAuth(Users.Auth.ROLE_USER)
                    .userName(oAuth2UserInfo.getName())
                    .profileUrl(oAuth2UserInfo.getProfileUrl() != null && !oAuth2UserInfo.getProfileUrl().isEmpty() ? oAuth2UserInfo.getProfileUrl() : null)
                    .social(true)
                    .build();
            userRepository.save(user);

            SocialUsers socialUser = new SocialUsers();
            socialUser.setUser(user);
            socialUser.setSocialProvider(oAuth2UserInfo.getProvider());
            socialUser.setSocialProviderId(oAuth2UserInfo.getId());
            socialRepository.save(socialUser);

            Oauth2DTO oauth2DTO = Oauth2DTO.builder()
                    .email(user.getEmail())
                    .userName(user.getUserName())
                    .authorities(user.getUserAuth().name())
                    .build();

            return new CustomOAuth2User(oauth2DTO);
        }
    }