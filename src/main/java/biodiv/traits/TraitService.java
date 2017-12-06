package biodiv.traits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.NotFoundException;

import org.pac4j.core.profile.CommonProfile;

import biodiv.common.AbstractDao;
import biodiv.common.AbstractService;
import biodiv.common.CommonMethod;
import biodiv.common.License;
import biodiv.license.LicenseService;
import biodiv.user.User;
import biodiv.user.UserService;

public class TraitService extends AbstractService<Trait> {

	private TraitDao traitDao;
	private static final String single_category = "SINGLE_CATEGORICAL";
	private static final String multiple_category = "MULTIPLE_CATEGORICAL";
	private static final String range_category = "RANGE";

	public TraitService() {

		// TODO Auto-generated constructor stub
		this.traitDao = new TraitDao();
	}

	@Override
	public AbstractDao<Trait, Long> getDao() {
		return traitDao;
	}

	/**
	 * 
	 * @param objectId
	 * @param objectType
	 * @param sGroup
	 * @param classificationId
	 * @param isNotObservationTrait
	 * @param showInObservation
	 * @return
	 */
	public List<TraitFactUi> list(Long objectId, String objectType, Long sGroup, Long classificationId,
			Boolean isNotObservationTrait, Boolean showInObservation) {
		if (objectId == null) {
			return null;
		}
		if (objectType == null) {
			return null;
		}
		if (sGroup == null) {
			return null;
		}

		List<TraitFactUi> results = new ArrayList<TraitFactUi>();

		List<Long> taxonIds = traitDao.getTaxonIds(sGroup);
		Set<Long> allTaxonIds = new HashSet<Long>();
		allTaxonIds = traitDao.getPathToRoot(taxonIds, classificationId);

		Set<Long> traitIds = traitDao.getTraitId(allTaxonIds);

		List<TraitObject> obj = new ArrayList<TraitObject>();
		obj = traitDao.list(traitIds, isNotObservationTrait, showInObservation);

		List<Fact> factInstance = traitDao.getFact(objectId, objectType);

		TraitFactUi result = new TraitFactUi();
		result.setFactInstance(factInstance);
		result.setInstanceList(obj);
		results.add(result);
		return results;
	}

	/**
	 * 
	 * @param id
	 * @param objectType
	 * @return
	 */

	public List<Fact> slist(Long id, String objectType) {

		List<Fact> taxonIds = traitDao.getFact(id, objectType);
		return taxonIds;
	}

