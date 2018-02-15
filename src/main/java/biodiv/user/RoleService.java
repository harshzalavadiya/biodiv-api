package biodiv.user;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.AbstractService;

public class RoleService extends AbstractService<Role>{

	private final Logger log = LoggerFactory.getLogger(getClass());

	private RoleDao roleDao;
	
	@Inject
	RoleService(RoleDao roleDao){
		super(roleDao);
		this.roleDao = roleDao;
		log.trace("RoleService constructor");
	}
	
	public Role findRoleByAuthority(String authority) {
		
		try{
			Role role = roleDao.findRoleByAuthority(authority);
			return role;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

}
