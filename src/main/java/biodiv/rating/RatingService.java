package biodiv.rating;

import javax.inject.Inject;

import org.jvnet.hk2.annotations.Service;

import biodiv.common.AbstractService;
import biodiv.observation.Observation;
import biodiv.observation.ObservationDao;

@Service
public class RatingService extends AbstractService<Rating> {
	
	private RatingDao ratingDao;
	
	@Inject
	RatingService(RatingDao ratingDao) {
		super(ratingDao);
		this.ratingDao = ratingDao;
		System.out.println("RatingService constructor");
	}
}
