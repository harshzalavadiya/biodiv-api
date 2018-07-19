package biodiv.observation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.apache.commons.mail.HtmlEmail;
import org.hibernate.SessionFactory;
import org.jvnet.hk2.annotations.Service;

import biodiv.Transactional;
import biodiv.Checklists.Checklists;
import biodiv.Checklists.ChecklistsService;
import biodiv.activityFeed.ActivityFeedService;
import biodiv.comment.CommentService;
import biodiv.common.AbstractService;
import biodiv.common.Language;
import biodiv.common.LanguageService;
import biodiv.common.SpeciesGroup;
import biodiv.common.SpeciesGroupService;
import biodiv.customField.CustomField;
import biodiv.customField.CustomFieldService;
import biodiv.dataTable.DataTableService;
import biodiv.observation.RecommendationVote.ConfidenceType;
import biodiv.rating.RatingLinkService;
import biodiv.speciesPermission.SpeciesPermission;
import biodiv.speciesPermission.SpeciesPermission.PermissionType;
import biodiv.speciesPermission.SpeciesPermissionService;
import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.taxon.datamodel.dao.TaxonomyRegistry;
import biodiv.taxon.service.TaxonService;
import biodiv.user.User;
import biodiv.user.UserService;
import biodiv.userGroup.UserGroup;
import biodiv.userGroup.UserGroupMailingService;
import biodiv.userGroup.UserGroupService;
import net.minidev.json.JSONObject;

@Service
public class ObservationService extends AbstractService<Observation> {

	private ObservationDao observationDao;
	
	@Inject
	UserGroupService userGroupService;
	
	@Inject
	private CustomFieldService customFieldService;

	@Inject
	private SpeciesGroupService speciesGroupService;

	@Inject
	private ActivityFeedService activityFeedService;

	@Inject
	private UserService userService;

	@Inject
	private RecommendationService recommendationService;

	@Inject
	private CommentService commentService;

	@Inject
	private LanguageService languageService;
	
	@Inject
	private ObservationListService observationListService;
	
	@Inject
	private SpeciesPermissionService speciesPermissionService;
	
	@Inject
	private SessionFactory sessionFactory;
	
	@Inject
	private RecommendationVoteService recommendationVoteService;
	
	@Inject
	ChecklistsService checklistsService;
	
	@Inject
	DataTableService dataTableService;
	
	@Inject
	RatingLinkService ratingLinkService;
	
	@Inject
	TaxonService taxonService;
	
	

	@Inject
	ObservationService(ObservationDao observationDao) {
		super(observationDao);
		this.observationDao = observationDao;
		System.out.println("ObservationService constructor");
	}

	public List<Observation> findAllByGroup(int max, int offset) {
		System.out.println("alter");
		return null;
	}

