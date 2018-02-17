package biodiv.common;

import java.util.List;

import javax.inject.Inject;

public class SpeciesGroupService extends AbstractService<SpeciesGroup> {

	
	private SpeciesGroupDao speciesGroupDao;

	@Inject
	SpeciesGroupService(SpeciesGroupDao speciesGroupDao) {
		super(speciesGroupDao);
		System.out.println("SpeciesGroupService constructor");
		this.speciesGroupDao = speciesGroupDao;
	}

	public List<SpeciesGroup> list() {
		try {
			List<SpeciesGroup> results = speciesGroupDao.list();
			return results;
		} catch (Exception e) {
			throw e;
		} finally {

		}

	}

}
