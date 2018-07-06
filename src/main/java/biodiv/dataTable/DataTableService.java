package biodiv.dataTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.activityFeed.ActivityFeedService;
import biodiv.comment.CommentService;
import biodiv.common.AbstractService;
import biodiv.observation.RecommendationVote;
import biodiv.observation.RecommendationVoteDao;
import biodiv.user.UserService;

public class DataTableService extends AbstractService<DataTable> {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private DataTableDao dataTableDao;
	
	@Inject
	private UserService userService;
	
	@Inject
	private ActivityFeedService activityFeedService;
	
	@Inject
	private CommentService commentService;
	
	@Inject
	DataTableService(DataTableDao dataTableDao){
		super(dataTableDao);
		this.dataTableDao = dataTableDao;
	}

	public List<List<String>> fetchColumnNames(DataTable dataTable) {
		
		List<List<String>> res = new ArrayList<List<String>>();
		if(dataTable.getColumns()!=null){
			//org.json.JSONObject root = new org.json.JSONObject(dataTable.getColumns());
			JSONArray root = new JSONArray(dataTable.getColumns());
			for(int i=0 ; i< root.length() ; i++){
				res.add((List<String>) root.get(i));
			}
			//Iterator<String> itr = root.length();
			
//			while(itr.hasNext()){
//				res.add(itr.next());
//			}
		}
		return res;
	}
	
//	public static void main(String[] args){
//		JSONArray root = new JSONArray("[[\"http://r...content-available-to-author-only...g.org/dwc/terms/scientificName\",\"elephus maximus\",10.1],[\"http://r...content-available-to-author-only...g.org/dwc/terms/vernacularName\",\"elephant\",10.2],[\"\",\"1\",1000.0],[\"\",\"media\",1000.0]]");
//		System.out.println(root.get(0));
//		
//	}
}
