package biodiv.userGroup;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.ws.rs.NotFoundException;

import org.hibernate.SessionFactory;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.google.inject.Inject;

import biodiv.common.AbstractObject;
import biodiv.user.User;

@Entity
@Table(name = "acl_sid", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = { "sid", "principal" }))
public class AclSid extends AbstractObject implements java.io.Serializable {

	private long id;
	private boolean principal;
	private String sid;
	private Set<AclEntry> aclEntries = new HashSet(0);
	private Set<AclObjectIdentity> aclObjectIdentities = new HashSet(0);

	public AclSid() {
		
	}

	public AclSid(String sid, boolean principal) {
		this.principal = principal;
		this.sid = sid;
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

	@Column(name = "principal", nullable = false)
	public boolean isPrincipal() {
		return this.principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	@Column(name = "sid", nullable = false)
	public String getSid() {
		return this.sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aclSid")
	public Set<AclEntry> getAclEntries() {
		return this.aclEntries;
	}

	public void setAclEntries(Set<AclEntry> aclEntries) {
		this.aclEntries = aclEntries;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aclSid")
	public Set<AclObjectIdentity> getAclObjectIdentities() {
		return this.aclObjectIdentities;
	}

	public void setAclObjectIdentities(Set<AclObjectIdentity> aclObjectIdentities) {
		this.aclObjectIdentities = aclObjectIdentities;
	}

	public static AclSid findBySidAndPrincipal(String sidName, boolean principal, SessionFactory sessionFactory) {
		Query q = sessionFactory.getCurrentSession().createQuery("from AclSid where sid=:sid and principal=:principal");
		q.setParameter("sid", sidName);
		q.setParameter("principal", Boolean.valueOf(principal));
		AclSid aclSid = null;

        try {
        	aclSid = (AclSid) q.getSingleResult();
        } catch(NoResultException e ) {
            //e.printStackTrace();
            //throw new NotFoundException(e);

        }
        System.out.println("------------__*************-----------------");
        System.out.println(aclSid);
        return aclSid;
	}

}
