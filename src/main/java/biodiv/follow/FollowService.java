package biodiv.follow;

import java.util.Date;

import biodiv.Intercept;
import biodiv.common.AbstractService;
import biodiv.user.User;

public class FollowService extends AbstractService<Follow>{

	private FollowDao followDao;
	
	public FollowService(){
		this.followDao = new FollowDao();
	}
	
	@Override
	public FollowDao getDao() {
		return followDao;
	}
	
	@Intercept
	public void addFollower(Object objectToFollow,Long objectToFollowId,User user){
		
		//String objectType = objectToFollow.getClass().getCanonicalName();
		try{
			String objectType = Follow.getType(objectToFollow);
			long userId = user.getId();
			Date createdOn = new java.util.Date();
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
	
	@Intercept
	public Boolean isFollowing(String objectToFollowType,Long objectToFollowId , long userId){
		if(objectToFollowType == null || userId == 0 || objectToFollowId == null){
			return false;
		}
		
		Boolean whetherFollowing = getDao().isFollowing(objectToFollowType,objectToFollowId,userId);
		System.out.println("follow************* "+whetherFollowing);
		return whetherFollowing;
	}

	
}
