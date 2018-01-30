package biodiv.util;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.DateUtils;









public class Utils {

	static final String[] DATE_PATTERNS = {"dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd'T'HH:mm'Z'", "EEE, dd MMM yyyy HH:mm:ss z", "yyyy-MM-dd"};
	
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

	public static Date parseDate(Object date,Boolean sendNew) {
		try{
			if(!sendNew){
				Date d;
				if(date !=null) {
                    d = DateUtils.parseDate((String) date, DATE_PATTERNS);//Date.parse("dd/MM/yyyy", date) 
                    //d.set(['hourOfDay':23, 'minute':59, 'second':59]);
                    d.setHours(23);
                    d.setMinutes(59);
                    d.setSeconds(59);
                }else {
                    d = null;
                }
				return d;
			}else{
				return date !=null?DateUtils.parseDate((String) date, DATE_PATTERNS):new Date();
			}
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
}
