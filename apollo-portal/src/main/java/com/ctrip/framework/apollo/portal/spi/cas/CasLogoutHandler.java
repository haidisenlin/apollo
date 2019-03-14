package com.ctrip.framework.apollo.portal.spi.cas;

import com.ctrip.framework.apollo.portal.spi.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CasLogoutHandler implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        //将session销毁
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        //重定向到Cas的logout地址
        String casServerUrl = "http://sso.sunnyoptical.cn";
        String serverName = "http://127.0.0.1:8070/";

        try {
            response.sendRedirect(casServerUrl + "/logout?service=" + serverName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
