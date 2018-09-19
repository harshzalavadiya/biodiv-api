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

public class TaggedMailDataModel {

	@Inject
	ObservationService observationService;

	private Map<String, Object> root;

	public TaggedMailDataModel(User tagged, User whoTaggedThem, Observation obv,String commentBody,Configuration config) {
		root = new HashMap<>();
		User whoWasTagged = new User();
		whoWasTagged.setName(tagged.getName());

		User whoTagged = new User();
		whoTagged.setId(whoTaggedThem.getId());
		whoTagged.setName(whoTaggedThem.getName());
	

		if (whoTaggedThem.getIcon() == null) {
			if (whoTaggedThem.getProfilePic() == null) {
				whoTagged.setIcon("user.png");
			} else {
				whoTagged.setIcon(whoTaggedThem.getProfilePic());
			}
		} else {
			whoTagged.setIcon(whoTaggedThem.getIcon());
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

		// Set<UserGroup> obvUserGroups = obv.getUserGroups();
		// Set<UserGroup> whatPostedUserGroups = new HashSet<UserGroup>();
		//
		// for(UserGroup ug : obvUserGroups){
		// UserGroup u = new UserGroup();
		// u.setId(ug.getId());
		// u.setName(ug.getName());
		// u.setWebaddress(ug.getWebaddress());
		// whatPostedUserGroups.add(u);
		// }

		root.put("follower", whoWasTagged);
		root.put("whoPosted", whoTagged);
		root.put("whatPosted", obm);
		root.put("commentBody", commentBody);
		root.put("serverUrl", config.getString("serverUrl"));
		root.put("siteName", config.getString("siteName"));
	}

	public Map<String, Object> getRoot() {
		return this.root;
	}
}

