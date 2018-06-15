package biodiv.comment;

import java.util.List;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.common.Language;
import biodiv.user.User;

public class CommentDao extends AbstractDao<Comment,Long> implements DaoInterface<Comment,Long>{

	public CommentDao() {
		System.out.println("CommentDao constructor");
	}
	
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
		System.out.println(query.getSingleResult());
		System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
		System.out.println(count);
		return count;
	}

	public Comment findByAuthorCommentHolderAndRootHolder(Language lang, User user, String commentBody,
			String commentHolderType, Long commentHolderId, String rootHolderType, Long rootHolderId) {
		
		String hql = "from Comment c where c.language.id =:languageId and c.rootHolderType =:rootHolderType and c.rootHolderId =:rootHolderId "
				+ "and c.commentHolderType =:commentHolderType and c.commentHolderId =:commentHolderId and c.body =:commentBody";
		
		
			Query query = getCurrentSession().createQuery(hql);
			query.setParameter("commentHolderType", commentHolderType);
			query.setParameter("commentHolderId", commentHolderId);
			query.setParameter("rootHolderType", rootHolderType);
			query.setParameter("rootHolderId",rootHolderId);
			query.setParameter("commentBody",commentBody);
			query.setParameter("languageId",lang.getId());
			
			List<Comment> cl = query.getResultList();
			if(cl.size()>0){
				return cl.get(0);
			}else{
				return null;
			}
		
		
	}

}
