package biodiv.auth.token;

import java.util.List;

import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.user.User;


public class TokenDao extends AbstractDao<Token, Long> implements DaoInterface<Token, Long> {

	private static final Logger log = LoggerFactory.getLogger(TokenDao.class);

	@Override
	public Token findById(Long id) {
		Token entity = (Token) getCurrentSession().get(Token.class, id);
		return entity;
	}
	
	public List<Token> findByUser(User user) {
		Query q = getCurrentSession().createQuery("from Token where user=:user");
		q.setParameter("user", user);
		List<Token> tokens = q.getResultList();
		return tokens;
	}
	
	public Token findByValueAndUser(String value, User user) {
		Query q = getCurrentSession().createQuery("from Token where value=:value and user=:user");
		q.setParameter("value", value);
		q.setParameter("user", user);
		List<Token> tokens = q.getResultList();
		if(tokens.size() == 0) 
			return null;
		else
			return tokens.get(0);
	}
}
