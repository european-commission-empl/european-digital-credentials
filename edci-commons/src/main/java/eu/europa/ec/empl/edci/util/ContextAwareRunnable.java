package eu.europa.ec.empl.edci.util;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextAwareRunnable implements Runnable {
    private Runnable task;
    private RequestAttributes context;
    private LocaleContext locale;
    private SecurityContext security;

    public ContextAwareRunnable(Runnable task, RequestAttributes context) {
        this.task = task;
        // Keeps a reference to scoped/context information of parent thread.
        // So original parent thread should wait for the background threads. 
        // Otherwise you should clone context as @Arun A's answer
        this.context = context;
        this.locale = LocaleContextHolder.getLocaleContext();
        this.security = SecurityContextHolder.getContext();
    }

    @Override
    public void run() {
        if (context != null) {
            RequestContextHolder.setRequestAttributes(context);
        }
        if (locale != null) {
            LocaleContextHolder.setLocaleContext(locale);
        }
        if (security != null) {
            SecurityContextHolder.setContext(security);
        }

        try {
            task.run();
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}