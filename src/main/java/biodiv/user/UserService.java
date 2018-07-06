package biodiv.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.apache.commons.configuration2.Configuration;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.auth.AuthUtils;
import biodiv.common.AbstractService;

public class UserService extends AbstractService<User> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private UserDao userDao;
	
	@Inject
	private Configuration config;
	
	@Inject
	private RoleService roleService;

	@Inject
	public UserService(UserDao userDao) {
		super(userDao);
		this.userDao = userDao;
	}
	
	@Transactional
	public User findByEmail(String email)  throws NotFoundException {
	    return userDao.findByEmail(email);
	}

	public User findByEmailAndPassword(String email, String password) throws NotFoundException {
		return userDao.findByEmailAndPassword(email, password);
	}

	public CommonProfile createUserProfile(User user) {
		if (user == null)
			return null;
		try {
			Set<Role> roles = user.getRoles();
			List authorities = new ArrayList();
			for (Role role : roles) {
				authorities.add(role.getAuthority());
			}
			return AuthUtils.createUserProfile(user.getId(), user.getName(), user.getEmail(), authorities);
		} catch (Exception e) {
			throw e;
		}
	}

	public void updateUserProfile(CommonProfile profile, User user) {
		try {
			Set<Role> roles = user.getRoles();
			List authorities = new ArrayList();
			for (Role role : roles) {
				authorities.add(role.getAuthority());
			}
			AuthUtils.updateUserProfile(profile, user.getId(), user.getName(), user.getEmail(), authorities);
		} catch (Exception e) {
			throw e;
		}
	}

	public Map<String, Object> findAuthorSignature(User user) {
		Map<String, Object> author = new HashMap<String, Object>();
		author.put("id", user.getId());
		author.put("icon", user.getIcon());
		author.put("name", user.getName());
		author.put("profilePic", user.getProfilePic());
		author.put("fbProfilePic", user.getFbProfilePic());
		return author;
	}

	@Transactional
	public void setDefaultRoles(User user) {
		String[] defaultRoleNames = config.getStringArray("user.defaultRoleNames");
		log.debug("setDefaultRoles {}", defaultRoleNames.toString());
		for(int i=0; i<defaultRoleNames.length; i++) {
			Role role = roleService.findRoleByAuthority((String)defaultRoleNames[i]);
			user.addRole(role);
		}				
	}

	@Transactional
	public void save(User user) {
		userDao.save(user);
	}

}
