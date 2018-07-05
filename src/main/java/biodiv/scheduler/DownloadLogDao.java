package biodiv.scheduler;

import javax.inject.Inject;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class DownloadLogDao extends AbstractDao<DownloadLog, Long> implements DaoInterface<DownloadLog, Long> {

	@Inject
	public DownloadLogDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public DownloadLog findById(Long id) {
		DownloadLog entity = (DownloadLog) sessionFactory.getCurrentSession().get(DownloadLog.class, id);
		return entity;
	}


}
