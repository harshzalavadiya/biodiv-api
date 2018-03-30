package biodiv.scheduler;

import org.quartz.Scheduler;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;

public class SchedulerModule extends ServletModule  {
	
	@Override
	protected void configureServlets() {
		
		bind(DownloadJob.class);
		
		bind(Scheduler.class).toProvider(SchedulerProvider.class);
	}
}
