package biodiv.comment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.configuration2.Configuration;

import biodiv.observation.Observation;
import biodiv.observation.ObservationMailClass;
import biodiv.observation.ObservationService;
import biodiv.user.User;
import biodiv.userGroup.UserGroup;

public class CommentPostMailDataModel {

	@Inject
	ObservationService observationService;

	private Map<String, Object> root;

	public CommentPostMailDataModel(User followerr, User postingUser, Observation obv,
			String commentBody,Configuration config) {
		root = new HashMap<>();
		User follower = new User();
		follower.setId(followerr.getId());
		follower.setName(followerr.getName());

		User whoPosted = new User();
		whoPosted.setId(postingUser.getId());
		if (followerr.getEmail().equalsIgnoreCase(postingUser.getEmail())) {
			whoPosted.setName("You");
		} else {
			whoPosted.setName(postingUser.getName());
		}

		if (postingUser.getIcon() == null) {
			System.out.println("prfilepic of user " + postingUser.getProfilePic());
			if (postingUser.getProfilePic() == null) {
				whoPosted.setIcon("user.png");
			} else {
				whoPosted.setIcon(postingUser.getProfilePic());
			}
		} else {
			System.out.println("icon of user " + postingUser.getIcon());
			whoPosted.setIcon(postingUser.getIcon());
		}


		// List l = new List(userGroupsToBePosted);

		ObservationMailClass obm = new ObservationMailClass();
		obm.setId(obv.getId());
		if (obv.getMaxVotedReco() != null) {
			if (obv.getMaxVotedReco().getTaxonConcept() != null) {
				if (obv.getMaxVotedReco().getTaxonConcept().getNormalizedForm() != null) {
					obm.setName(obv.getMaxVotedReco().getTaxonConcept().getNormalizedForm());
				} else {
					obm.setName(obv.getMaxVotedReco().getName());
				}
			} else {
				obm.setName(obv.getMaxVotedReco().getName());
			}
		} else {
			obm.setName("Help Identify");
		}

		//obm.setCommonName("Help Identify");
		obm.setLocation(obv.getPlaceName());
		obm.setUserGroups(obv.getUserGroups());
		obm.setObservedOn(obv.getFromDate());
		if (obv.getReprImage() == null) {
			String group = obv.getGroup().getName().toLowerCase();

			System.out.println("no image for obv");
			obm.setIcon(config.getString("serverUrl")+"/biodiv/group_icons/speciesGroups/" + group + "_th1.png");
		} else {
			String[] file = obv.getReprImage().getFileName().split("\\.");
			System.out.println(file);
			System.out.println(file[0] + "_th1.jpg");
			obm.setIcon(config.getString("serverUrl")+"/biodiv/observations/" + file[0] + "_th1.jpg");
		}

		root.put("follower", follower);
		root.put("whoPosted", whoPosted);
		root.put("whatPosted", obm);
		root.put("commentBody", commentBody);
		root.put("serverUrl", config.getString("serverUrl"));
		root.put("siteName", config.getString("siteName"));
		// root.put("whatPostedUserGroups", whatPostedUserGroups);
	}
	
	public Map<String, Object> getRoot() {
		return this.root;
	}
}
