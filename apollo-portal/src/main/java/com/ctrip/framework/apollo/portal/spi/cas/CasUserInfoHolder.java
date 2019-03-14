package com.ctrip.framework.apollo.portal.spi.cas;

import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import org.jasig.cas.client.util.AssertionHolder;
import org.jasig.cas.client.validation.Assertion;


public class CasUserInfoHolder implements UserInfoHolder {

    @Override
    public UserInfo getUser() {
        Assertion assertion = AssertionHolder.getAssertion();
        String username = assertion.getPrincipal().getName();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(username);
        return userInfo;
    }
}
