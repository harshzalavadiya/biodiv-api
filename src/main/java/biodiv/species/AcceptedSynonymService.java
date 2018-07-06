package biodiv.species;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractService;
import biodiv.observation.Recommendation;
import biodiv.observation.RecommendationDao;
import biodiv.taxon.datamodel.dao.Taxon;

public class AcceptedSynonymService extends AbstractService<AcceptedSynonym> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private AcceptedSynonymDao acceptedSynonymDao;

	@Inject
	AcceptedSynonymService(AcceptedSynonymDao acceptedSynonymDao) {
		super(acceptedSynonymDao);
		this.acceptedSynonymDao = acceptedSynonymDao;
		
	}

	public List<Taxon> fetchAcceptedNames(Taxon synonym) {
		
		List<AcceptedSynonym> res = acceptedSynonymDao.findAllBySynonym(synonym);
		List<Taxon> acceptedNames = new ArrayList<Taxon>();
		for(AcceptedSynonym t : res){
			acceptedNames.add(t.getTaxonomyDefinitionByAcceptedId());
		}
		return acceptedNames;
	}
	
}

