package biodiv.userGroup;

import java.util.List;

import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

/**
* Copied from the <code>BasicLookupStrategy</code> private inner class.
*
* @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
*/
public class StubAclParent implements Acl {

	private static final long serialVersionUID = 1;

	private final Long id;

	/**
	 * Constructor.
	 * @param id  the id
	 */
	public StubAclParent(final Long id) {
		this.id = id;
	}

	/**
	 * Get the id.
	 * @return  the id
	 */
	public Long getId() {
		return id;
	}

	public List<AccessControlEntry> getEntries() {
		throw new UnsupportedOperationException("Stub only");
	}

	public ObjectIdentity getObjectIdentity() {
		return new ObjectIdentityImpl(getClass(), 0);
	}

	public Sid getOwner() {
		throw new UnsupportedOperationException("Stub only");
	}

	public Acl getParentAcl() {
		throw new UnsupportedOperationException("Stub only");
	}

	public boolean isEntriesInheriting() {
		throw new UnsupportedOperationException("Stub only");
	}

	public boolean isGranted(final List<Permission> permission, final List<Sid> sids,
			final boolean administrativeMode) {
		throw new UnsupportedOperationException("Stub only");
	}

	public boolean isSidLoaded(final List<Sid> sids) {
		throw new UnsupportedOperationException("Stub only");
	}
}
