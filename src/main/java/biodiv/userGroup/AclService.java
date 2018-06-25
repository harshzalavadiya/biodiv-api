package biodiv.userGroup;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.AuditableAccessControlEntry;
import org.springframework.security.acls.model.ChildrenExistException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.context.SecurityContextHolder;
import biodiv.Transactional;
import org.springframework.util.Assert;

import com.google.inject.Inject;

/**
 * implementation of {@link org.springframework.security.acls.model.AclService} and {@link MutableAclService}.
 * Ported from <code>JdbcAclService</code> and <code>JdbcMutableAclService</code>.
 *
 * Individual methods are @Transactional since NotFoundException
 * is a runtime exception and will cause an unwanted transaction rollback
 *
 */
class AclService implements MutableAclService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	AclLookupStrategy aclLookupStrategy;

	@Inject
	AclCache aclCache;

	@Inject
	SessionFactory sessionFactory;

	@Transactional
	public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
		Assert.notNull(objectIdentity, "Object Identity required");

		// Check this object identity hasn't already been persisted
		if (retrieveObjectIdentity(objectIdentity) != null) {
			throw new AlreadyExistsException("Object identity "+objectIdentity+" already exists");
		}

		// Need to retrieve the current principal, in order to know who "owns" this ACL (can be changed later on)
		PrincipalSid sid = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication());

		// Create the acl_object_identity row
		createObjectIdentity(objectIdentity, sid);

		return (MutableAcl) readAclById(objectIdentity);
	}

	protected void createObjectIdentity(ObjectIdentity object, Sid owner) {
		AclSid ownerSid = createOrRetrieveSid(owner, true);
		AclClass aclClass = createOrRetrieveClass(object.getType(), true);
		AclObjectIdentity aclObjectIdentity = new AclObjectIdentity(
				aclClass,
				(long) object.getIdentifier(),
				ownerSid,
				true);
		aclObjectIdentity.save(sessionFactory);
	}

	protected AclSid createOrRetrieveSid(Sid sid, boolean allowCreate) {
		Assert.notNull(sid, "Sid required");

		String sidName;
		boolean principal;
		if (sid instanceof PrincipalSid) {
			sidName = ((PrincipalSid) sid).getPrincipal();
			principal = true;
		}
		else if (sid instanceof GrantedAuthoritySid) {
			sidName = ((GrantedAuthoritySid) sid).getGrantedAuthority();
			principal = false;
		}
		else {
			throw new IllegalArgumentException("Unsupported implementation of Sid");
		}

		AclSid aclSid = AclSid.findBySidAndPrincipal(sidName, principal, sessionFactory);
		if (aclSid != null && allowCreate == true) {
			aclSid = new AclSid(sidName, principal);
			aclSid.save(sessionFactory);
		}
		return aclSid;
	}

	protected AclClass createOrRetrieveClass(String className, boolean allowCreate) {;
		AclClass aclClass = AclClass.findByClassName(className, sessionFactory);
		if (aclClass != null && allowCreate) {
			aclClass = new AclClass(className);
			aclClass.save(sessionFactory);
		}
		return aclClass;
	}

	@Transactional
	public void deleteAcl(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException {

		Assert.notNull(objectIdentity, "Object Identity required");
		Assert.notNull(objectIdentity.getIdentifier(), "Object Identity doesn't provide an identifier");

		if (deleteChildren) {
			List<ObjectIdentity> children = findChildren(objectIdentity);
			for(ObjectIdentity child : children) {
				deleteAcl(child, true);
			}
		}

		AclObjectIdentity oid = retrieveObjectIdentity(objectIdentity);
		if (oid != null) {
			// Delete this ACL's ACEs in the acl_entry table
			deleteEntries(oid);

			// Delete this ACL's acl_object_identity row
			oid.delete(sessionFactory);
		}

		// Clear the cache
		aclCache.evictFromCache(objectIdentity);
	}

	protected void deleteEntries(AclObjectIdentity oid) {
		if (oid != null) {
			Query q = sessionFactory.getCurrentSession().createQuery("delete from AclEntry where aclObjectIdentity=:aclObjectIdentity");
			q.setParameter("aclObjectIdentity", oid);
			q.executeUpdate();
			sessionFactory.getCurrentSession().flush();
		}
	}

	@Transactional
	protected void deleteEntries(List<AclEntry> entries) {
		Query q = sessionFactory.getCurrentSession().createQuery("delete from AclEntry where id=:id");
		//sessionFactory.getCurrentSession().beginTransaction();
		for(int i=0; i<entries.size(); i++) {
			q.setParameter("id", entries.get(i).getId());
			q.executeUpdate();
		}
		sessionFactory.getCurrentSession().flush();
	}

	@Transactional
	public MutableAcl updateAcl(MutableAcl acl) throws NotFoundException {
		Assert.notNull(acl.getId(), "Object Identity doesn't provide an identifier");
		AclObjectIdentity aclObjectIdentity = retrieveObjectIdentity(acl.getObjectIdentity());

		List<AclEntry> existingAces = AclEntry.findAllByAclObjectIdentity(aclObjectIdentity, sessionFactory);

		List<AclEntry> toDelete = new ArrayList<AclEntry>();
		for(AclEntry ace : existingAces) {
			for(AccessControlEntry entry : acl.getEntries()) {
				if(!(entry.getPermission().getMask() == ace.getMask() && entry.getSid().toString().equals(ace.getAclSid().getSid()))) {
					toDelete.add(ace);
				}
			}
		}
		
		List<AuditableAccessControlEntry> toCreate = new ArrayList<AuditableAccessControlEntry>();
		for(AccessControlEntry entry : acl.getEntries()) {
			for(AclEntry ace : existingAces) {
				if(!(entry.getPermission().getMask() == ace.getMask() && entry.getSid().toString().equals(ace.getAclSid().getSid()))) {
					toCreate.add((AuditableAccessControlEntry)entry);
				}
			}
		}
		
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println(toDelete);
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println(toCreate);
		// Delete this ACL's ACEs in the acl_entry table
		deleteEntries( toDelete);
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		
		// Create this ACL's ACEs in the acl_entry table
		createEntries(acl, toCreate);

		// Change the mutable columns in acl_object_identity
		updateObjectIdentity(acl);

		// Clear the cache, including children
		clearCacheIncludingChildren(acl.getObjectIdentity());

		return (MutableAcl) readAclById(acl.getObjectIdentity());
	}
	
	protected void createEntries(Acl acl) {
		createEntries(acl, null);
	}
	
	protected void createEntries(Acl acl, List<AuditableAccessControlEntry> entries) {
		//entries = (entries != null) ? entries : acl.getEntries();
		int i = 0;
		for (AuditableAccessControlEntry entry : entries) {
			Assert.isInstanceOf(AccessControlEntryImpl.class, entry, "Unknown ACE class");
			AclEntry aclEntry =  new AclEntry(
					createOrRetrieveSid(entry.getSid(), true),
					(AclObjectIdentity)	AclObjectIdentity.get(((MutableAcl)acl).getId(), AclObjectIdentity.class, sessionFactory),
					i++,
					entry.isAuditFailure(), 
					entry.isAuditSuccess(),
					entry.isGranting(),
					entry.getPermission().getMask());
			aclEntry.save(sessionFactory);
		}
	}

	protected void updateObjectIdentity(Acl acl) {
		Assert.notNull(acl.getOwner(), "Owner is required in this implementation");

		AclObjectIdentity aclObjectIdentity = (AclObjectIdentity) AclObjectIdentity.get(((MutableAcl)acl).getId(), AclObjectIdentity.class, sessionFactory);

		AclObjectIdentity parent = null;
		if (acl.getParentAcl() != null) {
			ObjectIdentity oii = acl.getParentAcl().getObjectIdentity();
			Assert.isInstanceOf(ObjectIdentityImpl.class, oii, "Implementation only supports ObjectIdentityImpl");
			parent = retrieveObjectIdentity(oii);
		}

		aclObjectIdentity.setAclObjectIdentity(parent);
		aclObjectIdentity.setAclSid(createOrRetrieveSid(acl.getOwner(), true));
		aclObjectIdentity.setEntriesInheriting(acl.isEntriesInheriting());
	}

	protected void clearCacheIncludingChildren(ObjectIdentity objectIdentity) {
		Assert.notNull(objectIdentity, "ObjectIdentity required");
		for (ObjectIdentity child : findChildren(objectIdentity)) {
			clearCacheIncludingChildren(child);
		}
		aclCache.evictFromCache(objectIdentity);
	}

	public List<ObjectIdentity> findChildren(ObjectIdentity parentOid) {
		
		Query q = sessionFactory.getCurrentSession().createQuery("select aoi from AclObjectIdentity aoi, AclClass ac where aoi.objectIdIdentity=:objectId and ac.class_=:className");
		q.setParameter("objectId", (long)parentOid.getIdentifier());
		q.setParameter("className", parentOid.getType());
		List<AclObjectIdentity> children = q.getResultList();
		
		if (children == null) {
			return null;
		}

		List<ObjectIdentity> x = new ArrayList<ObjectIdentity>();
		for(AclObjectIdentity aoi : children) {
			try {
				x.add(new ObjectIdentityImpl(lookupClass(aoi.getAclClass().getClass_()), aoi.getObjectIdIdentity()));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return x;
	}

	protected Class<?> lookupClass(String className) throws ClassNotFoundException {
		// workaround for Class.forName() not working in tests
		return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
	}

	public Acl readAclById(ObjectIdentity object) throws NotFoundException {
		return readAclById(object, null);
	}

	public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
		List x = new ArrayList<ObjectIdentity>(1);
		x.add(object);
		Map<ObjectIdentity, Acl> map = readAclsById(x, sids);
		Assert.isTrue(map.containsKey(object),
				"There should have been an Acl entry for ObjectIdentity "+object);
		return map.get(object);
	}

	public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
		return readAclsById (objects, null);
	}

	public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
		Map<ObjectIdentity, Acl> result = aclLookupStrategy.readAclsById(objects, sids);
		//log.debug("readAclsById result : {}", result);
		// Check every requested object identity was found (throw NotFoundException if needed)
		for (ObjectIdentity object : objects) {
			if (!result.containsKey(object)) {
				throw new NotFoundException("Unable to find ACL information for object identity "+object);
			}
		}
		return result;
	}

	protected AclObjectIdentity retrieveObjectIdentity(ObjectIdentity oid) {
		String query = "select aoi from AclObjectIdentity aoi, AclClass ac where aoi.objectIdIdentity=:objectIdIdentity and ac.class_=:className";
		log.debug("query {}", query);
		
		Query q = sessionFactory.getCurrentSession().createQuery(query);
		q.setParameter("objectIdIdentity", (long)oid.getIdentifier());
		q.setParameter("className", oid.getType());
		q.setMaxResults(1);
		
		List<AclObjectIdentity> aclObjectIdentities = q.getResultList();
		
		if(aclObjectIdentities.size() == 1) return  aclObjectIdentities.get(0);
		
		else throw new NotFoundException("No aclObjectIdenty found with "+oid);
	}

}
