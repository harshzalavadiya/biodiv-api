package biodiv.comment;

import javax.ws.rs.Path;

@Path("/comments")
public class CommentController {
	
	CommentService commentService =  new CommentService();
	
	

}
