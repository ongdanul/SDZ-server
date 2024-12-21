package com.elice.sdz.user.dto.social;

import java.util.Map;

public class NaverUserInfo extends OAuth2UserInfo {

    public NaverUserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get("response"));
    }

    public String getProvider() {
        return "naver";
    }

    @Override
    public String getId() {
        return attributes.get("id") != null ? attributes.get("id").toString() : null;
    }

    @Override
    public String getEmail() {
        return attributes.get("email") != null ? attributes.get("email").toString() : null;
    }

    @Override
    public String getName() {
        return attributes.get("name") != null ? attributes.get("name").toString() : null;
    }

    @Override
    public String getProfileUrl() {
        return attributes.get("profile_image") != null ? attributes.get("profile_image").toString() : null;
    }
}
