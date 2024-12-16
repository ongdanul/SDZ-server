package com.elice.sdz.user.dto.social;

import java.util.Map;

public class KakaoUserInfo extends OAuth2UserInfo {
    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getId() {
        return attributes.get("id") != null ? attributes.get("id").toString() : null;
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        return kakaoAccount != null && kakaoAccount.get("email") != null ? kakaoAccount.get("email").toString() : null;
    }

    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
        return properties != null && properties.get("nickname") != null ? properties.get("nickname").toString() : null;
    }

    @Override
    public String getProfileUrl() {
        Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
        return properties != null && properties.get("profile_image") != null ? properties.get("profile_image").toString() : null;
    }
}