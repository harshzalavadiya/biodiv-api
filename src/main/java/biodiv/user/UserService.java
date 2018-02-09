package biodiv.user;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pac4j.core.profile.CommonProfile;

import biodiv.auth.register.RegistrationCode;
import biodiv.common.AbstractService;
import biodiv.common.Language;
import biodiv.auth.AuthUtils;

public class UserService extends AbstractService<User> {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private UserDao userDao;

	public UserService() {
		this.userDao = new UserDao();
	}

	@Override
	public UserDao getDao() {	
		return userDao;
	}
	
	public User findByEmail(String email) {
		try {
			userDao.openCurrentSession();
			User user = userDao.findByPropertyWithCondition("email", email, "=");
			return user;
		} catch (Exception e) {
			throw e;
		} finally {
			userDao.closeCurrentSession();
		}
	}

	public User findByEmailAndPassword(String email, String password) {
		try {
			userDao.openCurrentSession();
			User user = userDao.findByEmailAndPassword(email, password);
			return user;
		} catch (Exception e) {
			throw e;
		} finally {
			userDao.closeCurrentSession();
		}
	}

	public RegistrationCode register(String email) {
		if (email == null)
			return null;
		try {
			RegistrationCode registrationCode = new RegistrationCode(email);
			userDao.openCurrentSessionWithTransaction();
			if (registrationCode.save() == null) {
				log.error("Coudn't save registrationCode");
			}
			return registrationCode;
		} catch (Exception e) {
			throw e;
		} finally {
			userDao.closeCurrentSessionWithTransaction();
		}
	}

    public CommonProfile createUserProfile(User user) {
		if(user == null) return null;
		try {
            userDao.openCurrentSession();
            Set<Role> roles = user.getRoles();
            List authorities = new ArrayList();
            for (Role role : roles) {
                authorities.add(role.getAuthority()); 
            }
            return AuthUtils.createUserProfile(user.getId(), user.getName(), user.getEmail(), authorities);
        } catch (Exception e) {
            throw e;
        } finally {
            userDao.closeCurrentSession();
		}
	}

    public void updateUserProfile(CommonProfile profile, User user) {
		try {
            userDao.openCurrentSession();
            Set<Role> roles = user.getRoles();
            List authorities = new ArrayList();
            for (Role role : roles) {
                authorities.add(role.getAuthority()); 
            }
            AuthUtils.updateUserProfile(profile, user.getId(), user.getName(), user.getEmail(), authorities);
        } catch (Exception e) {
            throw e;
        } finally {
            userDao.closeCurrentSession();
		}
	}
}
