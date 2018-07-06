package biodiv.rating;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jvnet.hk2.annotations.Service;

import biodiv.Transactional;
import biodiv.common.AbstractService;
import biodiv.user.User;
import biodiv.user.UserService;

@Service
public class RatingLinkService extends AbstractService<RatingLink> {
	
	private RatingLinkDao ratingLinkDao;
	
	@Inject
	RatingService ratingService;
	
	@Inject
	UserService userService;
	
	@Inject
	RatingLinkService(RatingLinkDao ratingLinkDao) {
		super(ratingLinkDao);
		this.ratingLinkDao = ratingLinkDao;
		System.out.println("RatingLinkService constructor");
	}

	@Transactional
	public List<User> findWhoLiked(String type, long id) {
		
		try{
			
			List<RatingLink> rll = ratingLinkDao.findWhoLiked(type,id);
			List<User> ul = new ArrayList<User>();
			if(rll.size()>0){
				for(RatingLink rl : rll){
					Rating r = rl.getRating();
					User u = userService.findById(r.getRaterId());
					ul.add(u);
				}
			}
			return ul;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		
	}
}
