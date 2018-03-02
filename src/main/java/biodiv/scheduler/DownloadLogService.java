package biodiv.scheduler;

import javax.inject.Inject;

import biodiv.Transactional;
import biodiv.common.AbstractService;

public class DownloadLogService extends AbstractService<DownloadLog> {
	
	@SuppressWarnings("unused")
	private DownloadLogDao downloadLogDao;

	@Inject
	DownloadLogService(DownloadLogDao downloadLogDao) {
		super(downloadLogDao);
		this.downloadLogDao = downloadLogDao;
	}

	@Transactional
	public void save(DownloadLog entity) {
		super.save(entity);
	}

	@Transactional
	public void update(DownloadLog entity) {
		super.update(entity);
	}
}
