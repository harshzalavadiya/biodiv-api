package biodiv.userGroup;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.activityFeed.ActivityFeedService;
import biodiv.common.AbstractService;
import biodiv.common.DataObject;
import biodiv.user.Role;
import biodiv.user.RoleService;
import biodiv.user.User;
import biodiv.user.UserService;
import biodiv.util.HibernateUtil;

public class UserGroupService extends AbstractService<UserGroup> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private RoleService roleService;

	@Inject
	UserService userService;

	@Inject
	ActivityFeedService activityFeedService;

	private UserGroupDao userGroupDao;

	@Inject
	UserGroupService(UserGroupDao userGroupDao) {
		super(userGroupDao);
		this.userGroupDao = userGroupDao;
		log.trace("UserGroupService constructor");
	}

	@Transactional
	public List<UserGroup> userUserGroups(long userId) {

		// boolean localTransaction = false;
		try {
			// if((HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().getStatus())
			// != TransactionStatus.ACTIVE)
			// {
			// HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
			// localTransaction = true;
			// }
			List<UserGroup> usrGrp = userGroupDao.userUserGroups(userId);
			// if(localTransaction == true)
			// {
			// HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
			// }
			return usrGrp;
		} catch (Exception e) {
			// if(localTransaction == true)
			// {
			// HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
			// }
			throw e;
		} finally {

		}
	}

	@Transactional
	public List<User> userList(long groupId, long roleId) {
		try {
			//userGroupDao.openCurrentSession();
			List<User> usr = userGroupDao.userList(groupId, roleId);
			return usr;
		} catch (Exception e) {
			throw e;
		} finally {
			//userGroupDao.closeCurrentSession();
		}
	}

	@Transactional
	public String posttoGroups(String objectType, String pullType, String submitType, String objectIds,
			String userGroups, long userId, String filterUrl) throws Exception {

		// System.out.println("beforeeeeeeeeeeeeeeeeeeeeee :"+
		// System.identityHashCode(HibernateUtil.getSessionFactory().getCurrentSession()));
		// StatelessSession session = null;
		// Transaction tx = null;
		try {
			// session =
			// HibernateUtil.getSessionFactory().openStatelessSession();
			// tx = session.beginTransaction();
			// System.out.println("afterrrrrrrrrrrrrrrr1 :"+
			// System.identityHashCode(session));
			// System.out.println("afterrrrrrrrrrrrrrrr2 :"+
			// System.identityHashCode(HibernateUtil.getSessionFactory().getCurrentSession()));
			long[] objects = Arrays.asList(objectIds.split(",")).stream().map(String::trim).mapToLong(Long::parseLong)
					.toArray();

			// List<Long> list =
			// Arrays.stream(objects).boxed().collect(Collectors.toList());

			// System.out.println("abc :" +
			// Arrays.asList(objectIds.split(",")));

			// List<List<String>> l =
			// UserGroup.collate(Arrays.asList(objectIds.split(",")), 3, 3);
			// System.out.println(l);
			// System.out.println(l.get(1));
			// List<String> s = l.get(1);
			// System.out.println(s);
			// System.out.println(s.stream().map(String::trim).mapToLong(Long::parseLong).toArray());
			// long[] abc =
			// s.stream().map(String::trim).mapToLong(Long::parseLong).toArray();
			// System.out.println(abc[0]);

			// System.out.println(UserGroup.splitBytes(objects,3));
			Date dateCreated = new Date();
			Date lastUpdated = dateCreated;

			User user = userService.findById((Long) userId);
			User admin = userService.findById((long) 1);
			List<UserGroup> allowedUsrGrps = userUserGroups(userId);
			Set<UserGroup> allowed = new HashSet<UserGroup>(allowedUsrGrps);
			Set<UserGroup> userGroupsWithFilterRule = findAllByFilterRuleIsNotNull();

			long i = 1;

			Map<Long, Long> groupFeed_ByUser = new HashMap<Long, Long>();
			Map<Long, Long> groupFeed_ByAdmin = new HashMap<Long, Long>();
			DataObject typeOfObject = null;
			for (long object : objects) {

				Class<?> clazz = Class.forName(objectType);
				DataObject _obj = (DataObject) clazz.newInstance();
				System.out.println("bbbbnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
				DataObject dataObj = _obj.get(object);
				System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
				typeOfObject = dataObj;
				Set<UserGroup> obvUsrGrps = dataObj.getUserGroups();

				Set<UserGroup> userGroupsContainingObv = UserGroup.findAllContainingObj(objectType, (Object) dataObj,
						userGroupsWithFilterRule);

				Set<UserGroup> updatedObjUsrGrps = userGroupDao.posttoGroups(objectType, dataObj, allowed,
						userGroupsContainingObv, obvUsrGrps, pullType, submitType, userGroups, filterUrl);

				dataObj.setUserGroups(updatedObjUsrGrps);
				dataObj.save();

				// activityFeed addition starts here for Object Entry

				String activityDescription = UserGroup.getActivityObjectType((Object) dataObj, submitType, "Object",
						(long) 0);
				Set<UserGroup> newAddedOrRemovedUsrGrps = new HashSet<>(updatedObjUsrGrps);

				if (submitType.equalsIgnoreCase("post")) {
					newAddedOrRemovedUsrGrps.removeAll(obvUsrGrps);
					Set<UserGroup> newAddedUsrGrpsByAdmin = new HashSet<>(newAddedOrRemovedUsrGrps);
					newAddedUsrGrpsByAdmin.retainAll(userGroupsContainingObv);
					Set<UserGroup> newAddedUsrGrpsByUsr = new HashSet<>(newAddedOrRemovedUsrGrps);
					newAddedUsrGrpsByUsr.removeAll(newAddedUsrGrpsByAdmin);

					for (UserGroup ug : newAddedUsrGrpsByUsr) {
						long ugId = ug.getId();
						String name = ug.getName();
						Map<String, Object> afNew = activityFeedService.createMapforAf("Object", object, dataObj, null,
								"species.groups.UserGroup", ugId, "Posted resource", activityDescription,
								activityDescription, null, name, "UserGroup",null, true, null, dateCreated, lastUpdated);
						activityFeedService.addActivityFeed(user, afNew, dataObj, null);
						if (pullType.equalsIgnoreCase("bulk")) {
							groupFeed_ByUser.merge(ugId, (long) 1, Long::sum);
						}

					}

					for (UserGroup ug : newAddedUsrGrpsByAdmin) {
						long ugId = ug.getId();
						String name = ug.getName();
						Map<String, Object> afNew = activityFeedService.createMapforAf("Object", object, dataObj, null,
								"species.groups.UserGroup", ugId, "Posted resource", activityDescription,
								activityDescription, null, name, "UserGroup",null, true, null, dateCreated, lastUpdated);
						activityFeedService.addActivityFeed(admin, afNew, dataObj, null);
						if (pullType.equalsIgnoreCase("bulk")) {
							groupFeed_ByAdmin.merge(ugId, (long) 1, Long::sum);
						}
					}
				} else {
					Set<UserGroup> previousUsrGrps = new HashSet<>(obvUsrGrps);
					previousUsrGrps.removeAll(newAddedOrRemovedUsrGrps);
					for (UserGroup ug : previousUsrGrps) {
						long ugId = ug.getId();
						String name = ug.getName();
						Map<String, Object> afNew = activityFeedService.createMapforAf("Object", object, dataObj, null,
								"species.groups.UserGroup", ugId, "Removed resoruce", activityDescription,
								activityDescription, null, name, "UserGroup", null,true, null, dateCreated, lastUpdated);
						activityFeedService.addActivityFeed(user, afNew, dataObj, null);
						if (pullType.equalsIgnoreCase("bulk")) {
							groupFeed_ByUser.merge(ugId, (long) 1, Long::sum);
						}
					}
				}
				// activityFeed addition ends here for Object Entry

				if (i % 50 == 0) {
					HibernateUtil.getSessionFactory().getCurrentSession().flush();
					HibernateUtil.getSessionFactory().getCurrentSession().clear();
				}

				i++;
			}

			// activityFeed for UserGroup entry starts here
			if (pullType.equalsIgnoreCase("bulk")) {
				if (submitType.equalsIgnoreCase("post")) {
					for (Long ugId : groupFeed_ByUser.keySet()) {
						UserGroup ug = findById(ugId);
						String name = ug.getName();
						long countOfObjs = groupFeed_ByUser.get(ugId);
						String description = UserGroup.getActivityObjectType((Object) typeOfObject, submitType,
								"UserGroup", countOfObjs);
						Map<String, Object> afNew = activityFeedService.createMapforAf("UserGroup", ugId, typeOfObject,
								null, "species.groups.UserGroup", ugId, "Posted resource", description, description,
								null, name, "UserGroup", null,true, null, dateCreated, lastUpdated);
						activityFeedService.addActivityFeed(user, afNew, null, null);
					}

					for (Long ugId : groupFeed_ByAdmin.keySet()) {
						UserGroup ug = findById(ugId);
						String name = ug.getName();
						long countOfObjs = groupFeed_ByAdmin.get(ugId);
						String description = UserGroup.getActivityObjectType((Object) typeOfObject, submitType,
								"UserGroup", countOfObjs);
						Map<String, Object> afNew = activityFeedService.createMapforAf("UserGroup", ugId, typeOfObject,
								null, "species.groups.UserGroup", ugId, "Posted resource", description, description,
								null, name, "UserGroup", null,true, null, dateCreated, lastUpdated);
						activityFeedService.addActivityFeed(admin, afNew, null, null);
					}
				} else {
					for (Long ugId : groupFeed_ByUser.keySet()) {
						UserGroup ug = findById(ugId);
						String name = ug.getName();
						long countOfObjs = groupFeed_ByUser.get(ugId);
						String description = UserGroup.getActivityObjectType((Object) typeOfObject, submitType,
								"UserGroup", countOfObjs);
						Map<String, Object> afNew = activityFeedService.createMapforAf("UserGroup", ugId, typeOfObject,
								null, "species.groups.UserGroup", ugId, "Removed resoruce", description, description,
								null, name, "UserGroup",null, true, null, dateCreated, lastUpdated);
						activityFeedService.addActivityFeed(user, afNew, null, null);
					}

				}
			}
			// activityFeed for UserGroup entry ends here

			// Follwer code

			return "success";
		} catch (Exception e) {
			throw e;
		} finally {
			// tx.commit();
			// session.close();
		}

	}

	public Set<UserGroup> findAllByFilterRuleIsNotNull() throws Exception {

		try {
			List<UserGroup> userGroupsHavingFilterRule = userGroupDao.findAllByFilterRuleIsNotNull();
			Set<UserGroup> userGroupsWithFilterRule = new HashSet<UserGroup>(userGroupsHavingFilterRule);
			return userGroupsWithFilterRule;
		} catch (Exception e) {
			throw e;
		} finally {

		}

	}

	public long[] findObjectIdsByFilterUrl(Map<String, String> filterUrlMap) {
		try {
			long[] objectIds = userGroupDao.findObjectIdsByFilterRule(filterUrlMap);
			return objectIds;
		} catch (Exception e) {
			throw e;
		} finally {

		}
	}

	public UserGroup findByName(String name) {
		// TODO Auto-generated method stub
		name = name.trim();
		UserGroup userGroup = userGroupDao.findByName(name);
		return userGroup;
	}

	public Boolean isFounder(UserGroup ug, Long userId) {

		try {
			Role role = roleService.findRoleByAuthority("ROLE_USERGROUP_FOUNDER");
			Boolean founder = userGroupDao.isFounder(ug.getId(), userId, role.getId());
			return founder;
		} catch (Exception e) {
			throw e;
		} finally {

		}
	}
}