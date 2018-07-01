package biodiv.userGroup;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.google.inject.Inject;


class AclLookupStrategy implements LookupStrategy {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());


	protected Field aceAclField;

	//protected HibernateProxyHandler hibernateProxyHandler;

	AclLookupStrategy() {
		findAceAclField();
		//createHibernateProxyHandler();
	}

	@Inject
	AclAuthorizationStrategy aclAuthorizationStrategy;

	@Inject
	AclCache aclCache;

	@Inject
	PermissionFactory permissionFactory;

	@Inject
	PermissionGrantingStrategy permissionGrantingStrategy;
	
	@Inject
	SessionFactory sessionFactory;

	int batchSize = 50;

	public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) {
		Map<ObjectIdentity, Acl> result = new HashMap<ObjectIdentity, Acl>();
		Set<ObjectIdentity> currentBatchToLoad = new HashSet<ObjectIdentity>();

		for(int i=0; i<objects.size(); i++) {
			// Check we don't already have this ACL in the results
			boolean aclFound = result.containsKey(objects.get(i));
			log.trace("aclFound {}", aclFound);
			// Check cache for the present ACL entry
			if (aclFound == false) {
				Acl acl = aclCache.getFromCache(objects.get(i));
				log.trace("aclFromCache {}", acl);
				// Ensure any cached element supports all the requested SIDs
				// (they should always, as our base impl doesn't filter on SID)
				if (acl != null) {
					Assert.state(acl.isSidLoaded(sids),
						"Error: SID-filtered element detected when implementation does not perform SID filtering " +
						"- have you added something to the cache manually?");

					result.put(acl.getObjectIdentity(), acl);
					log.trace("Putting acl in cache");
					aclFound = true;
				}
			}

			// Load the ACL from the database
			if (aclFound == false) {
				log.debug("to load {} from db", currentBatchToLoad);
				currentBatchToLoad.add(objects.get(i));
				log.debug("to load {} from db", objects.get(i));
			}
			

			// Is it time to load from JDBC the currentBatchToLoad?
			if (currentBatchToLoad.size() == batchSize || (i + 1) == objects.size()) {
				if (currentBatchToLoad.size() > 0) {
					Map<ObjectIdentity, Acl> loadedBatch = lookupObjectIdentities(currentBatchToLoad, sids);
					// Add loaded batch (all elements 100% initialized) to results
					result.putAll(loadedBatch);
					// Add the loaded batch to the cache
					for (Acl acl : loadedBatch.values()) {
						aclCache.putInCache((MutableAcl) acl);
					}
					currentBatchToLoad.clear();
				}
			}
		}

		return result;
	}

	protected Map<ObjectIdentity, Acl> lookupObjectIdentities(
			Collection<ObjectIdentity> objectIdentities, List<Sid> sids) {

		Assert.notEmpty(objectIdentities, "Must provide identities to lookup");

		Map<Serializable, Acl> acls = new HashMap<Serializable, Acl>(); // contains Acls with StubAclParents

		String query = "select aoi from AclObjectIdentity aoi, AclClass ac where ";
		for (ObjectIdentity objectIdentity : objectIdentities) {
			query += " ((aoi.objectIdIdentity="+objectIdentity.getIdentifier()+ " and ac.class_= '"+objectIdentity.getType()+"') or ";
		}
		
		query = query.substring(0, query.length()-4);
		query +=") order by aoi.objectIdIdentity asc";
		log.debug("query : {}", query);
		
		Query q = sessionFactory.getCurrentSession().createQuery(query);
		List<AclObjectIdentity> aclObjectIdentities = q.getResultList();
		
		log.trace("aclObjectIdentities {}", aclObjectIdentities);
		//unwrapProxies aclObjectIdentities

		Map<AclObjectIdentity, List<AclEntry>> aclObjectIdentityMap = findAcls(aclObjectIdentities);

		log.trace("aclObjectIdentityMap {}", aclObjectIdentityMap);
		
		List<AclObjectIdentity> parents = convertEntries(aclObjectIdentityMap, acls, sids);
		log.trace("acls {}", acls);
		
		if (parents != null) {
			lookupParents(acls, parents, sids);
		}

		// Finally, convert our 'acls' containing StubAclParents into true Acls
		Map<ObjectIdentity, Acl> result = new HashMap<ObjectIdentity, Acl>();
		for (Acl inputAcl : acls.values()) {
			Acl converted = convert(acls, ((AclImpl)inputAcl).getId());
			result.put(converted.getObjectIdentity(), converted);
		}

		return result;
	}

