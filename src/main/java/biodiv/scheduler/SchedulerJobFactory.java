package biodiv.scheduler;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import com.google.inject.Injector;

final class SchedulerJobFactory implements JobFactory {
	
	@Inject
	private Injector guice;

	@Override
	public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) {
		
		JobDetail jobDetail = triggerFiredBundle.getJobDetail();
		Class<? extends Job> jobClass = jobDetail.getJobClass();

		return (Job) guice.getInstance(jobClass);
	}
}