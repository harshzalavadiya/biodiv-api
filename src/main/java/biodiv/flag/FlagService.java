package biodiv.flag;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jvnet.hk2.annotations.Service;

import biodiv.Transactional;
import biodiv.common.AbstractService;
import biodiv.user.User;
import biodiv.user.UserService;

@Service
public class FlagService extends AbstractService<Flag> {
	
	private FlagDao flagDao;
	
	@Inject
	UserService userService;
	
	@Inject
	FlagService(FlagDao flagDao) {
		super(flagDao);
		this.flagDao = flagDao;
		System.out.println("FlagService constructor");
	}
	
	@Transactional
	public List<Flag> fetchOlderFlags(String objectType, long objectId) {
		
		try{
			List<Flag> lf = flagDao.fetchOlderFlags(objectType,objectId);
			List<Flag> lfwithUser =  new ArrayList<Flag>();
			for(Flag f : lf){
				f.setUser(userService.findById(f.getUser().getId()));
				lfwithUser.add(f);
			}
			return lfwithUser;
		}catch(Exception e){
			throw e;
		}
		
	}
}

