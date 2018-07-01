package biodiv.userGroup;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.security.acls.model.ObjectIdentity;

import biodiv.common.AbstractObject;

@Entity
@Table(name = "acl_object_identity", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
		"object_id_class", "object_id_identity" }))
public class AclObjectIdentity extends AbstractObject implements java.io.Serializable {

	private long id;
	private AclClass aclClass;
	private AclObjectIdentity aclObjectIdentity;
	private AclSid aclSid;
	private boolean entriesInheriting;
	private long objectIdIdentity;
	private Set<AclObjectIdentity> aclObjectIdentities = new HashSet<AclObjectIdentity>(0);
	private Set<AclEntry> aclEntries = new HashSet<AclEntry>(0);

	public AclObjectIdentity() {
	}

	public AclObjectIdentity(AclClass aclClass, long objectId, AclSid aclSid,
			boolean entriesInheriting) {
		this.aclClass = aclClass;
		this.objectIdIdentity = objectId;
		this.aclSid = aclSid;
		this.entriesInheriting = entriesInheriting;
	}

	@Id
	@GenericGenerator(
	        name = "hibernate_generator",
	        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
	        parameters = {
	                @Parameter(name = "sequence_name", value = "hibernate_sequence"),
	                @Parameter(name = "increment_size", value = "1"),
                    @Parameter(name = "optimizer", value = "hilo")
	        }
	)
	@GeneratedValue(generator = "hibernate_generator")
	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "object_id_class", nullable = false)
	public AclClass getAclClass() {
		return this.aclClass;
	}

	public void setAclClass(AclClass aclClass) {
		this.aclClass = aclClass;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_object")
	public AclObjectIdentity getAclObjectIdentity() {
		return this.aclObjectIdentity;
	}

	public void setAclObjectIdentity(AclObjectIdentity aclObjectIdentity) {
		this.aclObjectIdentity = aclObjectIdentity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_sid")
	public AclSid getAclSid() {
		return this.aclSid;
	}

	public void setAclSid(AclSid aclSid) {
		this.aclSid = aclSid;
	}

	@Column(name = "entries_inheriting", nullable = false)
	public boolean isEntriesInheriting() {
		return this.entriesInheriting;
	}

	public void setEntriesInheriting(boolean entriesInheriting) {
		this.entriesInheriting = entriesInheriting;
	}

	@Column(name = "object_id_identity", nullable = false)
	public long getObjectIdIdentity() {
		return this.objectIdIdentity;
	}

	public void setObjectIdIdentity(long objectIdIdentity) {
		this.objectIdIdentity = objectIdIdentity;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aclObjectIdentity")
	public Set<AclObjectIdentity> getAclObjectIdentities() {
		return this.aclObjectIdentities;
	}

	public void setAclObjectIdentities(Set<AclObjectIdentity> aclObjectIdentities) {
		this.aclObjectIdentities = aclObjectIdentities;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aclObjectIdentity")
	public Set<AclEntry> getAclEntries() {
		return this.aclEntries;
	}

	public void setAclEntries(Set<AclEntry> aclEntries) {
		this.aclEntries = aclEntries;
	}

}
