package com.ctrip.framework.apollo.portal.spi.springsecurity;

import com.google.common.collect.Lists;

import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.entity.po.UserPO;
import com.ctrip.framework.apollo.portal.repository.UserRepository;
import com.ctrip.framework.apollo.portal.spi.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

/**
 * @author lepdou 2017-03-10
 */
public class SpringSecurityUserService implements UserService {

  private PasswordEncoder encoder = new BCryptPasswordEncoder();
  private List<GrantedAuthority> authorities;

  @Autowired
  private JdbcUserDetailsManager userDetailsManager;
  @Autowired
  private UserRepository userRepository;

  @PostConstruct
  public void init() {
    authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_user"));
  }

  @Transactional
  public void createOrUpdate(UserPO user,String tableViewOperType) {
    String username = user.getUsername();

    User userDetails = new User(username, encoder.encode(user.getPassword()), authorities);

    if (userDetailsManager.userExists(username)&&tableViewOperType.equals("create")) {
      throw new UnsupportedOperationException("当前用户已存在,不允许创建");
    }else if(userDetailsManager.userExists(username)&&tableViewOperType.equals("update")){
      userDetailsManager.updateUser(userDetails);
    } else if(userDetailsManager.userExists(username)==false&&tableViewOperType.equals("create")) {
      userDetailsManager.createUser(userDetails);
    }else if(userDetailsManager.userExists(username)==false&&tableViewOperType.equals("update")){
      throw new UnsupportedOperationException("待修改的用户不存在");
    }else{
      throw new UnsupportedOperationException("当前操作异常");
    }

    UserPO managedUser = userRepository.findByUsername(username);
    managedUser.setEmail(user.getEmail());

    userRepository.save(managedUser);
  }

  //删除
  @Transactional
  public void delete(String username) {
    UserPO managedUser = userRepository.findByUsername(username);
    if(managedUser==null||managedUser.getUsername()==null||managedUser.getUsername().equals("")){
      throw new UnsupportedOperationException("待删除的用户不存在");
    }else {
      userRepository.delete(managedUser.getId());
    }
  }
  //启用禁用
  @Transactional
  public void enabled(String username,int state) {
    UserPO managedUser = userRepository.findByUsername(username);
    if(managedUser==null||managedUser.getUsername()==null||managedUser.getUsername().equals("")){
      throw new UnsupportedOperationException("待操作的用户不存在");
    }else {
      managedUser.setEnabled(state);
      userRepository.save(managedUser);
    }
  }
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
    return null;
  }


}
