package biodiv.user;

import java.util.Set;

import javax.inject.Inject;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pac4j.core.profile.CommonProfile;

import biodiv.auth.register.RegistrationCode;
import biodiv.common.AbstractService;
import biodiv.common.Language;
import biodiv.Transactional;
import biodiv.auth.AuthUtils;
import org.jvnet.hk2.annotations.Service;

//@Service
public class UserService extends AbstractService<User> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private UserDao userDao;

	@Inject
	public UserService(UserDao userDao) {
		super(userDao);
		this.userDao = userDao;
		log.trace("UserService constructor");
	}
	
	public User findByEmail(String email) {
		try {
			//userDao.openCurrentSession();
			User user = userDao.findByEmail(email);
			return user;
		} catch (Exception e) {
			throw e;
		} finally {
			//userDao.closeCurrentSession();
		}
	}

	public User findByEmailAndPassword(String email, String password) {
		try {
			//userDao.openCurrentSession();
			User user = userDao.findByEmailAndPassword(email, password);
			return user;
		} catch (Exception e) {
			throw e;
		} finally {
			//userDao.closeCurrentSession();
		}
	}

	public RegistrationCode register(String email) {
		if (email == null)
			return null;
		try {
			RegistrationCode registrationCode = new RegistrationCode(email);
			//userDao.openCurrentSessionWithTransaction();
			if (registrationCode.save() == null) {
				log.error("Coudn't save registrationCode");
			}
			return registrationCode;
		} catch (Exception e) {
			throw e;
		} finally {
			//userDao.closeCurrentSessionWithTransaction();
		}
	}

    public CommonProfile createUserProfile(User user) {
		if(user == null) return null;
		try {
            //userDao.openCurrentSession();
            Set<Role> roles = user.getRoles();
            List authorities = new ArrayList();
            for (Role role : roles) {
                authorities.add(role.getAuthority()); 
            }
            return AuthUtils.createUserProfile(user.getId(), user.getName(), user.getEmail(), authorities);
        } catch (Exception e) {
            throw e;
        } finally {
            //userDao.closeCurrentSession();
		}
	}

    public void updateUserProfile(CommonProfile profile, User user) {
		try {
            //userDao.openCurrentSession();
            Set<Role> roles = user.getRoles();
            List authorities = new ArrayList();
            for (Role role : roles) {
                authorities.add(role.getAuthority()); 
            }
            AuthUtils.updateUserProfile(profile, user.getId(), user.getName(), user.getEmail(), authorities);
        } catch (Exception e) {
            throw e;
        } finally {
            //userDao.closeCurrentSession();
		}
	}
	
    public Map<String,Object> findAuthorSignature(User user) {
			Map<String,Object> author = new HashMap<String,Object>();
			author.put("id", user.getId());
			author.put("icon", user.getIcon());
			author.put("name",user.getName());
			return author;
	}

}
