package biodiv.userGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface UserGroupModel {

	public Set<UserGroup> getUserGroups();
	
	public void setUserGroups(Set<UserGroup> userGroups);
}