	@Transactional
	public List<UserGroup> obvUserGroups(long id) {
		try {
			List<UserGroup> usrGrps = observationDao.obvUserGroups(id);
			return usrGrps;
		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	@Transactional
	public String updateInlineCf(String fieldValue, Long cfId, Long obvId, long userId,Long loggedInUserId,Boolean isAdmin) throws Exception {

		String msg;
		try {
			Observation obv = findById(obvId);
			Set<UserGroup> obvUserGrps = obv.getUserGroups();
			List<Object> toReturn = customFieldService.updateInlineCf(fieldValue, cfId, obvId, userId, obvUserGrps,loggedInUserId,obv.getAuthor().getId(),
					isAdmin);
			
			
			Date lastrevised = new Date();
			obv.setLastRevised(lastrevised);
			save(obv);
			
			//elastic elastic
			if(((String)toReturn.get(0)).equalsIgnoreCase("success")){
				JSONObject obj = new JSONObject();
				
				SimpleDateFormat out = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");
				SimpleDateFormat in = new SimpleDateFormat("EEE MMM dd YYYY HH:mm:ss");
				String newDate=out.format(obv.getLastRevised());
				obj.put("lastrevised",newDate);
				JSONObject cfObj = new JSONObject();
			
				cfObj.put(cfId.toString(), toReturn.get(1));
				obj.put("custom_fields", cfObj);
				//System.out.println("testin elastic*********************************************************************** "+obj.toString());
				observationListService.update("observation", "observation", obv.getId().toString(), obj.toString());
				
				//elastic elastic
			}
			msg = (String)toReturn.get(0);
			return msg;
		} catch (Exception e) {
			throw e;
		} finally {

		}
	}

	@Transactional
	public List<Map<String, Object>> getCustomFields(Long obvId) {

		try {
			Observation obv = findById(obvId);
			Set<UserGroup> obvUserGrps = obv.getUserGroups();
			List<Map<String, Object>> cf = customFieldService.getAllCustomfiedls(obvId, obvUserGrps);
			return cf;
		} catch (Exception e) {
			throw e;
		} finally {

		}
	}

	@Transactional
	public List<ObservationResource> getResouce(long id) {
		// TODO Auto-generated method stub
		List<ObservationResource> observationResources = observationDao.getResource(id);
		return observationResources;

	}

	@Transactional
	public String updateGroup(String objectIds, Long newGroupId,  Long userId) {
		if (objectIds == null || newGroupId == null || userId == null) {
			throw new NotFoundException("Null is not a valid Id");
		}
		
		long[] objects = Arrays.asList(objectIds.split(",")).stream().map(String::trim).mapToLong(Long::parseLong)
				.toArray();
		SpeciesGroup speciesGroup = speciesGroupService.findById(newGroupId);
		User user = userService.findById(userId);
		long i = 1;
		for(long objectId : objects){
			
			//SpeciesGroup speciesGroup = speciesGroupService.findById(newGroupId);
			Observation observation = show(objectId);

			if (speciesGroup == null || observation == null) {
				throw new NotFoundException("SpeciesGroup or observation is not found");
			}

			//User user = userService.findById(userId);

			String newSpeciesGroupName = speciesGroup.getName();
	//		SpeciesGroup oldSpeciesGroup = speciesGroupService.findById(oldGroupId);
			SpeciesGroup oldSpeciesGroup = observation.getGroup();
			String oldSpeciesGroupName = oldSpeciesGroup.getName();

			if(speciesGroup.getId() != oldSpeciesGroup.getId()){
				
				
				observationDao.updateGroup(observation, speciesGroup);
				

				// activityFeed
				Date dateCreated = new java.util.Date();
				Date lastUpdated = dateCreated;
				String activityDescription = oldSpeciesGroupName + " to " + newSpeciesGroupName;
				System.out.println(activityDescription);
				Map<String, Object> afNew = activityFeedService.createMapforAf("Object", objectId, observation,
						"species.participation.Observation", "species.participation.Observation", objectId,
						"Observation species group updated", "Species group updated", activityDescription, activityDescription,
						null, null, null, true, null, dateCreated, lastUpdated);
				activityFeedService.addActivityFeed(user, afNew, observation, (String) afNew.get("rootHolderType"));
				// activityFeed
				
				if (i % 50 == 0) {
					sessionFactory.getCurrentSession().flush();
					sessionFactory.getCurrentSession().clear();
				}

				i++;
			}
			
		}

		return "success";
	}

	@Transactional
	public Observation show(long id) {
		Observation obseravtion = observationDao.findById(id);
		return obseravtion;
	}

	@Transactional
	public Map<String, Object> getRecommendationVotes(String obvs,Long loggedInUserId,Boolean isAdmin,Boolean isSpeciesAdmin) {

		try {
			long[] obvIds = Arrays.asList(obvs.split(",")).stream().map(String::trim).mapToLong(Long::parseLong)
					.toArray();
			Integer noOfObvs = obvIds.length;
			Boolean singleObv = false;
			String query = "";
			if (noOfObvs == 1) {
				query = "select * from reco_vote_details rv where rv.observation_id =:obvId";
				singleObv = true;
			} else {
				query = "select * from reco_vote_details rv where rv.observation_id in (" + obvs
						+ ")  order by rv.observation_id";
			}

			List<Object[]> rv = observationDao.getRecommendationVotes(query, singleObv, obvIds[0], obvs);

			// return rv;
			List<Map<String, Object>> recoVotes = new ArrayList<Map<String, Object>>();

			Map<String, Object> obvListRecoVotesResult = new HashMap<String, Object>();

			for (Object[] obj : rv) {
				Map<String, Object> reco = new HashMap<String, Object>();
				reco.put("recoVoteId", ((java.math.BigInteger) obj[0]).longValue());
				reco.put("commonNameRecoId", obj[1]);
				reco.put("authorId", ((java.math.BigInteger) obj[2]).longValue());
				reco.put("votedOn", (Date) obj[3]);
				reco.put("comment", obj[4]);
				reco.put("originalAuthor", (String) obj[5]);
				reco.put("givenSciName", obj[6]);
				reco.put("givenCommonName", obj[7]);
				reco.put("recoId", ((java.math.BigInteger) obj[8]).longValue());
				reco.put("name", obj[9]);
				reco.put("isScientificName", obj[10]);
				reco.put("languageId", obj[11]);
				reco.put("taxonConceptId", obj[12]);
				reco.put("normalizedForm", obj[13]);
				reco.put("status", obj[14]);
				reco.put("speciesId", obj[15]);
				reco.put("observationId", ((java.math.BigInteger) obj[16]).longValue());
				reco.put("isLocked", obj[17]);
				reco.put("maxVotedRecoId", ((java.math.BigInteger) obj[18]).longValue());
				recoVotes.add(reco);
			}

			// trying

			Map<String, Object> recoMaps = new HashMap<String, Object>();
			for (Map<String, Object> recoVote : recoVotes) {
				// System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				// System.out.println(obvListRecoVotesResult.get("'"+recoVote.get("observationId")+"'"));
				Map<String, Object> obvRecoVotesResult = (Map<String, Object>) obvListRecoVotesResult
						.get(((Long) recoVote.get("observationId")).toString());
				if (obvRecoVotesResult == null) {
					System.out.println(recoVote.get("observationId"));
					System.out.println("obv not in obvlist till now");
					obvRecoVotesResult = new HashMap<String, Object>();
					obvRecoVotesResult.put("recoVotes", new ArrayList<Map<String, Object>>()); // checkin
																								// g%%%%%%%%%%%%%%%%%%%%%%%%%
					obvRecoVotesResult.put("totalVotes", 0);
					obvRecoVotesResult.put("uniqueVotes", 0);
					obvListRecoVotesResult.put(((Long) recoVote.get("observationId")).toString(), obvRecoVotesResult);
					recoMaps = new HashMap<String, Object>();
				}

				Map<String, Object> map = new HashMap<String, Object>();
				if (recoMaps.containsKey(recoVote.get("recoId").toString())) {
					System.out.println(" entry found in recoMaps");
					map = (Map<String, Object>) recoMaps.get(recoVote.get("recoId").toString());
				} else {
					map.put("recoId", recoVote.get("recoId"));
					map.put("isScientificName", recoVote.get("isScientificName"));

					if (recoVote.get("taxonConceptId") != null) {
						map.put("speciesId", recoVote.get("speciesId"));
						map.put("normalizedForm", recoVote.get("normalizedForm"));
						if (recoVote.get("status").equals("SYNONYM") && recoVote.get("isScientificName").equals(true)) {
							map.put("synonymOf", recoVote.get("normalizedForm"));
						}
					}
					map.put("name", recoVote.get("name"));

					recoMaps.put(recoVote.get("recoId").toString(), map);
					List<Map<String, Object>> array = (List<Map<String, Object>>) obvRecoVotesResult.get("recoVotes");
					array.add(map); // testing ^^^^^^^^^^^^^^^^6
					obvRecoVotesResult.put("recoVotes", array);
				}

				if (recoVote.get("commonNameRecoId") != null) {
					if (!map.containsKey("commonNames")) {
						map.put("commonNames", new HashMap<String, Object>());
					}
					Recommendation cnReco = recommendationService
							.findById(((java.math.BigInteger) recoVote.get("commonNameRecoId")).longValue());
					Long cnLangId = (cnReco.getLanguageId() != null) ? (cnReco.getLanguageId()) : (Long) 205L; /// what
																												/// is
																												/// englishId
																												/// hack
					if (!((Map<String, Object>) map.get("commonNames")).containsKey(cnLangId.toString())) {
						((Map<String, Object>) map.get("commonNames")).put(cnLangId.toString(),
								new HashSet<Recommendation>());
					}
					Set<Recommendation> abc = (Set<Recommendation>) ((Map<String, Object>) map.get("commonNames"))
							.get(cnLangId.toString());
					abc.add(cnReco);
					((Map<String, Object>) map.get("commonNames")).put(cnLangId.toString(), abc);
					// dont know

				}

				System.out.println(map.get("recoId"));
				if (!map.containsKey("authors")) {
					System.out.println("no author found");

					map.put("authors", new ArrayList<List<Object>>());
				}
				Map<String, Object> author = userService
						.findAuthorSignature(userService.findById((Long) recoVote.get("authorId")));
				// User originalAuthor = userService.findById((Long)
				// recoVote.get("originalAuthor"));
				List<List<Object>> userList = (List<List<Object>>) map.get("authors");
				List<Object> author_1 = new ArrayList<Object>();
				author_1.add(author);
				author_1.add(recoVote.get("originalAuthor"));
				userList.add(author_1);
				map.put("authors", userList);

				if (!map.containsKey("votedOn")) {
					map.put("votedOn", new ArrayList<String>());
				}
				List<String> voteDates = (List<String>) map.get("votedOn");
				voteDates.add(recoVote.get("votedOn").toString());
				map.put("votedOn", voteDates);

				if (!map.containsKey("noOfVotes")) {
					System.out.println("no no of votes");
					map.put("noOfVotes", 0);
				}
				int noOfVotes = ((int) map.get("noOfVotes")) + 1;
				map.put("noOfVotes", noOfVotes);

				if (recoVote.get("comment") != null) {
					if (!map.containsKey("recoComments")) {
						map.put("recoComments", "");
					}
					Map<String, Object> rc = new HashMap<String, Object>();
					rc.put("recoVoteId", recoVote.get("recoVoteId"));
					rc.put("comment", recoVote.get("comment"));
					rc.put("author", userService.findById((Long) recoVote.get("authorId")));
					rc.put("votedOn", recoVote.get("votedOn"));
					map.put("recoComments", rc);
				}

				map.put("obvId", recoVote.get("observationId"));
				map.put("isLocked", recoVote.get("isLocked"));

				if ((Boolean) recoVote.get("isLocked") == false) {
					map.put("showLock", true);
				} else {
					if (recoVote.get("recoId").equals(recoVote.get("maxVotedRecoId"))) {
						map.put("showLock", false);
					} else {
						map.put("showLock", true);
					}
				}

				map.put("hasObvLockPerm",
						hasObvLockPerm((Long) recoVote.get("observationId"), (Long) recoVote.get("recoId"),loggedInUserId,isAdmin,isSpeciesAdmin));
				Long totalCommentCount = commentService.getTotalRecoCommentCount((Long) map.get("recoId"),
						(Long) recoVote.get("observationId"));
				map.put("totalCommentCount", totalCommentCount);
			}

			for (Map.Entry<String, Object> entry : obvListRecoVotesResult.entrySet()) {
				
				int totalVotes = 0;
				Map<String, Object> obvRecoVotesResult = (Map<String, Object>) entry.getValue();
				System.out.println("testing recoVotes");
				System.out.println(obvRecoVotesResult.get("recoVotes"));
				List<Map<String, Object>> a = (List<Map<String, Object>>) obvRecoVotesResult.get("recoVotes");
				for (Map<String, Object> entry2 : a) {
					Map<String, Object> map = (Map<String, Object>) entry2; // checking..............................
					totalVotes += (int) map.get("noOfVotes");
					if (map.get("commonNames") != null) {

						// to be written
						String cNames = getFormattedCommonNames((Map<String, Object>) map.get("commonNames"), true);
						map.put("commonNames", (cNames.equals("")) ? "" : "(" + cNames + ")");
					}

					for (List<Object> authors_1 : (List<List<Object>>) map.get("authors")) {
//						 if(loggedInUserId != null){ // what is currentUser
//						 if((Map)authors_1.get(0).get("id") == loggedInUserId){
//						 map.put("disAgree", true);
//						 }
//						 }
					}
				}
				obvRecoVotesResult.put("totalVotes", totalVotes);
				obvRecoVotesResult.put("uniqueVotes",
						((List<Map<String, Object>>) obvRecoVotesResult.get("recoVotes")).size());

				List<Map<String, Object>> finalRecos = (List<Map<String, Object>>) obvRecoVotesResult.get("recoVotes");
				Collections.sort(finalRecos, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> map1, Map<String, Object> map2) {
						if ((Integer) map1.get("noOfVotes") > (Integer) map2.get("noOfVotes")) {
							return -1;
						} else if ((Integer) map1.get("noOfVotes") < (Integer) map2.get("noOfVotes")) {
							return +1;
						} else {
							return 0;
						}
					}
				});
				obvRecoVotesResult.put("recoVotes", finalRecos); // have to sort
																	// later
			}
			for (Long obvId : obvIds) {
				if (obvListRecoVotesResult.get(obvId.toString()) == null) {
					System.out.println("id not found");
					Map<String, Object> a = new HashMap<String, Object>();
					a.put("recoVotes", new ArrayList());
					a.put("totalVotes", 0);
					a.put("uniqueVotes", 0);
					obvListRecoVotesResult.put(obvId.toString(), a);
				}
			}
			return obvListRecoVotesResult;
		} catch (Exception e) {
			throw e;
		} finally {
			//
		}
	}

