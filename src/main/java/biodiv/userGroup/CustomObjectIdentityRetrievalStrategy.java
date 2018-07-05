package biodiv.userGroup;

import java.io.Serializable;

import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityGenerator;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;

class CustomObjectIdentityRetrievalStrategy implements ObjectIdentityRetrievalStrategy, ObjectIdentityGenerator {

	@Override
	public ObjectIdentity getObjectIdentity(Object domainObject) {
		return createObjectIdentity(((UserGroup) domainObject).getId(), domainObject.getClass().getName());
	}

	// HACK to handle class name in object identity
	public ObjectIdentity getObjectIdentity(UserGroup domainObject) {
		return createObjectIdentity(((UserGroup) domainObject).getId(), "species.groups.UserGroup");
	}

	@Override
	public ObjectIdentity createObjectIdentity(Serializable id, String type) {
		return new ObjectIdentityImpl(type, id);
	}

}
