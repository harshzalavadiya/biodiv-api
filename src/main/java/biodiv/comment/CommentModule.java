package biodiv.comment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class CommentModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected void configureServlets() {
		log.debug("Configuring CommentModule Servlets");
		bind(Comment.class);
		
		bind(CommentDao.class).in(Singleton.class);
		bind(CommentService.class).in(Singleton.class);
		
		bind(CommentController.class).in(Singleton.class);
	}
}
