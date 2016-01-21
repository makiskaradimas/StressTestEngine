package engine.thread.executors;

import engine.stepdefs.tools.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import engine.thread.controllers.StatisticsController;
import engine.thread.controllers.ThroughputController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author ekaradimas
 * @since 9/2/2015
 */
@Service("CountDownMetaLatchExecutor")
@Scope("prototype")
public class CountDownMetaLatchExecutor {

    @Autowired
    StatisticsController statisticsController;

    @Autowired
    ThroughputController throughputController;

    @Autowired
    Config config;

    private final Set<CountDownLatchExecutor> countDowns;

    public CountDownMetaLatchExecutor(List<CountDownLatchExecutor> countDowns)
    {
        this.countDowns = new HashSet<CountDownLatchExecutor>(countDowns);
    }

    public void executeThreads() throws Exception
    {
        for (CountDownLatchExecutor countDown : countDowns)
        {
            countDown.executeThreads();
        }
        this.await();
    }

    public boolean executionSuccessful() {
        for (CountDownLatchExecutor countDown : countDowns) {
            if (!countDown.threadsSuccessful())
                return false;
        }
        return true;
    }

    public void await() throws Exception
    {
        for (CountDownLatchExecutor count : countDowns)
            count.latchAwait();
    }

    public boolean await(long timeout, TimeUnit unit)
            throws InterruptedException {
        boolean success = true;
        ExecutorService e = Executors.newFixedThreadPool(countDowns.size());
        List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>(countDowns.size());

        for (CountDownLatchExecutor count : countDowns)
            futures.add(e.submit(new SingleLatchTimeOutAwaiter(count, timeout, unit)));

        for (Future<Boolean> f : futures) {
            try {
                success &= f.get();
            } catch (ExecutionException e1) {
                throw new InterruptedException();
            }
        }
        e.shutdown();
        return success;
    }

    private static class SingleLatchTimeOutAwaiter implements Callable<Boolean> {
        private final CountDownLatchExecutor count;
        private final long timeout;
        private final TimeUnit unit;

        private SingleLatchTimeOutAwaiter(CountDownLatchExecutor count, long timeout,
                                          TimeUnit unit) {
            this.count = count;
            this.timeout = timeout;
            this.unit = unit;
        }

        public Boolean call() throws Exception {
            return count.latchAwait(timeout, unit);
        }
    }
}
