package com.urzaizcoding.subscriber.utils.task;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static volatile AppExecutors instance;
    private Executor uiThreadExecutor;
    private ExecutorService diskIOExecutor;
    private static final int THREAD_POOL_SIZE = 2;

    private AppExecutors(Executor uiThreadExecutor, ExecutorService diskIOExecutor) {
        this.uiThreadExecutor = uiThreadExecutor;
        this.diskIOExecutor = diskIOExecutor;
    }

    private AppExecutors(){
        this(new UIThreadExecutor(), Executors.newFixedThreadPool(THREAD_POOL_SIZE));
    }

    public Executor uiThread(){
        return this.uiThreadExecutor;
    }

    public ExecutorService diskIO(){
        return this.diskIOExecutor;
    }

    public static AppExecutors getInstance(){
        if(instance == null){
            synchronized (AppExecutors.class){
                instance = new AppExecutors();
            }
        }
        return instance;
    }

    private static class UIThreadExecutor implements Executor {
        private Handler handler;

        public UIThreadExecutor() {
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void execute(Runnable runnable) {
            handler.post(runnable);
        }
    }
}
