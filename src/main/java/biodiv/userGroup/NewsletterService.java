package biodiv.userGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.common.AbstractDao;
import biodiv.common.AbstractService;
import biodiv.common.Language;
import biodiv.user.User;
import biodiv.user.UserService;

public class NewsletterService extends AbstractService<Newsletter>{
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private NewsletterDao newsletterDao;
	
	@Inject
	private UserService userService;
	
	@Inject
	private UserGroupService userGroupService;
	
	@Inject
	NewsletterService(NewsletterDao newsletterDao) {
		super(newsletterDao);
		this.newsletterDao = newsletterDao;
		log.trace("NewsletterService constructor");
	}
	
	@Transactional
	public List<Map<String, Object>> getPages(Long ugId, Long max, Long offset, String sort, String order,
			String currentLanguage, Map<String,Object> filterParams,Long userId,Boolean showInFooter) {
		
		User user;
		if(userId != null){
			user = userService.findById(userId);
		}
		 
		Map<String,Object> params = new HashMap<String,Object>();
		Newsletter nl = new Newsletter();
		String query = "select nl.id,nl.version,nl.date,nl.newsitem,nl.title,nl.sticky,nl.userGroup.id,nl.displayOrder,"
				+ "nl.language.id,nl.parentId,nl.showInFooter";
		 query += " from Newsletter nl";
		if(ugId != null){
			UserGroup userGroup = userGroupService.findById(ugId);
			query += " where nl.userGroup =:userGroup";
			//params.put("ug", ug);
			nl.setUserGroup(userGroup);
			
			if(userId == null || !userGroupService.isFounder(userGroup,userId)){
				query += " and nl.sticky =:sticky";
				//params.put("sticky",true);
				nl.setSticky(true);
			}
		}else{
			query += " where nl.userGroup is null";
			query += " and nl.sticky =:sticky";
			//params.put("sticky", true);
			nl.setSticky(true);
		}
		
		if(showInFooter != null){
			query += " and nl.showInFooter =:showInFooter";
			nl.setShowInFooter(showInFooter);
		}
		if(currentLanguage != null){
			Language language = null;
			query += " and nl.language =:language";
			//params.put("languageId",currentLanguage);
			nl.setLanguage(language);
		}
		
		if(filterParams != null){
//			for(String filter : filterParams){
//				
//			}
			if(filterParams.get("date") != null){
				query += "  and nl.date =:date";
				nl.setDate((Date)filterParams.get("date"));
			}
			if(filterParams.get("newsitem") != null){
				query += " nl.newsitem =:newsitem";
				nl.setNewsitem((String)filterParams.get("newsitem"));
			}
			if(filterParams.get("title") != null){
				query += " and nl.title =:title";
				nl.setTitle((String)filterParams.get("title"));
			}
			if(filterParams.get("displayOrder") != null){
				query += " and nl.displayOrder =:displayOrder";
				nl.setDisplayOrder((Integer)filterParams.get("displayOrder"));
			}
			if(filterParams.get("parentId") != null){
				query += " and nl.parentId =:parentId";
				nl.setParentId((Long)filterParams.get("parentId"));
			}
//			if(filterParams.get("showInFooter") != null){
//				query += " and nl.showInFooter =:showInFooter";
//				nl.setShowInFooter((Boolean)filterParams.get("showInFooter"));
//			}
		}
		
		if(max != null && max != -1){
			params.put("max", max);
		}
		
		if(offset != null && offset != -1){
			params.put("offset", offset);
		}
		
		if(sort == null){
			sort = " displayOrder";
		}
		
		if(order == null){
			order = " desc";
		}
		
		if(sort != null){
			sort = sort;
			query += " order by nl."+sort;
		}
		
		if(order != null){
			order = order;
			query += " "+order;
		}
		
		List<Object[]> n = newsletterDao.getPages(nl,params,query);
		List<Map<String, Object>> pages = new ArrayList<Map<String, Object>>();
		for(Object[] obj : n){
			Map<String, Object> res = new HashMap<String, Object>();
			res.put("id", (Long) obj[0]);
			res.put("version", (Long) obj[1]);
			res.put("date", (Date) obj[2]);
			res.put("title", (String) obj[4]);
			res.put("sticky", (Boolean) obj[5]);
			res.put("userGroupId", (Long) obj[6]);
			res.put("displayOrder", (Integer) obj[7]);
			res.put("languageId", (Long) obj[8]);
			res.put("parentId", (Long) obj[9]);
			res.put("showInFooter", (Boolean) obj[10]);
			pages.add(res);
		}
		return pages;
	}

	

}
