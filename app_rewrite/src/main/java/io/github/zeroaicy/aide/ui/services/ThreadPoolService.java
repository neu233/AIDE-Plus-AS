package io.github.zeroaicy.aide.ui.services;

import android.os.Handler;
import android.os.Looper;
import com.aide.common.AppLog;
import io.github.zeroaicy.util.Log;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import io.github.zeroaicy.aide.utils.Utils;

public class ThreadPoolService implements ExecutorService, ThreadFactory {

	public boolean isCurrentThread() {
		return this.currentThread == Thread.currentThread();
	}
	/**
	 * 实现ExecutorService接口
	 */
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return this.service.awaitTermination(timeout, unit);
	}

	@Override
	public <T extends Object> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return  this.service.invokeAll(tasks);
	}
	@Override
	public <T extends Object> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
		return this.service.invokeAll(tasks, timeout, unit);
	}

	@Override
	public <T extends Object> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
		return this.service.invokeAny(tasks);
	}

	@Override
	public <T extends Object> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
		return this.service.invokeAny(tasks, timeout, unit);
	}

	@Override
	public boolean isShutdown() {
		return  this.service.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return  this.service.isTerminated();
	}

	@Override
	public void shutdown() {
		this.service.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return  this.service.shutdownNow();
	}

	@Override
	public <T extends Object> Future<T> submit(Runnable task, T result) {
		return  this.service.submit(task, result);
	}

	@Override
	public <T extends Object> Future<T> submit(Callable<T> task) {
		return  this.service.submit(task);
	}


	/**
	 * 监听提交
	 */
	@Override
	public Future<?> submit(Runnable runnable) {
		try {
			Group group = new Group(runnable);

			if (!isUiThread()) {
				if (this.keepAliveSingleThread
					&& this.currentThread == Thread.currentThread()) {
					group.run();
					return null;
				}
			}
			return service.submit(group);

		}
		catch (RejectedExecutionException rejectedExecution) {
			// 过滤 shutdownNow()导致的异常
			return null;
		}
	}
	/**
	 * 监听提交任务
	 */
	@Override
	public void execute(Runnable command) {
		Group group = new Group(command);

		if (!isUiThread()) {
			// 在本线程内
			if (this.keepAliveSingleThread
				&& this.currentThread == Thread.currentThread()) {
				group.run();			
				return;
			}
		}
		service.submit(group);
	}

	/**
	 * 查询任务是否为空
	 */
	public boolean isEmptyTask() {
		return this.service.getQueue().size() == 0;
	}


	public static final boolean isDebug = !true;
	public static final boolean isPrint = false;

	private static final String TAG = "ExecutorsService";
	private static final int maxThreadNumber = 8;

	private static Map<String, ThreadPoolService> executorsNameMap = new HashMap<>();

	// 默认线程池为单线程且保持运行
	private static ThreadPoolService defaultThreadPoolService;
	public static ThreadPoolService getDefaultThreadPoolService() {
		if (ThreadPoolService.defaultThreadPoolService == null) {
			ThreadPoolService.defaultThreadPoolService = getThreadPoolService("default", 2);
		}
		return defaultThreadPoolService;
	}

	/**
	 * 单线程
	 */
	public static ThreadPoolService getSingleThreadPoolService(String executorsName) {
		ThreadPoolService temp = ThreadPoolService.executorsNameMap.get(executorsName);
		if (temp == null) {
			temp = new ThreadPoolService(executorsName);
			ThreadPoolService.executorsNameMap.put(executorsName, temp);
		}
		return temp;
	}


	// 主线程
	private static Handler uiHandler = new Handler(Looper.getMainLooper());
	private static Thread uiThread = uiHandler.getLooper().getThread();


	public static final boolean postOfUi(Runnable r) {
		return uiHandler.post(r);
	}
	public static final boolean postDelayedOfUi(Runnable r, long delayMillis) {
		return uiHandler.postDelayed(r, delayMillis);
	}

	public static final void removeCallbacksOfUi(Runnable r) {
		uiHandler.removeCallbacks(r);
	}
	public static boolean isUiThread() {
		return uiThread == Thread.currentThread();
	}



	/**
	 * 空闲时间会关闭线程
	 */
	public static ThreadPoolService getThreadPoolService(String executorsName, int threadNumber) {
		ThreadPoolService temp = executorsNameMap.get(executorsName);
		if (temp == null) {
			temp = new ThreadPoolService(executorsName, threadNumber);
			executorsNameMap.put(executorsName, temp);
		}
		return temp;
	}

	/**
	 * corePoolSize为最大线程数的一半
	 */
	public static ThreadPoolExecutor newFixedThreadPool(int maximumPoolSize, ThreadFactory threadFactory) {
		int corePoolSize = maximumPoolSize == 1 ? 1 : maximumPoolSize / 2;
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
    }


	private static final AtomicInteger poolNumber = new AtomicInteger();
	@Override
	public synchronized Thread newThread(Runnable r) {
		String prefix = this.executorsName;
		if (this.keepAliveSingleThread) {
			if (this.currentThread != null) {
				return this.currentThread;
			}
			this.currentThread = new Thread(r, prefix + "-keep-single-pool-" + poolNumber.incrementAndGet());
			this.currentThread.setDaemon(true);
			return this.currentThread;
		}
		return new Thread(r, prefix + "-pool-" + poolNumber.incrementAndGet());
	}

	// invokeAll阻塞调用处线程
	// 暂时用这种
	private final ThreadPoolExecutor service;

	/**
	 * 长时间单线程池
	 */
	final boolean keepAliveSingleThread;
	Thread currentThread;
	final String executorsName;
	public ThreadPoolService(String executorsName) {
		this.keepAliveSingleThread = true;
		this.executorsName = executorsName;
		this.service = new ThreadPoolExecutor(1, 1, Long.MAX_VALUE, TimeUnit.DAYS, new LinkedBlockingQueue<Runnable>(), this);
	}

	public ThreadPoolService(String executorsName, int threadNumber) {
		this.keepAliveSingleThread = false;
		this.executorsName = executorsName;

		threadNumber = Math.min(threadNumber, maxThreadNumber);
		this.service = newFixedThreadPool(threadNumber, this);
	}

	public ExecutorService getService() {
		return this.service;
	}

	public static class Group implements Runnable {
		public Runnable runnable;
		String stackTrace;
		StackTraceElement[] stackTraces;

		public Group(Runnable runnable) {
			this.runnable = runnable;
			if (isDebug) {
				StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
				String stackTrace = null;
				if (stackTraces != null && stackTraces.length > 4) {
					stackTrace = "来自"  + stackTraces[4];
				}
				setStackTrace(stackTrace);
				setStackTraces(stackTraces);
			}
		}

		public Group setStackTraces(StackTraceElement[] stackTraces) {
			this.stackTraces = stackTraces;
			return this;
		}

		public Group setStackTrace(String stackTrace) {
			this.stackTrace = stackTrace;
			return this;
		}
		public String getStackTrace() {
			return stackTrace;
		}

		@Override
		public void run() {
			if (isDebug && isPrint) {
				AppLog.println_e("\n******************************************************");
				AppLog.println_e(stackTrace);
			}
			long nowTime = Utils.nowTime();
			try {
				this.runnable.run();
			}
			catch (Throwable e) {
				AppLog.e("异步错误", this.stackTrace, e);
			}
			if (isDebug && isPrint) {
				AppLog.d(TAG, "Group::run(): %sms ", Utils.nowTime() - nowTime);
				AppLog.println_e(stackTraces);
				AppLog.println_e("******************************************************\n");
			}
		}
	}
}
