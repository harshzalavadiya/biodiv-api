package biodiv.observation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractService;
import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.taxon.service.TaxonService;
import biodiv.util.Utils;

public class RecommendationService extends AbstractService<Recommendation> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private RecommendationDao recommendationDao;
	
	@Inject
	private TaxonService taxonService;

	@Inject
	RecommendationService(RecommendationDao recommendationDao) {
		super(recommendationDao);
		this.recommendationDao = recommendationDao;
		log.trace("RecommendationService constructor");
	}

	public Recommendation findReco(String name, Boolean isScientificName, Long languageId, Taxon taxonConceptForNewReco, Boolean createNew, Boolean flush) {
		
		if(name!=null){
			
			if(isScientificName){
				// if sn then only sending to names parser for common name only cleaning
				name = Utils.getCanonicalForm(name);
				languageId = null;
			}else{
				//converting common name to title case
				name = Utils.getTitleCase(Utils.cleanName(name));
			}
			Recommendation reco = null;
			//check if name is null here only when parsing fails
			if(name!=null){
				reco = searchReco(name,isScientificName,languageId,taxonConceptForNewReco);
			}
			
			 
			
			if(reco==null && createNew && name!=null){
				//should add lower case name also
				String lowercaseName = name.toLowerCase();
				reco = new Recommendation(new Date(),name,lowercaseName,taxonConceptForNewReco,isScientificName,languageId);//write constructor properly
				//should add lower case name also
				reco.setAcceptedName(taxonService.fetchAccepted(taxonConceptForNewReco));
				
//				if(!save(reco, true, false)){
//					reco = null;
//				}
				save(reco);
			}
			return reco;
		}
		return null;
	}

	private Recommendation searchReco(String name, Boolean isScientificName, Long languageId,
			Taxon taxonConcept) {
		if(name==null) return null;
		
		Taxon acceptedName = taxonService.fetchAccepted(taxonConcept);
		List<Recommendation> recoList = null;
		String hql =  "from Recommendation r where r.isScientificName =:isScientificName ";
		Map<String,Object> data =  new HashMap<String,Object>();
		data.put("isScientificName", isScientificName);
		if(name.indexOf("%")>=0){
			hql = hql +"and r.lowercaseName is like =:lowercaseName ";
		}else{
			hql = hql +"and r.lowercaseName =:lowercaseName ";
		}
		data.put("lowercaseName", name.toLowerCase());
		
		if(languageId !=null){
			hql = hql +"and r.languageId =:languageId ";
			data.put("languageId", languageId);
		}else{
			hql = hql+"and r.languageId is null ";
		}
		
		if(taxonConcept !=null){
			hql = hql+"and r.taxonConcept =:taxonConcept ";
			data.put("taxonConcept", taxonConcept);
		}else{
			hql = hql+"and r.taxonConcept is null ";
		}
		
		if(acceptedName !=null){
			hql = hql+"and r.acceptedName =:acceptedName";
			data.put("acceptedName", acceptedName);
		}else{
			hql = hql+"and r.acceptedName is null ";
		}
		
		recoList = recommendationDao.fetchthisList(hql,data);
		
		if(recoList == null) return null;
		
		if(recoList.size() == 1) return recoList.get(0);
		
		// if taxon concept is not given and multiple result then trying to get one without taxon concept and returning it	
		Recommendation retReco = null;
		if(taxonConcept== null){
			for(Recommendation r : recoList){
				if(retReco==null){
					if(r.getTaxonConcept()==null){
						retReco = r;
					}
				}
			}
		}
		
		return retReco;
	}

}
