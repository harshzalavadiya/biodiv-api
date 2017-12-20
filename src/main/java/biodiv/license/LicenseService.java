package biodiv.license;

import biodiv.common.AbstractDao;
import biodiv.common.AbstractService;
import biodiv.common.License;

public class LicenseService extends AbstractService<License>{
	
	private LicenseDao commonDao;
	
	public LicenseService() {
		
		this.commonDao = new LicenseDao();
	}

	public License findByName(String name) {
		License license=commonDao.findByName(name);
		return license;
	}

	@Override
	public LicenseDao getDao() {
		return commonDao;
	}

	
}
