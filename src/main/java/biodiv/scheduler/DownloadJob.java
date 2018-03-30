package biodiv.scheduler;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.http.ParseException;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import biodiv.common.NakshaUrlService;
import biodiv.mail.DownloadMailingService;
import biodiv.maps.MapHttpResponse;
import biodiv.maps.MapIntegrationService;
import biodiv.maps.MapSearchQuery;
import biodiv.scheduler.DownloadLog.DownloadType;
import biodiv.scheduler.DownloadLog.SourceType;
import biodiv.user.User;

public class DownloadJob implements Job {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String INDEX_KEY = "index";
	public static final String TYPE_KEY = "type";
	public static final String DATA_KEY = "data";
	public static final String USER_KEY = "user";
	public static final String SEARCH_KEY = "search";
	public static final String NOTES_KEY = "notes";

	@Inject
	MapIntegrationService mapIntegrationService;

	@Inject
	DownloadLogService downloadLogService;

	@Inject
	NakshaUrlService nakshaUrlService;

	@Inject
	DownloadMailingService downloadMailingService;

	public DownloadJob() {
		// Instances of Job must have a public no-argument constructor.
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		JobDataMap data = context.getMergedJobDataMap();
		
		String index = data.getString(INDEX_KEY);
		String indexType = data.getString(TYPE_KEY);
		MapSearchQuery mapSearchQuery = (MapSearchQuery) data.get(DATA_KEY);
		
		User user = (User) data.get(USER_KEY);
		String filterUrl = data.getString(SEARCH_KEY);
		String notes = data.getString(NOTES_KEY);
		SchedulerStatus status = SchedulerStatus.Scheduled;
		DownloadType type = DownloadType.CSV;
		SourceType sourceType = SourceType.Observations;
		
		DownloadLog downloadLog = new DownloadLog(user, filterUrl, notes, status, type, sourceType, 0);
		downloadLogService.save(downloadLog);

		String url = nakshaUrlService.getDownloadUrl(index, indexType);
		MapHttpResponse httpResponse = mapIntegrationService.postRequest(url, mapSearchQuery);
		
		String filePath = null;
		status = SchedulerStatus.Failed;

		if(httpResponse != null) {
			try {
				filePath = (String) httpResponse.getDocument();
				status = SchedulerStatus.Success;
				
				addDownloadMail(user);
				
			} catch (ParseException e) {
				e.printStackTrace();
				log.error("Error while reading the csv file path response from naksha");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		log.info("Download file generated with status {} at {}", status.name(), filePath);
		downloadLog.setFilePath(filePath);
		downloadLog.setStatus(status);
		downloadLogService.update(downloadLog);
	}
	
	public void addDownloadMail(User user) throws Exception{
		
		try{
			//System.out.println("emailid of the the user,whom mail is tobe sent "+user.getEmail());
			//System.out.println("emailid of the the user,whom mail is tobe sent "+user.getEmail());
			
			List<User> allBccs = downloadMailingService.getAllBccPeople();
			for(User bcc : allBccs){
				HtmlEmail emailToBcc = downloadMailingService.buildDownloadMailMessage(bcc.getEmail(),user.getId(),user.getName());
			}
			
			if(user.getSendNotification()){
				HtmlEmail email = downloadMailingService.buildDownloadMailMessage(user.getEmail(),user.getId(),user.getName());
			}
		
			if(!downloadMailingService.isAnyThreadActive()){
				System.out.println("no thread is active currently");
				Thread th = new Thread(downloadMailingService);
				th.start();
			}
			//downloadMailingService.send(email);
		}catch(Exception e){
			throw e;
		}
		
	}

}