	private Boolean hasObvLockPerm(Long obvId, Long recoId,Long loggedInUserId,Boolean isAdmin,Boolean isSpeciesAdmin) {
		if(loggedInUserId != null){
			Taxon taxon = (recommendationService.findById(recoId) != null)
					? recommendationService.findById(recoId).getTaxonConcept() : null;
			User currentUser = userService.findById(loggedInUserId);
			PermissionType[] allPerm = {SpeciesPermission.PermissionType.ROLE_CONTRIBUTOR, SpeciesPermission.PermissionType.ROLE_CURATOR,
					 SpeciesPermission.PermissionType.ROLE_TAXON_CURATOR, SpeciesPermission.PermissionType.ROLE_TAXON_EDITOR};
			return (loggedInUserId != null && 
					(		isAdmin
							|| isSpeciesAdmin 
							|| (taxon != null && speciesPermissionService.isTaxonContributor(taxon,currentUser,Arrays.asList(allPerm)) )
					)
					);
		}else{
			 return false;
		}
		
				
	}

	private String getFormattedCommonNames(Map<String, Object> langToCommonName, Boolean addLanguage) {
		if (langToCommonName.isEmpty()) {
			return "";
		}
		System.out.println("langToCommonName " + langToCommonName);
		Long englishId = 205L; // dont know
		// System.out.println("remove
		// "+langToCommonName.remove(englishId.toString()));
		Set<Recommendation> englishNames = (Set<Recommendation>) langToCommonName.remove(englishId.toString());

		List<String> cnList = new ArrayList<String>();

		for (String key : langToCommonName.keySet()) {
			// System.out.println(langToCommonName.get(key));
			String langSuffix = null;
			Set<Recommendation> langInstance = (Set<Recommendation>) langToCommonName.get(key);
			for (Recommendation reco : langInstance) {
				langSuffix = reco.getName() + ",";
			}
			System.out.println("langSuffix " + langSuffix);
			if (langSuffix.endsWith(",")) {
				langSuffix = langSuffix.substring(0, langSuffix.length() - 1);
			}
			System.out.println("langSuffix " + langSuffix);
			if (addLanguage == true) {
				langSuffix = languageService.findById(Long.parseLong(key)).getName() + ": " + langSuffix;
			}
			cnList.add(langSuffix);
		}

		String engNamesString = null;

		if (englishNames != null) {
			if (!englishNames.isEmpty()) {
				for (Recommendation reco : englishNames) {
					engNamesString = reco.getName() + ",";
				}
				System.out.println("engNamesString " + engNamesString);
				if (engNamesString.endsWith(",")) {
					engNamesString = engNamesString.substring(0, engNamesString.length() - 1);
				}
				if (addLanguage == true) {
					engNamesString = languageService.findById(205L).getName() + ": " + engNamesString;
				}
			}
		}

		if (engNamesString != null) {
			cnList.add(0, engNamesString);
		}
		String cNames = String.join(",", cnList);
		return cNames;
	}

