package eu.europa.ec.empl.edci.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

public class ContextAwareCallable<T> implements Callable<T> {
    private Callable<T> task;
    private RequestAttributes context;
    private LocaleContext locale;
    private SecurityContext security;

    private static final Logger logger = LogManager.getLogger(ContextAwareCallable.class);

    public ContextAwareCallable(Callable task, RequestAttributes context) {
        this.task = task;
        // Keeps a reference to scoped/context information of parent thread.
        // So original parent thread should wait for the background threads. 
        // Otherwise you should clone context as @Arun A's answer
        this.context = context;
        this.locale = LocaleContextHolder.getLocaleContext();
        this.security = SecurityContextHolder.getContext();
    }

    @Override
    public T call() throws Exception {
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
            return task.call();
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}