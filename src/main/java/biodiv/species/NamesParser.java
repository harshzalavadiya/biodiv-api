package biodiv.species;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.util.Utils;

public class NamesParser {

	public NamesParser(){
		
	}

	public List<Taxon> parse(List<String> names) {
		
		List<String> cleanNames = new ArrayList<String>();
		List<Taxon> parsedNames = new ArrayList<Taxon>();
		for(String name : names){
			cleanNames.add(Utils.cleanName(name));
		}
		
		if(cleanNames !=null){
			List<JSONObject> parsedNamesJSON;
			try {
				parsedNamesJSON = gniNamesParser(cleanNames);
				parsedNames.addAll(getParsedNames(parsedNamesJSON));
				cleanNames.clear();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return parsedNames;
	}

	private List<Taxon> getParsedNames(List<JSONObject> parsedNamesJSON) {
		
		List<Taxon> parsedNames = new ArrayList<Taxon>();
		if(parsedNamesJSON !=null){
			for(JSONObject js : parsedNamesJSON){
				JSONObject sciName =  js;
				
				if((Boolean)sciName.get("parsed")){
					String hybridName;
					Taxon parsedName = new Taxon();
					JSONObject details =   ((org.json.JSONArray) sciName.get("details")).getJSONObject(0);
				
						sciName.put("verbatim", ((String) sciName.get("verbatim")).replaceAll("\n", ""));
						
						if(sciName.has("canonical_name")){
							parsedName.setCanonicalForm((String) ((JSONObject) sciName.get("canonical_name")).get("value"));
						}
						if(sciName.has("normalized")){
							parsedName.setNormalizedForm(((String) sciName.get("normalized")));
						}
						if(sciName.has("verbatim")){
							parsedName.setName((String) sciName.get("verbatim"));
						}
						
						parsedName.setItalicisedForm(getItalicisedForm(sciName));
						
						
						if(details.has("genus") && ((JSONObject) details.get("genus")).has("value")){
							if(details.has("specific_epithet") && ((JSONObject) details.get("specific_epithet")).has("value")){
								parsedName.setBinomialForm(((JSONObject) details.get("genus")).get("value")+" "+((JSONObject) details.get("specific_epithet")).get("value"));
							}
						}
						
						if(details.has("specific_epithet")){
							if( ((JSONObject) details.get("specific_epithet")).has("authorship")){
								parsedName.setAuthorYear(((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).get("value").toString());
							}
							
							
							if( ((JSONObject) details.get("specific_epithet")).has("authorship") && ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).has("combination_authorship") &&  ( (JSONObject) ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).get("combination_authorship")).has("authors")){
								for( String author : (List<String>)( (JSONObject) ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).get("combination_authorship")).get("authors")  ) {
									//parsedName.addToAuthor(author);
								}
							}
							
							if( ((JSONObject) details.get("specific_epithet")).has("authorship") && ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).has("basionym_authorship") &&  ( (JSONObject) ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).get("basionym_authorship")).has("authors")){
								for( String author : (List<String>)( (JSONObject) ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).get("basionym_authorship")).get("authors")  ) {
									//parsedName.addToAuthor(author);
								}
							}
							
							if( ((JSONObject) details.get("specific_epithet")).has("authorship") && ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).has("combination_authorship") &&  ( (JSONObject) ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).get("combination_authorship")).has("year")){
								//parsedName.addToYear(((JSONObject) ( (JSONObject) ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).get("combination_authorship")).get("year")).get("value").toString());
							}
								
							if(((JSONObject) details.get("specific_epithet")).has("authorship") && ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).get("basionym_authorship")!=null &&  ( (JSONObject) ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).get("basionym_authorship")).get("year")!=null){
								//parsedName.addToYear(((JSONObject) ( (JSONObject) ((JSONObject) ((JSONObject) details.get("specific_epithet")).get("authorship")).get("basionym_authorship")).get("year")).get("value").toString());
							}
						}
						
						if(details.has("infraspecies") && ((org.json.JSONArray) details.get("infraspecies")).get(0)!=null){
							parsedName.setAuthorYear(((JSONObject) ((org.json.JSONArray) details.get("infraspecies")).get(0)).get("authorship").toString());
						}
						
						parsedNames.add(parsedName);
						
					
				}else{
					System.out.println("name not parsed");
					//parsedNames.add(new Taxon(sciName.get("verbatim")));
					//returning null when parsing fails
					parsedNames.add(null);
				}
			}
		}
		
		return parsedNames;
	}

	private String getItalicisedForm(JSONObject sciName) {
		
		String name = (String) sciName.get("verbatim");
		BitSet flags = new BitSet(name.length());
		
		String italicisedForm = name;
		org.json.JSONArray positions =  (org.json.JSONArray) sciName.get("positions");
		
		for(int i = 0; i<positions.length(); i++){
			org.json.JSONArray position = positions.getJSONArray(i);
			if(position.get(0).equals("author_word")){
				flags.set((int) position.get(1));
				flags.set((int) position.get(2));
			}else if(position.get(0).equals("year")){
				flags.set((int) position.get(1));
				flags.set((int) position.get(2));
			}
		}
		Pattern pattern =  Pattern.compile("\\s[a-z]+\\.");//store in class
		Matcher matcher = pattern.matcher(name);
		
		while(matcher.find()){
			flags.set(matcher.start()+1);
			flags.set(matcher.end());
		}
		int start = 0;
		int prevStart = 0;
		int end = 0;
		italicisedForm = "<i>";
		for(int i=flags.nextSetBit(0);  i>=0;  i=flags.nextSetBit(i+1)){
			start = i;
			i = end = flags.nextSetBit(i+1);
			if(start >= name.length()) break;
			if(end >= 0 && end < name.length()-1){
				italicisedForm += name.substring(prevStart, start) + "</i>" + name.substring(start, end) + "<i>";
			}else{
				italicisedForm += name.substring(prevStart, start) + "</i>" + name.substring(start) + "<i>";
				prevStart = name.length();
				break;
			}
			prevStart = end;
			start = end = 0;
		}
		if(prevStart < name.length())
			italicisedForm += name.substring(prevStart);

		italicisedForm += "</i>";
		italicisedForm = italicisedForm.replaceAll("<i>\\s*</i>", " ");
		italicisedForm = italicisedForm.replaceAll("<i>\\s*,\\s*</i>",", ");
		
		return italicisedForm;
	}

	private List<JSONObject> gniNamesParser(List<String> names) throws Exception {
		
		try{
			List<JSONObject> parsedJSON = new ArrayList<JSONObject>();
			
			for(String name : names){
				String message = NameParserUtility.parse(name);
				parsedJSON.add(new JSONObject(message));
			}
			
			return parsedJSON;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	
}
