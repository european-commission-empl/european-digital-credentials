package eu.europa.ec.empl.edci.locale;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class EDCILocaleParameterCookieResolver implements LocaleResolver {
    private String cookieName;
    private String defaultLocale;
    private String parameterName;
    private int cookieMaxAge;

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
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

    public int getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        Locale locale;
        Cookie cookie = WebUtils.getCookie(httpServletRequest, this.getCookieName());

        Locale localeRequestParameter = this.getLocaleFromRequestParameter(httpServletRequest);
        if (localeRequestParameter != null) {
            locale = localeRequestParameter;
        } else if (cookie != null) {
            locale = new Locale(cookie.getValue());
        } else {
            locale = new Locale(this.getDefaultLocale());
        }

        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
        Locale localeRequestParameter = this.getLocaleFromRequestParameter(httpServletRequest);
        if (locale == null && localeRequestParameter != null) {
            locale = localeRequestParameter;
        }
        Cookie cookie = new Cookie(this.getCookieName(), locale.toString());
        cookie.setMaxAge(this.getCookieMaxAge());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
    }

    private Locale getLocaleFromRequestParameter(HttpServletRequest httpServletRequest) {
        String paramter = httpServletRequest.getParameter(this.getParameterName());
        if (paramter != null) {
            return new Locale(paramter);
        }
        return null;
    }
}
