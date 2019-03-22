package com.ctrip.framework.apollo.portal.spi.cas;

import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.entity.po.UserPO;
import com.ctrip.framework.apollo.portal.repository.UserRepository;
import com.ctrip.framework.apollo.portal.spi.UserService;
import com.ctrip.framework.apollo.portal.spi.springsecurity.SpringSecurityUserService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CasUserService extends SpringSecurityUserService implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public List<UserInfo> searchUsers(String keyword, int offset, int limit) {
        List<UserPO> users;
        if (StringUtils.isEmpty(keyword)) {
            users = userRepository.findFirst20ByEnabled(1);
        } else {
            users = userRepository.findByUsernameLikeAndEnabled("%" + keyword + "%", 1);
        }

        List<UserInfo> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(users)) {
            return result;
        }

        result.addAll(users.stream().map(UserPO::toUserInfo).collect(Collectors.toList()));

        return result;
    }

    @Override
    public UserInfo findByUserId(String userId) {
        UserPO userPO = userRepository.findByUsername(userId);
        return userPO == null ? null : userPO.toUserInfo();
    }

    @Override
    public List<UserInfo> findByUserIds(List<String> userIds) {
        if (userIds.contains("apollo")) {
            return Lists.newArrayList(assembleDefaultUser());
        }
        return null;
    }
    private UserInfo assembleDefaultUser() {
        UserInfo defaultUser = new UserInfo();
        defaultUser.setUserId("apollo");
        defaultUser.setName("apollo");
        defaultUser.setEmail("apollo@acme.com");

        return defaultUser;
    }
}
