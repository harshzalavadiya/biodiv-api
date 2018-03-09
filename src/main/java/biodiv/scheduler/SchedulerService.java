package biodiv.scheduler;

import java.util.concurrent.atomic.AtomicLong;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.google.inject.Inject;

import biodiv.maps.MapSearchQuery;
import biodiv.user.User;

public class SchedulerService {

	private Scheduler scheduler;
	private static final AtomicLong counter = new AtomicLong(0);

	@Inject
	public SchedulerService(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	private void scheduleJob(Trigger trigger) throws SchedulerException {
		JobDetail eventJob = JobBuilder.newJob(DownloadJob.class).build();
		scheduler.scheduleJob(eventJob, trigger);
	}

	private TriggerBuilder<Trigger> newJobTrigger(JobDataMap jobDataMap) {
		
		return TriggerBuilder.newTrigger().withIdentity(Long.toString(counter.getAndIncrement())).usingJobData(jobDataMap);
	}

	public SchedulerStatus scheduleNow(String index, String type, User user, MapSearchQuery mapSearchQuery, String notes, String url) {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(DownloadJob.INDEX_KEY, index);
		jobDataMap.put(DownloadJob.TYPE_KEY, type);
		jobDataMap.put(DownloadJob.USER_KEY, user);
		jobDataMap.put(DownloadJob.DATA_KEY, mapSearchQuery);
		jobDataMap.put(DownloadJob.SEARCH_KEY, url);
		jobDataMap.put(DownloadJob.NOTES_KEY, notes);
		
		Trigger trigger = newJobTrigger(jobDataMap).startNow().build();
		try {
			scheduleJob(trigger);
			return SchedulerStatus.Scheduled;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return SchedulerStatus.Failed;
		}
	}
}
