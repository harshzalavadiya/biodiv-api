package biodiv.scheduler;

import javax.inject.Inject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class SchedulerProvider implements Provider<Scheduler> {

	private Scheduler scheduler;

	@Inject
	public SchedulerProvider(SchedulerJobFactory jobFactory) throws SchedulerException {
		scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.setJobFactory(jobFactory);
	}

	@Override
	public Scheduler get() {
		try {
			if (!scheduler.isStarted())
				scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return scheduler;
	}

}