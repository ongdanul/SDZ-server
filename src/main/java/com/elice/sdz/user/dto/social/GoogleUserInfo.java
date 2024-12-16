package com.elice.sdz.user.dto.social;

import java.util.Map;

public class GoogleUserInfo extends OAuth2UserInfo {

    public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getId() {
        return attributes.get("sub") != null ? attributes.get("sub").toString() : null;
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
        return attributes.get("picture") != null ? attributes.get("picture").toString() : null;
    }
}
