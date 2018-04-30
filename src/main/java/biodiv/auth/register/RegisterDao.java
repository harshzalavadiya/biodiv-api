package biodiv.auth.register;

import javax.inject.Inject;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;

public class RegisterDao extends AbstractDao<RegistrationCode, Long> {

	@Inject
	public RegisterDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public RegistrationCode findById(Long id) {
		RegistrationCode entity = (RegistrationCode) sessionFactory.getCurrentSession().get(RegistrationCode.class, id);
		return entity;
	}

}
