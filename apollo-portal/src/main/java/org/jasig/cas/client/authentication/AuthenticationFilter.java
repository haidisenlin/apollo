//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jasig.cas.client.authentication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.ReflectUtils;
import org.jasig.cas.client.validation.Assertion;

public class AuthenticationFilter extends AbstractCasFilter {
    private String casServerLoginUrl;
    private boolean renew = false;
    private boolean gateway = false;
    private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();
    private AuthenticationRedirectStrategy authenticationRedirectStrategy = new DefaultAuthenticationRedirectStrategy();
    private UrlPatternMatcherStrategy ignoreUrlPatternMatcherStrategyClass = null;
    private static final Map<String, Class<? extends UrlPatternMatcherStrategy>> PATTERN_MATCHER_TYPES = new HashMap();

    public AuthenticationFilter() {
    }

    protected void initInternal(FilterConfig filterConfig) throws ServletException {
        if (!this.isIgnoreInitConfiguration()) {
            super.initInternal(filterConfig);
            this.setCasServerLoginUrl(this.getPropertyFromInitParams(filterConfig, "casServerLoginUrl", (String)null));
            this.logger.trace("Loaded CasServerLoginUrl parameter: {}", this.casServerLoginUrl);
            this.setRenew(this.parseBoolean(this.getPropertyFromInitParams(filterConfig, "renew", "false")));
            this.logger.trace("Loaded renew parameter: {}", this.renew);
            this.setGateway(this.parseBoolean(this.getPropertyFromInitParams(filterConfig, "gateway", "false")));
            this.logger.trace("Loaded gateway parameter: {}", this.gateway);
            String ignorePattern = this.getPropertyFromInitParams(filterConfig, "ignorePattern", (String)null);
            this.logger.trace("Loaded ignorePattern parameter: {}", ignorePattern);
            String ignoreUrlPatternType = this.getPropertyFromInitParams(filterConfig, "ignoreUrlPatternType", "REGEX");
            this.logger.trace("Loaded ignoreUrlPatternType parameter: {}", ignoreUrlPatternType);
            if (ignorePattern != null) {
                Class<? extends UrlPatternMatcherStrategy> ignoreUrlMatcherClass = (Class)PATTERN_MATCHER_TYPES.get(ignoreUrlPatternType);
                if (ignoreUrlMatcherClass != null) {
                    this.ignoreUrlPatternMatcherStrategyClass = (UrlPatternMatcherStrategy)ReflectUtils.newInstance(ignoreUrlMatcherClass.getName(), new Object[0]);
                } else {
                    try {
                        this.logger.trace("Assuming {} is a qualified class name...", ignoreUrlPatternType);
                        this.ignoreUrlPatternMatcherStrategyClass = (UrlPatternMatcherStrategy)ReflectUtils.newInstance(ignoreUrlPatternType, new Object[0]);
                    } catch (IllegalArgumentException var6) {
                        this.logger.error("Could not instantiate class [{}]", ignoreUrlPatternType, var6);
                    }
                }

                if (this.ignoreUrlPatternMatcherStrategyClass != null) {
                    this.ignoreUrlPatternMatcherStrategyClass.setPattern(ignorePattern);
                }
            }

            String gatewayStorageClass = this.getPropertyFromInitParams(filterConfig, "gatewayStorageClass", (String)null);
            if (gatewayStorageClass != null) {
                this.gatewayStorage = (GatewayResolver)ReflectUtils.newInstance(gatewayStorageClass, new Object[0]);
            }

            String authenticationRedirectStrategyClass = this.getPropertyFromInitParams(filterConfig, "authenticationRedirectStrategyClass", (String)null);
            if (authenticationRedirectStrategyClass != null) {
                this.authenticationRedirectStrategy = (AuthenticationRedirectStrategy)ReflectUtils.newInstance(authenticationRedirectStrategyClass, new Object[0]);
            }
        }

    }

    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.casServerLoginUrl, "casServerLoginUrl cannot be null.");
    }

    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        if (this.isRequestUrlExcluded(request)) {
            this.logger.debug("Request is ignored.");
            filterChain.doFilter(request, response);
        } else {
            HttpSession session = request.getSession(false);
            Assertion assertion = session != null ? (Assertion)session.getAttribute("_const_cas_assertion_") : null;
            if (assertion != null) {
                filterChain.doFilter(request, response);
            } else {
                String serviceUrl = this.constructServiceUrl(request, response);
                String ticket = this.retrieveTicketFromRequest(request);
                boolean wasGatewayed = this.gateway && this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);
                if (!CommonUtils.isNotBlank(ticket) && !wasGatewayed) {
                    this.logger.debug("no ticket and no assertion found");
                    String modifiedServiceUrl;
                    if (this.gateway) {
                        this.logger.debug("setting gateway attribute in session");
                        modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
                    } else {
                        modifiedServiceUrl = serviceUrl;
                    }

                    this.logger.debug("Constructed service url: {}", modifiedServiceUrl);
                    String urlToRedirectTo = CommonUtils.constructRedirectUrl(this.casServerLoginUrl, this.getServiceParameterName(), modifiedServiceUrl, this.renew, this.gateway);
                    this.logger.debug("redirecting to \"{}\"", urlToRedirectTo);
                    this.authenticationRedirectStrategy.redirect(request, response, urlToRedirectTo);
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        }
    }

    public final void setRenew(boolean renew) {
        this.renew = renew;
    }

    public final void setGateway(boolean gateway) {
        this.gateway = gateway;
    }

    public final void setCasServerLoginUrl(String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    public final void setGatewayStorage(GatewayResolver gatewayStorage) {
        this.gatewayStorage = gatewayStorage;
    }

    private boolean isRequestUrlExcluded(HttpServletRequest request) {
        if (this.ignoreUrlPatternMatcherStrategyClass == null) {
            return false;
        } else {
            StringBuffer urlBuffer = request.getRequestURL();
            System.out.println(urlBuffer);
            if (request.getQueryString() != null) {
                urlBuffer.append("?").append(request.getQueryString());
            }

            String requestUri = urlBuffer.toString();
            System.out.println(this.ignoreUrlPatternMatcherStrategyClass.matches(requestUri));
            return this.ignoreUrlPatternMatcherStrategyClass.matches(requestUri);
        }
    }

    static {
        PATTERN_MATCHER_TYPES.put("CONTAINS", ContainsPatternUrlPatternMatcherStrategy.class);
        PATTERN_MATCHER_TYPES.put("REGEX", RegexUrlPatternMatcherStrategy.class);
        PATTERN_MATCHER_TYPES.put("EXACT", ExactUrlPatternMatcherStrategy.class);
    }
}
