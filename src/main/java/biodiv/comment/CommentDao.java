package biodiv.comment;

import java.util.List;

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
		
		String hql = "select c.body from Comment c where c.id =:id";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("id", id);
		String resultBody = (String) query.getSingleResult();
		return resultBody;
	}

	public List<Comment> findAllByParentId(Long id) {
		String hql = "from Comment c where c.parentId =:id";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("id",id);
		List<Comment> listResult = query.getResultList();
		return listResult;
	}

	public Long getTotalRecoCommentCount(Long recoId, Long obvId) {
		String hql = "select count(*) from Comment c where c.commentHolderType =:commentHolderType and c.commentHolderId =:commentHolderId and"
				+ " c.rootHolderType =:rootHolderType and c.rootHolderId =:rootHolderId";
		
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("commentHolderType", "species.participation.Recommendation");
		query.setParameter("commentHolderId", recoId);
		query.setParameter("rootHolderType", "species.participation.Observation");
		query.setParameter("rootHolderId", obvId);
		System.out.println(recoId);
		System.out.println(obvId);
		Long count = (Long) query.getSingleResult();
		System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
		System.out.println(count);
		return count;
	}

}
