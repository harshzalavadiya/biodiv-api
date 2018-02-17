package biodiv.auth.token;

import java.util.List;

import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;


public class TokenDao extends AbstractDao<Token, Long> implements DaoInterface<Token, Long> {

	private static final Logger log = LoggerFactory.getLogger(TokenDao.class);

	public TokenDao() {
		System.out.println("TokenDao constructor");
	}
	
	@Override
	public Token findById(Long id) {
		Token entity = (Token) getCurrentSession().get(Token.class, id);
		return entity;
	}
	
	public List<Token> findByUser(Long userId) {
		Query q = getCurrentSession().createQuery("from Token where user.id=:userId");
		q.setParameter("userId", userId);
		List<Token> tokens = q.getResultList();
		return tokens;
	}
	
    public Token findByValue(String value) {
		Query q = getCurrentSession().createQuery("from Token where value=:value");
		q.setParameter("value", value);
		List<Token> tokens = q.getResultList();
		if(tokens.size() == 1) return  tokens.get(0);
		else throw new NotFoundException("No token found with value : "+value);
	}

	public Token findByValueAndUser(String value, Long userId) {
		Query q = getCurrentSession().createQuery("from Token where value=:value and user.id=:userId");
		q.setParameter("value", value);
		q.setParameter("userId", userId);
		List<Token> tokens = q.getResultList();
		if(tokens.size() == 0) 
			return null;
		else
			return tokens.get(0);
	}
}
