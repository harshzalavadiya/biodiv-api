package biodiv.scheduler;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import biodiv.Transactional;
import biodiv.maps.MapHttpResponse;
import biodiv.maps.MapIntegrationService;
import biodiv.maps.MapSearchQuery;
import biodiv.observation.ObservationList;
import biodiv.scheduler.DownloadLog.DownloadType;
import biodiv.user.User;

public class DownloadJob implements Job {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String INDEX_KEY = "index";
	public static final String TYPE_KEY = "type";
	public static final String DATA_KEY = "data";
	public static final String USER_KEY = "user";
	public static final String SEARCH_KEY = "search";
	
	@Inject
	MapIntegrationService mapIntegrationService;
	
	@Inject
	DownloadLogService downloadLogService;
	
	public DownloadJob() {
		// Instances of Job must have a public no-argument constructor.
	}

	@Transactional
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		JobDataMap data = context.getMergedJobDataMap();
		
		String index = data.getString(INDEX_KEY);
		String indexType = data.getString(TYPE_KEY);
		MapSearchQuery mapSearchQuery = (MapSearchQuery) data.get(DATA_KEY);
		
		User user = (User) data.get(USER_KEY);
		String filterUrl = data.getString("filterUrl");
		SchedulerStatus status = SchedulerStatus.SCHEDULED;
		DownloadType type = DownloadType.CSV;
		
		DownloadLog downloadLog = new DownloadLog(user, filterUrl, status, type, 0);
		downloadLogService.save(downloadLog);
		
		String url = ObservationList.URL + "naksha/services/download/" + index + "/" + indexType; 
		MapHttpResponse httpResponse = mapIntegrationService.postRequest(url, mapSearchQuery);
		
		String filePath = null;
		try {
			filePath = EntityUtils.toString((HttpEntity) httpResponse.getDocument());
		} catch (ParseException | IOException e) {
			e.printStackTrace();
			log.error("Error while reading response");
		}
		
		log.debug("Download file generated at {}", filePath);
		downloadLog.setFilePath(filePath);
		downloadLogService.update(downloadLog);
		
	}

}