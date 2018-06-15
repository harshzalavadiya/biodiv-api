package biodiv.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URIBuilder;

import biodiv.species.NamesParser;
import biodiv.taxon.datamodel.dao.Taxon;

public class Utils {

	static final String[] DATE_PATTERNS = { "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd'T'HH:mm'Z'",
			"EEE, dd MMM yyyy HH:mm:ss z", "yyyy-MM-dd" };
	
	private static final NamesParser namesParser = new NamesParser();

	public static String capitalize(final String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	public static String generateLink(String controller, String action, Map<String, String> linkParams,
			HttpServletRequest request) throws Exception {
		/* TODO: build userGroup context link
		 * return userGroupService.userGroupBasedLink(base:
		 * Utils.getDomainServerUrl(request), controller: controller, action:
		 * action, 'userGroupWebaddress':params.webaddress, params: linkParams)
		 */
		return buildURL(request.getRequestURL() + "/" + controller + "/" + action, linkParams);
	}

	private static String buildURL(String base, Map<String, String> parameters) throws Exception {
		if (base != null && StringUtils.isNotBlank(base)) {
			URIBuilder builder = new URIBuilder(base);
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
		return null;
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

	public static String cleanName(String name) {
		
		if(name == null){
			return null;
		}
		name = name.replaceAll("/<.*?>/", "").replaceAll("\u00A0|\u2007|\u202F", " ").replaceAll("\\n","").replaceAll("\\s+", " ").replaceAll("\\*", "").trim();
		System.out.println("cleaning of name");
		System.out.println("cleaning of name");
		System.out.println("cleaning of name");
		System.out.println("cleaning of name");
		System.out.println(name);
		return name;
	}

	public static String getTitleCase(String str) {
		org.apache.commons.lang3.text.WordUtils.capitalizeFully(str, " (/".toCharArray());
		return null;
	}


	public static String getCanonicalForm(String name) {
		
		List<String> x = new ArrayList<String>();
		x.add(name);
		List<Taxon> ltax = namesParser.parse(x);
		Taxon taxdef = null;
		if(ltax !=null){
			taxdef = ltax.get(0);
		}
		if(taxdef !=null){
			if(taxdef.getCanonicalForm()!=null){
				System.out.println("canonical form not null");
				System.out.println("canonical form not null");
				System.out.println("canonical form not null");
				System.out.println("canonical form not null");
				System.out.println(taxdef.getCanonicalForm());
				return taxdef.getCanonicalForm();
			}else{
				System.out.println("canonical form  null");
				System.out.println("canonical form  null");
				System.out.println("canonical form  null");
				System.out.println("canonical form  null");
				System.out.println(taxdef.getName());
				return taxdef.getName();
			}
		}
		//have to check whthrer dropdown selected Names which are scientific going through it.
		return cleanSciName(name);
	
	}

	private static String cleanSciName(String sciName) {
		String cleanSciName = cleanName(sciName);
		
//		if(cleanSciName =~ /s\.\s*str\./) {
//            cleanSciName = cleanSciName.replaceFirst(/s\.\s*str\./, cleanSciName.split()[0]);
//        }
		
		if(cleanSciName.indexOf(" ") == -1) {
            cleanSciName = cleanSciName.toLowerCase().substring(0,1).toUpperCase() + cleanSciName.toLowerCase().substring(1);
        } else {
            cleanSciName = cleanSciName.substring(0,1).toUpperCase() + cleanSciName.substring(1);
        }
		return cleanSciName;
	}
}
