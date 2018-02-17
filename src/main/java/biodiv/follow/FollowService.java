package biodiv.follow;

import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.common.AbstractService;
import biodiv.user.User;

public class FollowService extends AbstractService<Follow>{

	private final Logger log = LoggerFactory.getLogger(getClass());

	private FollowDao followDao;
	
	@Inject
	FollowService(FollowDao followDao){
		super(followDao);
		this.followDao = followDao;
	}
	
	@Transactional
	public void addFollower(Object objectToFollow,String objectTyp,Long objectToFollowId,User user){
		
		//String objectType = objectToFollow.getClass().getCanonicalName();
		try{
			
			String objectType;
			if(objectTyp != null){
				objectType = objectTyp;
			}else{
				objectType = Follow.getType(objectToFollow);
			}
			long userId = user.getId();
			Date createdOn = new java.util.Date();
			System.out.println("objectTypeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee "+objectType);
			System.out.println("oibjectToFollowIdddddddddddddddddddddddddddddddddddddddddd "+objectToFollowId);
			System.out.println("authoridddddddddddddddddddddddddddddddd "+user.getId());
			if(!isFollowing(objectType,objectToFollowId,userId)){
				System.out.println("is zFollowing is false");
				Follow follow = new Follow(user,objectToFollowId,objectType,createdOn);
				save(follow);
			}	
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		
	}
	
	@Transactional
	public Boolean isFollowing(String objectToFollowType,Long objectToFollowId , long userId){
		if(objectToFollowType == null || userId == 0 || objectToFollowId == null){
			return false;
		}
		
		Boolean whetherFollowing = followDao.isFollowing(objectToFollowType,objectToFollowId,userId);
		System.out.println("follow************* "+whetherFollowing);
		return whetherFollowing;
	}

	
}
