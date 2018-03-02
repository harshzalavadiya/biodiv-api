package biodiv.scheduler;

import org.quartz.Scheduler;

import com.google.inject.AbstractModule;

public class SchedulerModule extends AbstractModule {
	
	@Override
	protected void configure() {
		
		bind(DownloadJob.class);
		
		bind(Scheduler.class).toProvider(SchedulerProvider.class);
	}
}
