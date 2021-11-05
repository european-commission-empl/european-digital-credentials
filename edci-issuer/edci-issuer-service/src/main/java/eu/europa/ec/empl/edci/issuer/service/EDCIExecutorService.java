package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.util.ContextAwareCallable;
import eu.europa.ec.empl.edci.util.ContextAwareRunnable;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class EDCIExecutorService {

    private static final Logger logger = Logger.getLogger(CredentialService.class);

    private Map<String, ExecutorService> executors = new HashMap<String, ExecutorService>();

    public void createExecutor(String name, int numThreads) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        this.getExecutors().put(name, executor);
        logger.debug("Executor named " + name + " created with a fixed thread pool of " + numThreads);
    }

    protected ExecutorService getExecutor(String name) {
        ExecutorService executor = this.getExecutors().get(name);
        if (executor == null) {
            throw new EDCIException().addDescription(String.format("Executor %s was not found", name));
        }
        return executor;
    }

    public Future submitTask(String executorName, Runnable task) {
        ExecutorService executor = this.getExecutors().get(executorName);
        ContextAwareRunnable contextAwareRunnable = new ContextAwareRunnable(task, RequestContextHolder.currentRequestAttributes());
        return executor.submit(contextAwareRunnable);
    }

    public Future submitTask(String executorName, Callable task) {
        ExecutorService executor = this.getExecutors().get(executorName);
        ContextAwareCallable contextAwareRunnable = new ContextAwareCallable(task, RequestContextHolder.currentRequestAttributes());
        return executor.submit(contextAwareRunnable);
    }

    public void shutdownAndAwaitTermination(String executorName, Long timeOutMinutes) {
        ExecutorService executor = this.getExecutor(executorName);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeOutMinutes, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

    }

    public void shutdown(String executorName, boolean shutdownNow) {
        ExecutorService executor = this.getExecutor(executorName);
        if (shutdownNow) {
            executor.shutdownNow();
        } else {
            executor.shutdown();
        }
    }

    /**
     * Run a task using a defined amount of threads
     *
     * @param task           task to run
     * @param context        context attributes to be used
     * @param numThreads     number of threads
     * @param timeOutMinutes maximum timeout
     */
    /*public void runTask(Runnable task, RequestAttributes context, int numThreads, Long timeOutMinutes) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ContextAwareRunnable contextAwareRunnable = new ContextAwareRunnable(task, context);
        executor.submit(contextAwareRunnable);

        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeOutMinutes, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }


    private Future callTask(ExecutorService executor, Callable task, RequestAttributes context, Long timeOutMinutes) throws Exception {
        ContextAwareCallable contextAwareCallable = new ContextAwareCallable(task, context);
        Future future = executor.submit(contextAwareCallable);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeOutMinutes, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        return future;
    }*/
    public Map<String, ExecutorService> getExecutors() {
        return executors;
    }

    public void setExecutors(Map<String, ExecutorService> executors) {
        this.executors = executors;
    }
}
