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
import javax.ws.rs.NotFoundException;

import org.hibernate.SessionFactory;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import biodiv.common.AbstractObject;

@Entity
@Table(name = "acl_class", schema = "public")
public class AclClass extends AbstractObject implements java.io.Serializable {

	private long id;
	private String class_;
	private Set<AclObjectIdentity> aclObjectIdentities = new HashSet<AclObjectIdentity>(0);

	public AclClass() {
	}

	public AclClass(String class_) {
		this.class_ = class_;
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

	@Column(name = "class", nullable = false)
	public String getClass_() {
		return this.class_;
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aclClass")
	public Set<AclObjectIdentity> getAclObjectIdentities() {
		return this.aclObjectIdentities;
	}

	public void setAclObjectIdentities(Set<AclObjectIdentity> aclObjectIdentities) {
		this.aclObjectIdentities = aclObjectIdentities;
	}

	public static AclClass findByClassName(String className, SessionFactory sessionFactory) {
		Query q = sessionFactory.getCurrentSession().createQuery("from AclClass where class_=:class_");
		q.setParameter("class_", className);
		AclClass aclClass = null;

        try {
        	aclClass = (AclClass) q.getSingleResult();
        } catch(NoResultException e ) {
            e.printStackTrace();
            throw new NotFoundException(e);

        }

        if(aclClass != null) return aclClass;
        else return null;
	}

}
