package biodiv.species;

import org.globalnames.parser.ScientificNameParser;

public class NameParserUtility {

	public static String parse(String name) {
		name.replace(" ", "+");
		name.replace("&", "%26");
		String jsonStr = ScientificNameParser.instance().fromString(name).renderCompactJson();
		
		return jsonStr;
	}
	
	
}
