package biodiv.comment;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class CommentDao extends AbstractDao<Comment,Long> implements DaoInterface<Comment,Long>{

	@Override
	public Comment findById(Long id) {
		Comment entity = (Comment) getCurrentSession().get(Comment.class, id);
		return entity;
	}

	public String getCommentBody(long id) {
		
		String hql = "select c.body from Comment c where id =:id";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("id", id);
		String resultBody = (String) query.getSingleResult();
		return resultBody;
	}

}
