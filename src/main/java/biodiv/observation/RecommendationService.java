package biodiv.observation;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractService;

public class RecommendationService extends AbstractService<Recommendation> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private RecommendationDao recommendationDao;

	@Inject
	RecommendationService(RecommendationDao recommendationDao) {
		super(recommendationDao);
		this.recommendationDao = recommendationDao;
		log.trace("RecommendationService constructor");
	}

}
