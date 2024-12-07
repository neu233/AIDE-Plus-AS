package com.aide.ui.services;
import android.os.Handler;
import android.os.Looper;
import com.aide.ui.ServiceContainer;
import com.aide.ui.trainer.TrainerState;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ZeroAicyTrainerService extends TrainerService {
	static TrainerService mTrainerService;
	public static TrainerService getSingleton() {
		if (mTrainerService == null) {
			// 异步总是有问题
			mTrainerService = //new TrainerService();
				new ZeroAicyTrainerService();
		}
		return mTrainerService;
	}

	public class cZeroAicy extends com.aide.ui.trainer.Course {
		private AtomicBoolean inited = new AtomicBoolean(false);
		private final Lock lock = new ReentrantLock();
		private final Condition condition = lock.newCondition();

		//构造器
		public cZeroAicy() {
			// 使用此父类构造器[空实现]
			super(false);
			// 使用父构造器的实现
			// 并发
			this.j6 = new Vector<com.aide.ui.trainer.Course.XmlInfo>();
			// 异步初始化
			executorsService.submit(new Runnable(){
					@Override
					public void run() {
						initAsync();
					}
				});
		}

		// 阻塞点
		// this.EQ = this.tp.P8(this.j6.getCurrentLessonId());
		@Override
		public com.aide.ui.trainer.Course.XmlInfo P8(String string) {
			// 没准备好就阻塞[实在没办法了]
			// 不过卡顿检测并没有检测到耗时
			if (!inited.get()) {
				doSomething();
			}
			return super.P8(string);
		}

		// 等待[阻塞线程]
		public void doSomething() {
			lock.lock();
			try {
				// 等待条件
				condition.await();
				// 执行其他操作
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				lock.unlock();
			}
		}

		public void notifyThread() {
			lock.lock();
			try {
				// 唤醒线程
				condition.signalAll();
			}
			finally {
				lock.unlock();
			}
		}

		public void initAsync() {
			synchronized (this.j6) {
				int index = 0;
				for (ProjectSupport projectSupport : ServiceContainer.getProjectSupports()) {
					List<com.aide.ui.trainer.Course.File> trainerCourses = projectSupport.getTrainerCourses();
					if (trainerCourses == null) {
						continue;
					}
					for (com.aide.ui.trainer.Course.File cVar : trainerCourses) {
						index = index + 1;
						try {
							com.aide.ui.trainer.Course.XmlInfo vy = parseCourseXmlFile(cVar.fileName, index, cVar);
							if (vy.j3()) {
								this.j6.add(vy);
							}
						}
						catch (Exception e) {
							this.DW = e.toString();
						}
					}

				}
				Collections.sort(this.j6, new com.aide.ui.trainer.Course.a());
			}
			// 采用回调方式调用
			J0();
			inited.set(true);

			// 唤醒线程
			notifyThread();
		}
	}

	// 默认子线程
	ThreadPoolService executorsService = ThreadPoolService.getDefaultThreadPoolService();
	// ui进程
	private final Handler mainHandler = new Handler(Looper.getMainLooper());

	// 原来的实现是每次都new一个Handler
	@Override
	public void hz() {
		mainHandler.postDelayed(new a(), 1000L);
	}
	@Override
	public void init() {
		this.trainerState = new TrainerState();
		// 替换 com.aide.ui.trainer.Course tp 的实现
		this.tp = new cZeroAicy();
	}

	protected void J0() {
		String EQ = this.tp.EQ(this.trainerState.getCurrentLessonId());
		if (EQ.equals(this.trainerState.getCurrentLessonId())) {
			return;
		}
		this.trainerState.startLesson(EQ);
	}

	// Labcd/gb;[教程]::isVisible
	
	@Override
	public boolean dx() {
		if (true) {
			return true;		
		}
		// 有点慢
		return super.dx();
	}


}
