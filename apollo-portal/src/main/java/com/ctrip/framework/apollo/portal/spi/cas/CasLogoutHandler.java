package com.ctrip.framework.apollo.portal.spi.cas;

import com.ctrip.framework.apollo.portal.spi.LogoutHandler;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

public class CasLogoutHandler implements LogoutHandler {
    @Value("${server.name}")
    private String servername;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        //将session销毁
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        //重定向到Cas的logout地址
        String casServerUrl = "http://sso.sunnyoptical.cn";
        String serverName = servername+"/?"+new Date();

        try {
            response.sendRedirect(casServerUrl + "/logout?service=" + serverName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
