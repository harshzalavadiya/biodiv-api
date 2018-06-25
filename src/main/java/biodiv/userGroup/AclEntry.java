package biodiv.userGroup;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.ws.rs.NotFoundException;

import org.hibernate.SessionFactory;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import biodiv.common.AbstractObject;

@Entity
@Table(name = "acl_entry", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
		"acl_object_identity", "ace_order" }))
public class AclEntry extends AbstractObject implements java.io.Serializable {

	private long id;
	private AclSid aclSid;
	private AclObjectIdentity aclObjectIdentity;
	private int aceOrder;
	private boolean auditFailure;
	private boolean auditSuccess;
	private boolean granting;
	private int mask;

	public AclEntry() {
	}

	public AclEntry(AclSid aclSid, AclObjectIdentity aclObjectIdentity, int aceOrder, boolean auditFailure,
			boolean auditSuccess, boolean granting, int mask) {
		this.id = id;
		this.aclSid = aclSid;
		this.aclObjectIdentity = aclObjectIdentity;
		this.aceOrder = aceOrder;
		this.auditFailure = auditFailure;
		this.auditSuccess = auditSuccess;
		this.granting = granting;
		this.mask = mask;
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
	@JoinColumn(name = "sid", nullable = false)
	public AclSid getAclSid() {
		return this.aclSid;
	}

	public void setAclSid(AclSid aclSid) {
		this.aclSid = aclSid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "acl_object_identity", nullable = false)
	public AclObjectIdentity getAclObjectIdentity() {
		return this.aclObjectIdentity;
	}

	public void setAclObjectIdentity(AclObjectIdentity aclObjectIdentity) {
		this.aclObjectIdentity = aclObjectIdentity;
	}

	@Column(name = "ace_order", nullable = false)
	public int getAceOrder() {
		return this.aceOrder;
	}

	public void setAceOrder(int aceOrder) {
		this.aceOrder = aceOrder;
	}

	@Column(name = "audit_failure", nullable = false)
	public boolean isAuditFailure() {
		return this.auditFailure;
	}

	public void setAuditFailure(boolean auditFailure) {
		this.auditFailure = auditFailure;
	}

	@Column(name = "audit_success", nullable = false)
	public boolean isAuditSuccess() {
		return this.auditSuccess;
	}

	public void setAuditSuccess(boolean auditSuccess) {
		this.auditSuccess = auditSuccess;
	}

	@Column(name = "granting", nullable = false)
	public boolean isGranting() {
		return this.granting;
	}

	public void setGranting(boolean granting) {
		this.granting = granting;
	}

	@Column(name = "mask", nullable = false)
	public int getMask() {
		return this.mask;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	public static List<AclEntry> findAllByAclObjectIdentity(AclObjectIdentity aclObjectIdentity,
			SessionFactory sessionFactory) {
		Query q = sessionFactory.getCurrentSession().createQuery("from AclObjectIdentity where aclObjectIdentity=:aclObjectIdentity");
		q.setParameter("aclObjectIdentity", aclObjectIdentity);
		List<AclEntry> aclEntries = null;

        try {
        	aclEntries = (List<AclEntry>) q.getResultList();
        } catch(NoResultException e ) {
            e.printStackTrace();
            throw new NotFoundException(e);

        }
        return aclEntries;
	}

}
