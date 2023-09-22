package com.leesh.devlab.external.implementation.google.dto;

import com.leesh.devlab.constant.OauthType;
import com.leesh.devlab.external.abstraction.OauthMemberInfo;

public record GoogleMemberInfo(String sub, String email, String picture) implements OauthMemberInfo {

    @Override
    public String getId() {
        return sub;
    }

    @Override
    public OauthType getOauthType() {
        return OauthType.GOOGLE;
    }

}
