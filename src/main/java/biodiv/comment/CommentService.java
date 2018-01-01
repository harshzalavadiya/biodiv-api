package biodiv.comment;

import biodiv.Intercept;
import biodiv.common.AbstractService;

public class CommentService extends AbstractService<Comment>{

	private CommentDao commentDao;
	
	public CommentService(){
		this.commentDao = new CommentDao();
	}
	
	@Override
	public CommentDao getDao(){
		return commentDao;
	}
	
	@Intercept
	public String getCommentBody(long id){
		
		String body;
		
		try{
			body = getDao().getCommentBody(id);
			return body;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		
	}
}