//	protected void unwrapProxies(List<AclObjectIdentity> aclObjectIdentities) {
//		if (!hibernateProxyHandler) {
//			return
//		}
//		for (ListIterator<AclObjectIdentity> iter = aclObjectIdentities.listIterator(); iter.hasNext(); ) {
//			iter.set hibernateProxyHandler.unwrapIfProxy(iter.next())
//		}
//	}

	protected Map<AclObjectIdentity, List<AclEntry>> findAcls(List<AclObjectIdentity> aclObjectIdentities) {

		List<AclEntry> entries = null;
		if (aclObjectIdentities != null && aclObjectIdentities.size() > 0) {
			
			String query = "from AclEntry where aclObjectIdentity in (:aclObjectIdentities) order by aceOrder asc";
			log.debug("query : {} with params {}", query, aclObjectIdentities);
			
			Query q = sessionFactory.getCurrentSession().createQuery(query);			
			q.setParameter("aclObjectIdentities", aclObjectIdentities);
			entries = q.getResultList();
			log.debug("result entries : {}", entries);
		}

		Map<AclObjectIdentity, List<AclEntry>>  map = new HashMap<AclObjectIdentity, List<AclEntry>>();
		for (AclObjectIdentity aclObjectIdentity : aclObjectIdentities) {
			System.out.println(aclObjectIdentity);
			map.put(aclObjectIdentity, new ArrayList<AclEntry>());
		}

		if(entries != null) {
			for(AclEntry entry : entries) {
				((List<AclEntry>) map.get(entry.getAclObjectIdentity())).add(entry);
			}
		}

		return map;
	}

	protected AclImpl convert(Map<Serializable, Acl> inputMap, Serializable currentIdentity) {
		Assert.notEmpty (inputMap, "InputMap required");
		Assert.notNull (currentIdentity, "CurrentIdentity required");

		// Retrieve this Acl from the InputMap
		AclImpl inputAcl = (AclImpl)inputMap.get(currentIdentity);
		Assert.isInstanceOf(AclImpl.class, inputAcl, "The inputMap contained a non-AclImpl");

		Acl parent = inputAcl.getParentAcl();
		if (parent instanceof StubAclParent) {
			parent = convert(inputMap, ((StubAclParent) parent).getId());
		}

		// Now we have the parent (if there is one), create the true AclImpl
		AclImpl result = new AclImpl(inputAcl.getObjectIdentity(), inputAcl.getId(),
				aclAuthorizationStrategy, permissionGrantingStrategy, parent, null /*List<Sid> loadedSids*/,
				inputAcl.isEntriesInheriting(), inputAcl.getOwner());

		List acesNew = new ArrayList();
		Field acesField = ReflectionUtils.findField(AclImpl.class, "aces");
		acesField.setAccessible(true);
		List<AccessControlEntryImpl> aces = (List<AccessControlEntryImpl>) ReflectionUtils.getField(acesField,  inputAcl);
		for (AccessControlEntryImpl ace : aces) { //inputAcl.@aces) {
			ReflectionUtils.setField(aceAclField, ace, result);
			acesNew.add(ace);
		}
		
		List<AccessControlEntryImpl> resultAces = (List<AccessControlEntryImpl>) ReflectionUtils.getField(acesField,  result);
		resultAces.clear();
		resultAces.addAll(acesNew);

		return result;
	}

	protected List<AclObjectIdentity> convertEntries(Map<AclObjectIdentity, List<AclEntry>> aclObjectIdentityMap,
			Map<Serializable, Acl> acls, List<Sid> sids) {

		List<AclObjectIdentity> parents = new ArrayList<AclObjectIdentity>();

		for(Map.Entry<AclObjectIdentity, List<AclEntry>> entrySet : aclObjectIdentityMap.entrySet()) {
			AclObjectIdentity aclObjectIdentity = entrySet.getKey();
			List<AclEntry> aclEntries = entrySet.getValue();
			createAcl(acls, aclObjectIdentity, aclEntries);

			if (aclObjectIdentity.getAclObjectIdentity() == null) {
				return null;
			}

			Serializable parentId = aclObjectIdentity.getAclObjectIdentity().getId();
			if (acls.containsKey(parentId)) {
				return null;
			}

			// Now try to find it in the cache
			MutableAcl cached = aclCache.getFromCache(parentId);
			if (cached == null || !cached.isSidLoaded(sids)) {
				parents.add(aclObjectIdentity.getAclObjectIdentity());
			}
			else {
				// Pop into the acls map, so our convert method doesn't
				// need to deal with an unsynchronized AclCache
				acls.put(cached.getId(), cached);
			}
		}

		return parents;
	}

	protected void createAcl(Map<Serializable, Acl> acls, AclObjectIdentity aclObjectIdentity,
			List<AclEntry> entries) {

		Serializable id = aclObjectIdentity.getId();

		// If we already have an ACL for this ID, just create the ACE
		AclImpl acl = (AclImpl) acls.get(id);
		if (acl == null) {
			// Make an AclImpl and pop it into the Map
			ObjectIdentity objectIdentity = new ObjectIdentityImpl(
					lookupClassType(aclObjectIdentity.getAclClass().getClass_()),
					aclObjectIdentity.getObjectIdIdentity());
			Acl parentAcl = null;
			if (aclObjectIdentity.getAclObjectIdentity() != null) {
				parentAcl = new StubAclParent(aclObjectIdentity.getAclObjectIdentity().getId());
			}

			AclSid ownerSid = aclObjectIdentity.getAclSid();
			Sid owner = ownerSid.isPrincipal() ?
					new PrincipalSid(ownerSid.getSid()) :
					new GrantedAuthoritySid(ownerSid.getSid());

			acl = new AclImpl(objectIdentity, id, aclAuthorizationStrategy, permissionGrantingStrategy,
					parentAcl, null /*List<Sid> loadedSids*/, aclObjectIdentity.isEntriesInheriting(), owner);
			acls.put(id, acl);
		}

		Field acesField = ReflectionUtils.findField(AclImpl.class, "aces");
		acesField.setAccessible(true);
		List<AccessControlEntryImpl> aces = (List<AccessControlEntryImpl>) ReflectionUtils.getField(acesField,  acl);		
		//List aces = acl.@aces;
		log.debug("{}",acesField);
		log.debug("{}",aces);
		for (AclEntry entry : entries) {
			// Add an extra ACE to the ACL (ORDER BY maintains the ACE list order)
			// It is permissable to have no ACEs in an ACL
			String aceSid = (entry.getAclSid()!=null)?entry.getAclSid().getSid():null;
			if (aceSid != null) {
				Sid recipient = entry.getAclSid().isPrincipal() ? new PrincipalSid(aceSid) : new GrantedAuthoritySid(aceSid);

				Permission permission = permissionFactory.buildFromMask(entry.getMask());
				AccessControlEntryImpl ace = new AccessControlEntryImpl(entry.getId(), acl, recipient, permission,
						entry.isGranting(), entry.isAuditSuccess(), entry.isAuditFailure());

				// Add the ACE if it doesn't already exist in the ACL.aces field
				if (!aces.contains(ace)) {
					aces.add(ace);
				}
			}
		}
	}

	protected Class<?> lookupClass(String className) {
		// workaround for Class.forName() not working in tests
		try {
			if(className.equals("species.groups.UserGroup")) {
				className = "biodiv.userGroup.UserGroup";
			}
			return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected String lookupClassType(String className) {
		// workaround for Class.forName() not working in tests
		try {
			if(className.equals("species.groups.UserGroup")) {
				className = "biodiv.userGroup.UserGroup";
			}
			if(Class.forName(className, true, Thread.currentThread().getContextClassLoader()) != null) {
				return "species.groups.UserGroup";
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void lookupParents(Map<Serializable, Acl> acls, Collection<AclObjectIdentity> findNow,
			List<Sid> sids) {

		Assert.notNull (acls, "ACLs are required");
		Assert.notEmpty (findNow, "Items to find now required");

		Map<AclObjectIdentity, List<AclEntry>> aclObjectIdentityMap = findAcls((List)findNow);
		List<AclObjectIdentity> parents = convertEntries(aclObjectIdentityMap, acls, sids);
		if (parents != null) {
			lookupParents(acls, parents, sids);
		}
	}

	protected void findAceAclField() {
		aceAclField = ReflectionUtils.findField(AccessControlEntryImpl.class, "acl");
		aceAclField.setAccessible(true);
	}

/*	protected void createHibernateProxyHandler() {
		try {
			Class<?> c = lookupClass("org.codehaus.groovy.grails.orm.hibernate.proxy.HibernateProxyHandler");
			hibernateProxyHandler = c.newInstance();
		}
		catch (Exception ignored) {}
	}
*/
}