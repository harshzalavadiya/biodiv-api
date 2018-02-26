package biodiv.scheduler;

import javax.inject.Inject;

import biodiv.common.AbstractService;

public class DownloadLogService extends AbstractService<DownloadLog> {
	
	@SuppressWarnings("unused")
	private DownloadLogDao downloadLogDao;

	@Inject
	DownloadLogService(DownloadLogDao downloadLogDao) {
		super(downloadLogDao);
		this.downloadLogDao = downloadLogDao;
	}
}
