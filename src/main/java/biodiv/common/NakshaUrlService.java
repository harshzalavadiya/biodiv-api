package biodiv.common;

import org.apache.commons.configuration2.Configuration;

import com.google.inject.Inject;

public class NakshaUrlService {

	@Inject
	Configuration config;
	
	public String getDownloadUrl(String index, String type, String geoField) {
		return config.getString("naksha.url") + "/services/download/" + index + "/" + type + "?filePath=" + config.getString("download.filePath") + "&geoField=" + geoField;
	}
}
