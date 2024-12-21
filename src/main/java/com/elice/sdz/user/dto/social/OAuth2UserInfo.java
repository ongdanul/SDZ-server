package com.elice.sdz.user.dto.social;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class OAuth2UserInfo {

    protected final Map<String, Object> attributes;

    public abstract String getProvider();

    public abstract String getId();

    public abstract String getEmail();

    public abstract String getName();

    public abstract String getProfileUrl();
}
