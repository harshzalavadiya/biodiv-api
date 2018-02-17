package biodiv.common;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseService extends AbstractService<License> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private LicenseDao licenseDao;

	@Inject
	public LicenseService(LicenseDao licenseDao) {
		super(licenseDao);
		this.licenseDao = new LicenseDao();
	}

	public License findByName(String name) {
		License license = licenseDao.findByName(name);
		return license;
	}

}
