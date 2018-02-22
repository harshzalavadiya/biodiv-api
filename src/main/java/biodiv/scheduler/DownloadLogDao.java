package biodiv.scheduler;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class DownloadLogDao extends AbstractDao<DownloadLog, Long> implements DaoInterface<DownloadLog, Long> {

	public DownloadLogDao() {
	}
	
	@Override
	public DownloadLog findById(Long id) {
		DownloadLog entity = (DownloadLog) getCurrentSession().get(DownloadLog.class, id);
		return entity;
	}


}
