package biodiv.userGroup;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.inject.Inject;

import biodiv.user.Role;
import biodiv.user.User;

/**
 * Utility service that hides a lot of the implementation details for working with ACLs.
 *
 */
public class AclUtilService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());


	@Inject
	biodiv.userGroup.AclService aclService;

	@Inject
	PermissionEvaluator permissionEvaluator;

	@Inject
	SidRetrievalStrategy sidRetrievalStrategy;

	@Inject
	CustomObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy;

	public void initializeSecurityContextHolder(User founder) {
		Set<Role> roles = founder.getRoles();
		List authorities = new ArrayList();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
		}
		
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(founder.getEmail(), founder.getPassword(), authorities);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	/**
	 * Grant a permission. Used when you don't have the instance available.
	 *
	 * @param domainClass  the domain class
	 * @param id  the instance id
	 * @param recipient  the grantee; can be a username, role name, Sid, or Authentication
	 * @param permission  the permission to grant
	 */
	void addPermission(Class<?> domainClass, Serializable id, User recipient, Permission permission) {
		ObjectIdentity oid = objectIdentityRetrievalStrategy.createObjectIdentity(id, domainClass.getName());
		addPermission(oid, recipient, permission);
	}

	/**
	 * Grant a permission. Used when you have the instance available.
	 *
	 * @param domainObject  the domain class instance
	 * @param recipient  the grantee; can be a username, role name, Sid, or Authentication
	 * @param permission  the permission to grant
	 */
	void addPermission(UserGroup domainObject, User recipient, Permission permission) {
		ObjectIdentity oid = objectIdentityRetrievalStrategy.getObjectIdentity(domainObject);
		addPermission(oid, recipient, permission);
	}

	/**
	 * Grant a permission.
	 *
	 * @param oid  represents the domain object
	 * @param recipient  the grantee; can be a username, role name, Sid, or Authentication
	 * @param permission  the permission to grant
	 */
	void addPermission(ObjectIdentity oid, User recipient, Permission permission) {

		Sid sid = createSid(recipient);
		Acl acl = null;
		try {
			log.debug("reading acl for oid : {}", oid);
			acl = aclService.readAclById(oid);
			log.debug("acl : {}", acl);
		}
		catch (NotFoundException e) {
			log.error(e.getMessage());
			log.debug("creating acl for oid : {}", oid);
			acl = aclService.createAcl(oid);
		}
		log.debug("Insert permission {} for Sid {}", permission, sid);

		((AclImpl)acl).insertAce(acl.getEntries().size(), permission, sid, true);
		log.debug("Updating acl");
		aclService.updateAcl((MutableAcl)acl);
		log.debug("Added permission {} for Sid {} for {} with id {}", permission, sid, oid.getType(), oid.getIdentifier());
	}

	/**
	 * Update the owner of the domain class instance.
	 *
	 * @param domainObject  the domain class instance
	 * @param newOwnerUsername  the new username
	 */
//	void changeOwner(UserGroup domainObject, String newUsername) {
//		def acl = readAcl(domainObject);
//		acl.owner = new PrincipalSid(newUsername);
//		aclService.updateAcl(acl);
//	}

	/**
	 * Removes a granted permission. Used when you have the instance available.
	 *
	 * @param domainObject  the domain class instance
	 * @param recipient  the grantee; can be a username, role name, Sid, or Authentication
	 * @param permission  the permission to remove
	 */
	void deletePermission(UserGroup domainObject, User recipient, Permission permission) {
		deletePermission(domainObject.getClass(), domainObject.getId(), recipient, permission);
	}

	/**
	 * Removes a granted permission. Used when you don't have the instance available.
	 *
	 * @param domainClass  the domain class
	 * @param id  the instance id
	 * @param recipient  the grantee; can be a username, role name, Sid, or Authentication
	 * @param permission  the permission to remove
	 */
	void deletePermission(Class<?> domainClass, long id, User recipient, Permission permission) {
		Sid sid = createSid(recipient);
		AclImpl acl = (AclImpl) readAcl(domainClass, id);

		int i=0;
		for(AccessControlEntry entry : acl.getEntries()) {
			if (entry.getSid().equals(sid) && entry.getPermission().equals(permission)) {
				acl.deleteAce(i);
			}
			i++;
		}

		aclService.updateAcl(acl);

		log.debug("Deleted {}({}) ACL permissions for recipient {}", domainClass.getName(), id, recipient);
	}

	/**
	 * Check if the authentication has grants for the specified permission(s) on the domain class instance.
	 *
	 * @param authentication  an authentication representing a user and roles
	 * @param domainObject  the domain class instance
	 * @param permissions  one or more permissions to check
	 * @return  <code>true</code> if granted
	 */
	boolean hasPermission(Authentication authentication, UserGroup domainObject, Permission... permissions) {
		return permissionEvaluator.hasPermission(authentication, domainObject, permissions);
	}

	/**
	 * Check if the authentication has grants for the specified permission(s) on the domain class instance.
	 *
	 * @param authentication  an authentication representing a user and roles
	 * @param domainObject  the domain class instance
	 * @param permissions  one or more permissions to check
	 * @return  <code>true</code> if granted
	 */
	boolean hasPermission(Authentication authentication, UserGroup domainObject, List<Permission> permissions) {
		Permission[] p = new Permission[permissions.size()];
		return hasPermission(authentication, domainObject, permissions.toArray(p));
	}

	/**
	 * Helper method to retrieve the ACL for a domain class instance.
	 *
	 * @param domainObject  the domain class instance
	 * @return the {@link Acl} (never <code>null</code>)
	 */
	Acl readAcl(UserGroup domainObject) {
		return aclService.readAclById(objectIdentityRetrievalStrategy.getObjectIdentity(domainObject));
	}

	/**
	 * Helper method to retrieve the ACL for a domain class instance.
	 *
	 * @param domainClass  the domain class
	 * @param id  the instance id
	 * @return the {@link Acl} (never <code>null</code>)
	 */
	Acl readAcl(Class<?> domainClass, long id) {
		return aclService.readAclById(objectIdentityRetrievalStrategy.createObjectIdentity(id, domainClass.getName()));
	}

	/**
	 * Helper method to delete an ACL for a domain class.
	 *
	 * @param domainObject  the domain class instance
	 */
	void deleteAcl(UserGroup domainObject) {
		aclService.deleteAcl(objectIdentityRetrievalStrategy.getObjectIdentity(domainObject), false);
	}

	protected Sid createSid(User recipient) {
		return new PrincipalSid(recipient.getEmail());
		/*
		if (recipient instanceof String) {
			return recipient.startsWith('ROLE_') ?
					new GrantedAuthoritySid(recipient) :
					new PrincipalSid(recipient)
		}

		if (recipient instanceof Sid) {
			return recipient
		}

		if (recipient instanceof Authentication) {
			return new PrincipalSid(recipient)
		}

		throw new IllegalArgumentException('recipient must be a String, Sid, or Authentication')
		*/
	}
}
