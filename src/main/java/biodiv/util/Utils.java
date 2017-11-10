package biodiv.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class Utils {

	public static String capitalize(final String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	public static String generateLink(String controller, String action, Map<String, String> linkParams,
			HttpServletRequest request) {
		/*
		 * return userGroupService.userGroupBasedLink(base:
		 * Utils.getDomainServerUrl(request), controller: controller, action:
		 * action, 'userGroupWebaddress':params.webaddress, params: linkParams)
		 */
		return "TODO";
	}

	public static String getDomainName(HttpServletRequest request) {

		return null;
	}
}
