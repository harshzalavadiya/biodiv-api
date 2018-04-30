package biodiv.util;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URIBuilder;

public class Utils {

	static final String[] DATE_PATTERNS = { "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd'T'HH:mm'Z'",
			"EEE, dd MMM yyyy HH:mm:ss z", "yyyy-MM-dd" };

	public static String capitalize(final String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	public static String generateLink(String controller, String action, Map<String, String> linkParams,
			HttpServletRequest request) throws Exception {
		/*
		 * TODO: build userGroup context link return
		 * userGroupService.userGroupBasedLink(base:
		 * Utils.getDomainServerUrl(request), controller: controller, action:
		 * action, 'userGroupWebaddress':params.webaddress, params: linkParams)
		 */
		return buildURL(request, "/" + controller + "/" + action, linkParams);
	}

	private static String buildURL(HttpServletRequest request, String pathInfo, Map<String, String> parameters)
			throws Exception {

		String scheme = request.getScheme();
		String serverName = request.getServerName();
		String contextPath = request.getContextPath();
		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);
		url.append(contextPath).append(pathInfo);

		URIBuilder builder = new URIBuilder(url.toString());
		if (parameters != null && !parameters.isEmpty()) {
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				String key = entry.getKey();
				if (StringUtils.isNotBlank(key)) {
					String value = entry.getValue();
					builder.addParameter(key, value);
				}
			}
		}
		return builder.build().toString();
	}

	public static Date parseDate(Object date, Boolean sendNew) {
		try {
			if (!sendNew) {
				Date d;
				if (date != null) {
					d = DateUtils.parseDate((String) date, DATE_PATTERNS);// Date.parse("dd/MM/yyyy",
																			// date)
					// d.set(['hourOfDay':23, 'minute':59, 'second':59]);
					d.setHours(23);
					d.setMinutes(59);
					d.setSeconds(59);
				} else {
					d = null;
				}
				return d;
			} else {
				return date != null ? DateUtils.parseDate((String) date, DATE_PATTERNS) : new Date();
			}
		} catch (Exception e) {
			throw e;
		} finally {

		}
	}
}
