package eu.europa.ec.empl.edci.wallet.web.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class EDCILocaleCookieChangeFilter extends GenericFilterBean {
    private String defaultLocale;
    private String cookieName;
    private int cookieMaxAge;
    private String parameterName;

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public int getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        Locale locale = null;
        Locale localeParameter = this.getLocaleFromRequestParameter(httpServletRequest);
        if (localeParameter != null) {
            locale = localeParameter;
        } else {
            locale = new Locale(defaultLocale);
        }

        Cookie cookie = new Cookie(this.getCookieName(), locale.toString());
        cookie.setMaxAge(this.getCookieMaxAge());
        httpServletResponse.addCookie(cookie);
        chain.doFilter(servletRequest, servletResponse);
    }

    private Locale getLocaleFromRequestParameter(HttpServletRequest httpServletRequest) {
        String parameter = httpServletRequest.getParameter(this.getParameterName());
        if (parameter != null) {
            return new Locale(parameter);
        }
        return null;
    }


}