	public List<CustomField> getAllCustomFieldsByUserGroup(Long uid) {
		// TODO Auto-generated method stub
		System.out.println(uid);
		UserGroup usGp=userGroupService.findById(uid);
		List<CustomField> cf=customFieldService.fetchCustomFieldsByGroup(usGp);
		return cf;
	}

	public List<CustomField> getAllCustomFields() {
		// TODO Auto-generated method stub
		List<CustomField> cf=customFieldService.fetchAllCustomFields();
		
		return cf;
	}
	
	@Transactional
	public String addRecommendationVote(String obvIds, String commonName, String languageName, String recoName,
			Long recoId, String recoComment, long authorId) {
		
		try{
			long[] obvs = Arrays.asList(obvIds.split(",")).stream().map(String::trim).mapToLong(Long::parseLong)
					.toArray();
			User author = userService.findById(authorId);
			String msg = "success";
			if(obvs.length>0){
				long i = 1;
				for(long obvId : obvs){
					Boolean canMakeSpeciesCall = true;
					Map<String,Object> recoVoteResult = null;
					RecommendationVote recommendationVoteInstance = null;
					if(canMakeSpeciesCall){
						recoVoteResult = getRecommendationVote(obvId,author,recoId,recoName,commonName,languageName);
						if(recoVoteResult != null){
							recommendationVoteInstance = (RecommendationVote) recoVoteResult.get("recoVote");
							System.out.println("recoVote got ");
							System.out.println("recoVote got ");
							System.out.println("recoVote got ");
							System.out.println("recoVote got "+recommendationVoteInstance);
							if(recommendationVoteInstance!=null){
								System.out.println(recommendationVoteInstance.getId());
							}
						}
					}
					
					if(recommendationVoteInstance != null){
						Observation obv = findById(obvId);
						//save(recommendationVoteInstance)
						recommendationVoteService.save(recommendationVoteInstance);
						//save
						//calculate maxVotedSpeciesName
						Map<String,Object> maxVotedMap = calculateMaxVotedSpeciesName(obv);
						Long maxVotedRecoId = (Long) maxVotedMap.get("reco");
						
						Recommendation maxVotedReco = recommendationService.findById(maxVotedRecoId);
						if(maxVotedReco !=null){
							obv.setMaxVotedReco(maxVotedReco);
							obv.setNoOfIdentifications((int) maxVotedMap.get("noOfIdentifications"));
						}
						
						Boolean updateChecklistAnnotations = false;
						if(maxVotedMap.containsKey("haveToUpdateChecklistAnnotation")){
							updateChecklistAnnotations = (Boolean) maxVotedMap.get("haveToUpdateChecklistAnnotation");
						}
						
						if(updateChecklistAnnotations == true){
							String checkAnno =  funcUpdateChecklistAnnotations((RecommendationVote) maxVotedMap.get("recoVoteForChecklist"));
							obv.setChecklistAnnotations(checkAnno);
						}
						//have to save obv
						save(obv);
						
						//elastic
//						Long taxonId=maxVotedReco.getTaxonConcept().getId();
//						TaxonomyRegistry taxonomyRegistry=taxonService.getTaxonRegistryWithTaxonConceptId(taxonId);
//
//						String observationmName=maxVotedReco.getTaxonConcept().getNormalizedForm();
//						String lastrevised=obv.getLastRevised().toString();
//						String maxvotedrecoid=obv.getMaxVotedReco().toString();
//						String noofidentifications=String.valueOf(obv.getNoOfIdentifications());
//						String taxonconceptid=String.valueOf(maxVotedReco.getTaxonConcept().getId());
//						String acceptednameid=String.valueOf(maxVotedReco.getAcceptedName().getId());
//						String taxonomycanonicalform=maxVotedReco.getTaxonConcept().getCanonicalForm();
//						String status=maxVotedReco.getTaxonConcept().getStatus();
//						String position=maxVotedReco.getTaxonConcept().getPosition();
//						String rank=String.valueOf(maxVotedReco.getTaxonConcept().getRank());
//						String path=taxonomyRegistry.getPath();
//						String classificationid=String.valueOf(taxonomyRegistry.getClassificationId());
//						
//						JSONObject obj = new JSONObject();
//						
//						
//						obj.put("name", observationmName);
//						obj.put("lastrevised", lastrevised);
//						obj.put("maxvotedrecoid", maxvotedrecoid);
//						obj.put("noofidentifications", noofidentifications);
//						obj.put("taxonconceptid", taxonconceptid);
//						obj.put("acceptednameid", acceptednameid);
//						obj.put("taxonomycanonicalform", taxonomycanonicalform);
//						obj.put("status", status);
//						obj.put("position", position);
//						obj.put("rank", rank);
//						obj.put("path", path);
//						obj.put("classificationid", classificationid);
//						observationListService.update("observation", "observation",obv.getId().toString(),obj.toString());

						
						//elastic
						
						
						
						//update species timeStamp
						//recommendationVoteService.updateSpeciesTimeStamp(recommendationVoteInstance);
						
						//addComment
						recommendationVoteService.addRecoComment(null,recoComment,null,recommendationVoteInstance.getRecommendationByRecommendationId().getId(),
								"species.participation.Recommendation",obvId,"species.participation.Observation",null,null,null,null,new Date().getTime(),
								"/observation/addRecommendationVote","en",authorId);
						
						//activityFeed
						
						Date dateCreated = new java.util.Date();
						Date lastUpdated = dateCreated;
						String name =  recommendationVoteInstance.getGivenSciName()!=null?recommendationVoteInstance.getGivenSciName():
							recommendationVoteInstance.getRecommendationByRecommendationId().getName();
						Long ro_id = recommendationVoteInstance.getRecommendationByRecommendationId().getTaxonConcept()!=null? recommendationVoteInstance.getRecommendationByRecommendationId().getTaxonConcept().getId():
							null;
						Boolean isScientificName = recommendationVoteInstance.getRecommendationByRecommendationId().getIsScientificName();
						Map<String, Object> afNew = activityFeedService.createMapforAf("Object", obv.getId(), obv,
								"species.participation.Observation", "species.participation.RecommendationVote", recommendationVoteInstance.getId(),
								"Suggested species name", "Suggested species name", null, null,
								name, "species", isScientificName, true, ro_id, dateCreated, lastUpdated);
						activityFeedService.addActivityFeed(author, afNew, obv, (String) afNew.get("rootHolderType"));
						//activityFeed
					}else{
						msg = "parsing failed";
					}
					
					if (i % 50 == 0) {
						sessionFactory.getCurrentSession().flush();
						sessionFactory.getCurrentSession().clear();
					}

					i++;
				}
				
				return msg;
			}else{
				throw new NotFoundException("No observation id provided");
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		
	}

	@Transactional
	private String funcUpdateChecklistAnnotations(RecommendationVote recoVote) {
		Observation obv = recoVote.getObservation();
		org.json.JSONObject m = fetchChecklistAnnotation(obv);
		
		if(m ==null){
			return null;
		}
		
		
		if(obv.isChecklist() && (obv.getSourceId()!=null) && (obv.getDataset()==null)){
			Checklists cl = checklistsService.findById(obv.getSourceId());
			
			if(cl.getSciNameColumn() != null && recoVote.getRecommendationByRecommendationId().getIsScientificName()){
				m.put(cl.getSciNameColumn(), recoVote.getRecommendationByRecommendationId().getName());
			}
			
			if(cl.getCommonNameColumn() != null && recoVote.getRecommendationByCommonNameRecoId() !=null){
				m.put(cl.getCommonNameColumn(), recoVote.getRecommendationByCommonNameRecoId().getName());
			}
			//convert m to JSON String 
			return m.toString();
		}
		return null;
		
	}
	
	@Transactional
	private org.json.JSONObject fetchChecklistAnnotation(Observation obv) {
		org.json.JSONObject m = new org.json.JSONObject();
		Checklists cl = checklistsService.findById(obv.getSourceId());
		if(cl !=null && obv.getChecklistAnnotations() != null && obv.isChecklist()){
			//JSONObject checklistsJson = new JSONObject(obv.getChecklistAnnotations());
			//JSONTokener tokener = new JSONTokener(obv.getChecklistAnnotations());
			org.json.JSONObject root = new org.json.JSONObject(obv.getChecklistAnnotations());
			List<String> ls = checklistsService.fetchColumnNames(cl);
			for(String name : ls){
				m.put(name, root.get(name));
			}
		}else if(obv.getDataTable()!=null){
			if(obv.getChecklistAnnotations()!=null){
				org.json.JSONObject root = new org.json.JSONObject(obv.getChecklistAnnotations());
				List<List<String>> ls = dataTableService.fetchColumnNames(obv.getDataTable());
				for(List<String> name : ls){
					m.put(name.get(1), root.get(name.get(1)));	
				}
			}		
		}
		m.put("id", obv.getId());
		m.put("type", "observation");
		
		if(obv.getMaxVotedReco()!=null && obv.getMaxVotedReco().getTaxonConcept()!=null){
			m.put("speciesId", obv.getMaxVotedReco().getTaxonConcept());//have to do
			m.put("title",obv.getMaxVotedReco().getTaxonConcept().getCanonicalForm());
		}else if(obv.getMaxVotedReco()!=null){
			m.put("title", obv.getMaxVotedReco().getName());
		}
			
		return m;
		
	}

	@Transactional
	private Map<String,Object> calculateMaxVotedSpeciesName(Observation obv) {
		Map<String,Object> maxVotedMap = observationDao.calculateMaxVotedSpeciesName(obv);
		return maxVotedMap;
		
	}

	@Transactional
	private Map<String, Object> getRecommendationVote(long obvId, User author, Long recoId, String recoName,
			String commonName, String languageName) {
		
		Date dateCreated = new Date();
		Map<String,Object> toReturn = new HashMap<String,Object>();
		ConfidenceType confidence = ConfidenceType.CERTAIN;
		Observation obv = findById(obvId);
		RecommendationVote existingRecVote = recommendationVoteService.findByAuthorAndObservation(author,obv);
		Recommendation reco = null;
		Recommendation commonNameReco = null;
		Boolean isAgreeRecommendation = false;
		
		if(recoId!=null && !(recoName != null || commonName !=null)){
			  //user presses on agree button so getting reco from id and creating new recoVote without additional common name
			reco = recommendationService.findById(recoId);
			isAgreeRecommendation = true;
		}else{
			  //add recommendation used so taking both reco and common name reco if available
			Map<String,Object> recoResultMap = getRecommendations(recoId, recoName, commonName, languageName);
			reco = (Recommendation) recoResultMap.get("mainReco");
			commonNameReco =  (Recommendation) recoResultMap.get("commonNameReco");
		}
		//if parsing fails
		RecommendationVote newRecoVote = null;
		if(reco !=null || commonNameReco!=null){
			 newRecoVote = new RecommendationVote(obv,reco,commonNameReco,author,confidence,recoName,commonName,dateCreated);
		}
		//if parsing fails
		
		System.out.println(" &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		System.out.println(" &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		System.out.println(" &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		System.out.println(" &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		if(reco==null){
			System.out.println(" reco is null");
			System.out.println(" reco is null");
			System.out.println(" reco is null");
			System.out.println(" reco is null");
			return null;
		}else{
			System.out.println("reco is not null");
			System.out.println("reco is not null");
			System.out.println("reco is not null");
			System.out.println("reco is not null");
			if(existingRecVote==null){
				System.out.println("exixsting reco  null");

				System.out.println("exixsting reco  null");

				System.out.println("exixsting reco  null");

				System.out.println("exixsting reco  null");
				toReturn.put("recoVote", newRecoVote);
				return toReturn;
			}else{
				System.out.println("exixtingreco is not null");
				System.out.println("exixtingreco is not null");
				System.out.println("exixtingreco is not null");
				System.out.println("exixtingreco is not null");
				System.out.println(reco.getId());
				System.out.println(existingRecVote.getRecommendationByRecommendationId().getId());
				if(existingRecVote.getRecommendationByRecommendationId().getId() == reco.getId()){
					//if old recommendation is same as new recommendation then

					System.out.println("if old recommendation is same as new recommendation then");
					System.out.println("if old recommendation is same as new recommendation then");
					System.out.println("if old recommendation is same as new recommendation then");
					System.out.println("if old recommendation is same as new recommendation then");
					if(!isAgreeRecommendation && !existingRecVote.getRecommendationByCommonNameRecoId().equals(commonNameReco)){
						//user might want to update(add new common name or delete existing one) the common name
						System.out.println("if old recommendation is same but new common name");
						System.out.println("if old recommendation is same but new common name");
						System.out.println("if old recommendation is same but new common name");
						System.out.println("if old recommendation is same but new common name");
						existingRecVote.setRecommendationByCommonNameRecoId(commonNameReco);
						toReturn.put("recoVote", existingRecVote);
						return toReturn;
					}
					// when same sciName & same commonName
					System.out.println("same sci Name and common Name");
					System.out.println("same sci Name and common Name");
					System.out.println("same sci Name and common Name");
					System.out.println("same sci Name and common Name");
					System.out.println("same sci Name and common Name");
					 return null;
				}else{
					System.out.println("both votes id not same");
					System.out.println("dont know");
					System.out.println("dont know");
					System.out.println("dont know");
					//recommendationVoteService.removeFromRecommendationVote(existingRecVote);
					//delete existing vote
					recommendationVoteService.delete(existingRecVote.getId());
					sessionFactory.getCurrentSession().flush();
					sessionFactory.getCurrentSession().clear();
					toReturn.put("recoVote", newRecoVote);
					return toReturn;
				}
			}
		}
	}

	private ConfidenceType getConfidenceType(String confidenceType) {
		if(confidenceType==null) return null;
//		for(ConfidenceType type ){
//			
//		}
		return null;
	}
	
	@Transactional
	private Map<String, Object> getRecommendations(Long recoId, String recoName, String commonName,
			String languageName) {
		
		try{
			Recommendation commonNameReco = null;
			Recommendation scientificNameReco = null;
			Language lang = languageService.findByName(languageName); //languageService.findById(205L);
			Map<String,Object> toReturn = new HashMap<String,Object>();
			
			//if selected from dropdown then recoId is there
			if(recoId!=null){
				Recommendation r = recommendationService.findById(recoId);
				if(r.getIsScientificName()){
					scientificNameReco = r;
					if(commonName!=null){
						commonNameReco = recommendationService.findReco(commonName, false, lang.getId(), scientificNameReco.getTaxonConcept(), true, false);
					}
				}else{
					if(commonName!=null){
						if(commonName.toLowerCase().equals(r.getLowercaseName())){
							commonNameReco = r;
						}else{
							commonNameReco = recommendationService.findReco(commonName, false, lang.getId(), r.getTaxonConcept(), true, false);
						}
					}else{
						commonNameReco = null;
					}
					
					if(r.getTaxonConcept()!= null){
						scientificNameReco = recommendationService.findReco(r.getTaxonConcept().getCanonicalForm(), true, null, r.getTaxonConcept(), true, false);
						System.out.println("scientificName reco id "+scientificNameReco.getId());
						System.out.println("scientificName reco id "+scientificNameReco.getId());
						System.out.println("scientificName reco id "+scientificNameReco.getId());
						System.out.println("scientificName reco id "+scientificNameReco.getId());
					}
				}
				
				toReturn.put("mainReco", scientificNameReco!=null?scientificNameReco:commonNameReco);
				toReturn.put("commonNameReco", commonNameReco);
				
				return toReturn;
			}
			
			//not selected from dropdown
			
			if(commonName!=null){
				commonNameReco = recommendationService.findReco(commonName, false, lang.getId(), null, true, false);
			}
			scientificNameReco = recommendationService.findReco(recoName, true, null, null, true, false);
			toReturn.put("mainReco", scientificNameReco!=null?scientificNameReco:commonNameReco);
			toReturn.put("commonNameReco", commonNameReco);
			return toReturn;
			
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		
	}

	@Transactional
	public List<User> findWhoLiked(long obvId) {
		List<User> userList = ratingLinkService.findWhoLiked("observation",obvId);
		return userList;
	}
	
	
	
	

}