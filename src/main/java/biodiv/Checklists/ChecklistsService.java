package biodiv.Checklists;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.activityFeed.ActivityFeedService;
import biodiv.comment.CommentService;
import biodiv.common.AbstractService;
import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.user.User;
import biodiv.user.UserService;

public class ChecklistsService extends AbstractService<Checklists> {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private ChecklistsDao checklistsDao;
	
	@Inject
	private UserService userService;
	
	@Inject
	private ActivityFeedService activityFeedService;
	
	@Inject
	private CommentService commentService;
	
	@Inject
	ChecklistsService(ChecklistsDao checklistsDao){
		super(checklistsDao);
		this.checklistsDao = checklistsDao;
	}

	public List<String> fetchColumnNames(Checklists cl) {
		
		org.json.JSONObject root = new org.json.JSONObject(cl.getColumns());
		Iterator<String> itr =  root.keys();
		List<String> res = new ArrayList<String>();
		while(itr.hasNext()){
			res.add(itr.next());
		}
		return res;
		
	}

	
}