	/**
	 * 
	 * @param traitValues
	 * @param traitId
	 * @param objectId
	 * @param objectType
	 * @param profile
	 * @return
	 */
	public Serializable updateFact(Set<Long> traitValues, Long traitId, Long objectId, String objectType,CommonProfile profile) {
		Serializable result = null;
		if (traitValues == null || traitId == null || objectId == null || objectType == null) {
			return null;
		}
		Trait trait = list(traitId);

		if (trait == null) {
			return null;
		}

		List<Fact> listFact = new ArrayList<Fact>();

		if (trait.getTraitTypes().equalsIgnoreCase(single_category)) {

			listFact = traitDao.getFact(objectId, objectType, traitId);

			Fact fact1 = new Fact();

			if (listFact.size() == 0) {
				Fact newupdated = new Fact();
				newupdated.setFromDate(new Date());
				newupdated.setAttribution(profile.getUsername());
				newupdated.setIsDeleted(false);

				/**
				 * Getting the licence
				 */
				LicenseService licenceService = new LicenseService();
				License license = licenceService.findByName("CC_BY");
				if (license == null) {
					throw new NotFoundException("No license find with name : ");
				}
				newupdated.setLicense(license);

				newupdated.setObjectId(objectId);
				newupdated.setObjectType(objectType);
				newupdated.setTaxonomyDefinition(fact1.getTaxonomyDefinition());
				newupdated.setToDate(new Date());
				newupdated.setToValue(fact1.getToValue());
				newupdated.setTrait(trait);
				/**
				 * TO get the single TaxonValue
				 */
				Optional<Long> firstString = traitValues.stream().findFirst();
				if (firstString.isPresent()) {
					Long value = firstString.get();
					TraitValue traitValue = traitDao.getTraitValue(value);
					if (traitValue != null) {
						newupdated.setTraitValue(traitValue);
					}

				}
				/**
				 * Getting user corresponding to given id
				 */
				UserService userService = new UserService();
				User s = userService.findById(Long.parseLong(profile.getId()));
				newupdated.setUser(s);
				newupdated.setVersion(0L);
				/**
				 * Updating fact Table
				 */
				traitDao.updateFact(newupdated);

			} else {

				int Deleteresult = traitDao.deleteFact(objectId, objectType, traitId);
				Fact newupdated = new Fact();
				for (Fact fact : listFact) {

					newupdated.setFromDate(new Date());
					// newupdated.setAttribution(fact.getAttribution());
					newupdated.setAttribution(profile.getUsername());
					newupdated.setIsDeleted(fact.isIsDeleted());
					newupdated.setLicense(fact.getLicense());
					newupdated.setObjectId(objectId);
					newupdated.setObjectType(objectType);
					newupdated.setTaxonomyDefinition(fact.getTaxonomyDefinition());
					newupdated.setToDate(new Date());
					newupdated.setToValue(fact.getToValue());

					newupdated.setTrait(trait);

					Optional<Long> firstString = traitValues.stream().findFirst();
					if (firstString.isPresent()) {
						Long value = firstString.get();
						TraitValue traitValue = traitDao.getTraitValue(value);
						if (traitValue != null) {
							newupdated.setTraitValue(traitValue);
						}
					}
					UserService userService = new UserService();
					User s = userService.findById(Long.parseLong(profile.getId()));
					newupdated.setUser(s);
					newupdated.setVersion(fact.getVersion() + 1L);

				}
				traitDao.updateFact(newupdated);
			}

		} else if (trait.getTraitTypes().equalsIgnoreCase(multiple_category)) {

			int Deleteresult = traitDao.deleteFact(objectId, objectType, traitId);

			LicenseService licenceService = new LicenseService();
			License license = licenceService.findByName("CC_BY");
			if (license == null) {
				throw new NotFoundException("No license find with name : ");
			}

			User user=new User();
			User s =  (User) user.get(Long.parseLong(profile.getId()));

			for (Long traitValue : traitValues) {
				Fact newupdated = new Fact();
				newupdated.setFromDate(new Date());
				newupdated.setAttribution(profile.getUsername());
				newupdated.setIsDeleted(false);
				newupdated.setLicense(license);
				newupdated.setObjectId(objectId);
				newupdated.setObjectType(objectType);
				newupdated.setTaxonomyDefinition(null);
				newupdated.setToDate(new Date());
				newupdated.setToValue(null);
				newupdated.setTrait(trait);
				TraitValue traitValue1 = traitDao.getTraitValue(traitValue);
				if (traitValue1 != null) {
					newupdated.setTraitValue(traitValue1);
				}
				newupdated.setUser(s);
				newupdated.setVersion(0L);

				result = traitDao.updateFact(newupdated);

			}

		}
		else if(trait.getTraitTypes().equalsIgnoreCase(range_category)){
			
		}
		else{
			throw new NotFoundException("Trait not found");
		}
		

		return result;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Trait list(Long id) {
		Class<?> css = null;
		try {
			css = Class.forName("biodiv.traits.Trait");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Trait result = null;
		try {
			result = (Trait) ((CommonMethod) css.newInstance()).get(id);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Fact listFact(Long id) {
		Class<?> css = null;
		;
		try {
			css = Class.forName("biodiv.traits.Fact");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Fact result = null;
		try {
			result = (Fact) ((CommonMethod) css.newInstance()).get(id);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
