package com.ctrip.framework.apollo.portal.controller;

import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.entity.po.UserPO;
import com.ctrip.framework.apollo.portal.repository.UserRepository;
import com.ctrip.framework.apollo.portal.spi.LogoutHandler;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import com.ctrip.framework.apollo.portal.spi.UserService;
import com.ctrip.framework.apollo.portal.spi.springsecurity.SpringSecurityUserService;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserInfoController {

    @Autowired
    private UserInfoHolder userInfoHolder;
    @Autowired
    private LogoutHandler logoutHandler;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;


    @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public void createOrUpdateUser(@RequestBody UserPO user) {
        if (StringUtils.isContainEmpty(user.getUsername(), user.getPassword())) {
            throw new BadRequestException("Username and password can not be empty.");
        }

        if (userService instanceof SpringSecurityUserService) {
            ((SpringSecurityUserService) userService).createOrUpdate(user);
        } else {
            throw new UnsupportedOperationException("Create or update user operation is unsupported");
        }

    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public UserInfo getCurrentUserName() {
        return userInfoHolder.getUser();
    }

    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logoutHandler.logout(request, response);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<UserInfo> searchUsersByKeyword(@RequestParam(value = "keyword") String keyword,
                                               @RequestParam(value = "offset", defaultValue = "0") int offset,
                                               @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return userService.searchUsers(keyword, offset, limit);
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public UserInfo getUserByUserId(@PathVariable String userId) {
        return userService.findByUserId(userId);
    }


    @RequestMapping(value = "/user/delete/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user/totalPage", method = RequestMethod.GET)
    public Map<String, Integer> totalPage() {
        PageRequest pageable = new PageRequest(1, 10);
        Page<UserPO> page = userRepository.findAll(pageable);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("totalPage", page.getTotalPages());
        return map;
    }

    @RequestMapping(value = "/user/getPage", method = RequestMethod.GET)
    public Page<UserPO> getPage(@RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "size", defaultValue = "10") int size) {
        PageRequest pageable = new PageRequest(page, size);
        Page<UserPO> userPage = userRepository.findAll(pageable);
        System.out.println(userPage);
        return userPage;
    }

}
