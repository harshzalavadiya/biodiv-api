package biodiv.traits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.jvnet.hk2.annotations.Service;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.AbstractService;
import biodiv.common.License;
import biodiv.common.LicenseService;
import biodiv.user.User;

@Service
public class TraitService extends AbstractService<Trait> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private TraitDao traitDao;
	private static final String single_category = "SINGLE_CATEGORICAL";
	private static final String multiple_category = "MULTIPLE_CATEGORICAL";
	private static final String range_category = "RANGE";

	@Inject
	private LicenseService licenceService;
	
	@Inject
	public TraitService(TraitDao traitDao) {
		super(traitDao);
		this.traitDao = traitDao;
		log.trace("TraitDao constructor");
	}

	/**
	 * 
	 * @param objectId
	 * dummy
	 * @param objectType
	 * dummy
	 * @param sGroup
	 * dummy
	 * @param classificationId
	 * dummy
	 * @param isNotObservationTrait
	 * dummy
	 * @param showInObservation
	 * dummy
	 * @return
	 * dummy
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
	 * dummy
	 * @param objectType
	 * dummy
	 * @return
	 * dummy
	 */

	public List<Fact> slist(Long id, String objectType) {

		List<Fact> taxonIds = traitDao.getFact(id, objectType);
		return taxonIds;
	}

	/**
	 * 
	 * @param traitValues
	 * dummy
	 * @param traitId
	 * dummy
	 * @param objectId
	 * dummy
	 * @param objectType
	 * dummy
	 * @param profile
	 * dummy
	 * @return
	 * dummy
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
					
					//TraitValue traitValue=(TraitValue) new TraitValue().get(value);
					TraitValue traitValue=traitDao.getTraitValue(traitId,value);
					
					if (traitValue != null) {
						newupdated.setTraitValue(traitValue);
					}
					else{
						return null;
					}

				}
				/**
				 * Getting user corresponding to given id
				 */
				User user=new User();
				User s =  (User) user.get(Long.parseLong(profile.getId()));
				newupdated.setUser(s);
				newupdated.setVersion(0L);
				/**
				 * Updating fact Table
				 */
				newupdated.save();

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
						
						TraitValue traitValue=traitDao.getTraitValue(traitId,value);
						
						if (traitValue != null) {
							newupdated.setTraitValue(traitValue);
						}
						else{
							return null;
						}
					}
					User user=new User();
					User s =  (User) user.get(Long.parseLong(profile.getId()));
					newupdated.setUser(s);
					newupdated.setVersion(fact.getVersion() + 1L);

				}
				newupdated.save();
			}

		} else if (trait.getTraitTypes().equalsIgnoreCase(multiple_category)) {

			int Deleteresult = traitDao.deleteFact(objectId, objectType, traitId);

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
				
				//TraitValue traitValue1=(TraitValue) new TraitValue().get(traitValue);
						
				//TraitValue traitValue1 = traitDao.getTraitValue(traitValue);
				
				TraitValue traitValue1=traitDao.getTraitValue(traitId,traitValue);
				if (traitValue1 != null) {
					newupdated.setTraitValue(traitValue1);
				}
				else{
					return null;
				}
				
				newupdated.setUser(s);
				newupdated.setVersion(0L);

				newupdated.save();

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
	 * dummy
	 * @return
	 * dummy
	 */
	public Trait list(Long id) {
		Trait result=(Trait) new Trait().get(id);
		return result;

	}

	/**
	 * 
	 * @param id
	 * dummy
	 * @return
	 * dummy
	 */
	public Fact listFact(Long id) {

		Fact result=(Fact) new Fact().get(id);
		return result;
	}

	public List<Trait> listObservationTrait() {
		// TODO Auto-generated method stub
		List<Trait> results=traitDao.listObservationTrait();
		return results;
	}

	public List<TraitValue> getTraitValue(Long id) {
		// TODO Auto-generated method stub
		List<TraitValue> results=traitDao.getTraitValueWithTraitId(id);
		return results;
	}

}
