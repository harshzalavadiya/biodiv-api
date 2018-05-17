package biodiv.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.json.CDL;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import biodiv.Transactional;
import biodiv.activityFeed.ActivityFeedService;
import biodiv.customField.CustomField;
import biodiv.customField.CustomFieldService;
import biodiv.scheduler.CSVUtils;
import biodiv.traits.Trait;
import biodiv.traits.TraitService;


public class AdminService {
	
	@Inject
	private TraitService traitService;
	@Inject 
	private CustomFieldService customFieldService;
	
	@Transactional
	public void downloadFile(String fileName) throws IOException {
		// TODO Auto-generated method stub
		String traitsQuery="select '{' || string_agg(format('\"%s\":{%s:%s}', to_json(tvs.id),to_json(tvs.name), to_json(tvs.values)), ',') || '}' as alltraits  from ( select  t.id as id,t.name as name,array_remove(array_agg(DISTINCT tv.value), NULL) as values from trait t left join trait_value  tv on tv.trait_instance_id = t.id group by t.id,t.name) as tvs";
		String customQuery="select id,name,options from custom_field where options is not null";
		
		ArrayList<JSONObject> json=new ArrayList<JSONObject>();
	    JSONObject obj;
	    
	    
	    
	    String line = null;
	    Map<String,Map<String,Object>> traitValues= traitService.getAllTraitsWithValues(traitsQuery);
	    List<CustomField> allCustomFields=customFieldService.fetchAllCustomFields();

	    List<Object> header = new ArrayList<>();
	    String[] arr=fileName.split("\\.");
	    String outfileName=arr[0]+".csv";
	    System.out.println(outfileName);
	    FileWriter fileWriter = new FileWriter(outfileName);
	    try {
	        // FileReader reads text files in the default encoding.
	        FileReader fileReader = new FileReader(fileName);

	        // Always wrap FileReader in BufferedReader.
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        
	        
	        while((line = bufferedReader.readLine()) != null) {
	         JSONObject obj2 = new JSONObject(line);
	         
	         
//	         System.out.println(obj2.get("traits"));
//	         System.out.println(obj2.get("custom_fields"));
	         ObjectMapper objectMapper=new ObjectMapper();
	         Map<String,Object> originalData=objectMapper.readValue(obj2.toString(), Map.class);
	         
	         /**
	          * Insert Dummy coulmn for customFields
	          */
	         for(CustomField cf:allCustomFields){
	        	if(cf.getOptions()!=null){
	        		String optionArr[]=cf.getOptions().split(",");
	        		for(String opt:optionArr){
	        			String key="cf_"+cf.getName()+"_"+opt;
	        			originalData.put(key, false);
	        		}
	        	}
	        	else{
	        		String key="cf_"+cf.getName();
	        		originalData.put(key, null);
	        	}
	         }
	         /**
	          * Insert Dummy Column for Traits
	          */
	         for (Entry<String, Map<String, Object>> entry : traitValues.entrySet()) {
			        for (Entry<String, Object> entry2 : entry.getValue().entrySet()) {
			        
			        		if (entry2.getValue() instanceof List) {
			        		    List<String> objects  =  (List<String>) entry2.getValue();
			        		   if(objects.size()>0){
			        			   for (String obj3 : objects){
				        		    	String key="trait_"+entry2.getKey()+"_"+obj3;
				        		    	originalData.put(key, false);
				        		    }
			        		   }
			        		   else{
			        			   String key="trait_"+entry2.getKey();
			        			   originalData.put(key, null);
			        		   }
			        		    
			        		    	
			        		}   

			        		
			        }
			        
			    }
	         
	         
	         /**
	          * read custom_fields,traits,traits_json,traits_season from observation object
	          */
	         
	        
	         
			 Map<String,Map<String,Object>> custoFieldsData=objectMapper.readValue(obj2.get("custom_fields").toString(), Map.class);
			
			 Map<String,Object> traitsData=objectMapper.readValue(obj2.get("traits").toString(), Map.class);
			 
			 Map<String,Map<String,Object>> traits_season=objectMapper.readValue(obj2.get("traits_season").toString(), Map.class);
			 
			 Map<String, List<Map<String,Object>>> traits_json=objectMapper.readValue(obj2.get("traits_json").toString(), Map.class);
			 originalData.remove("custom_fields");
			 originalData.remove("traits");
			 originalData.remove("traits_season");
			 originalData.remove("traits_json");

			/**
			 * insert custom Field Data
			 */
			 
			 for (Entry<String, Map<String, Object>> entry : custoFieldsData.entrySet()) {
				 String key=null;
				 Object value=null;
			        for (Entry<String, Object> entry2 : entry.getValue().entrySet()) {
			        			if(entry2.getKey().equalsIgnoreCase("key")){
			        				key=entry2.getValue().toString();
			        			}
			        			if(entry2.getKey().equalsIgnoreCase("value")){
			        				value=entry2.getValue();
			        			}
			        }
			        
			        for(CustomField cf:allCustomFields){
			        	if(cf.getName().equalsIgnoreCase(key)){
			        		if(cf.getOptions()!=null){
			        			if (value instanceof List) {
			        				 List<String> objects  =  (List<String>) value;
					        		   if(objects.size()>0){
					        			   for (String obj3 : objects){
					        				   String newkey="cf_"+cf.getName()+"_"+obj3;
						        		    	originalData.put(newkey, true);
						        		    }
					        		   }
			        			}
			        			else{
			        				String newkey="cf_"+cf.getName()+"_"+value;
			        				originalData.put(newkey, true);
			        			}
			        			}
			        		else{
			        			String newkey="cf_"+cf.getName();
			        			originalData.put(newkey, value);
			        		}
			        		
			        	}
			         }
			        
			        
			    }			 
					 
			/**
			 * insert trait data
			 */
			 for (Entry<String,  Object> entry : traitsData.entrySet()) {
				 
			        String traitId=entry.getKey().split("_")[1];
			        Map<String,Object> trait=traitValues.get(traitId);
			        
			        Trait singleTrait=traitService.getSingleTrait(Long.valueOf(traitId));
			       
			        String name=null;
			        for (Entry<String, Object> entry3 : trait.entrySet()) {
			        	 name=entry3.getKey();
			        }
			        if (entry.getValue() instanceof List) {
	        		    List<String> objects  =  (List<String>) entry.getValue();
	        		    if(singleTrait.getTraitTypes().equalsIgnoreCase("RANGE")){
	        		    	if(objects.size()>=2){
	        		    		String data=String.valueOf(objects.get(0))+"-"+String.valueOf(objects.get(1));
		        		    	String key="trait_"+name;
		        		    	originalData.put(key, data);
	        		    	}
	        		    	
	        		    }
	        		    else{
	        		    	for (String obj3 : objects){
		        		    	String key="trait_"+name+"_"+obj3;
		        		    	originalData.put(key, true);
		        		    }
	        		    }
	        		    
	        		    	
	        		} 
			        else{
			        	String key="trait_"+name;
        		    	originalData.put(key, entry.getValue());
			        }
			        
			    }
			 /**
			  * Inset traits season data of type month
			  */
			 for (Entry<String, Map<String, Object>> entry : traits_season.entrySet()) {
				 String traitId=entry.getKey().split("_")[1];
				 Map<String,Object> trait=traitValues.get(traitId);
//				 Trait singleTrait=traitService.getSingleTrait(Long.valueOf(traitId));
				 //get the name of the traits
				 String name=null;
			        for (Entry<String, Object> entry3 : trait.entrySet()) {
			        	 name=entry3.getKey();
			        }
			        String gte=null;
			        String lte=null;
			        for (Entry<String, Object> entry2 : entry.getValue().entrySet()) {
	        			if(entry2.getKey().equalsIgnoreCase("gte")){
	        				gte=entry2.getValue().toString();
	        			}
	        			if(entry2.getKey().equalsIgnoreCase("lte")){
	        				lte=entry2.getValue().toString();
	        			}
			        }
			        String key="trait_"+name;
			        
			        originalData.put(key, gte+"-"+lte);
			        
			        
			 }
			 
			 /**
			  * Insert traits_json in 
			  */
			 for (Entry<String, List<Map<String, Object>>> entry : traits_json.entrySet()) {
				 String traitId=entry.getKey().split("_")[1];
				 Map<String,Object> trait=traitValues.get(traitId);
				 String name=null;
			        for (Entry<String, Object> entry3 : trait.entrySet()) {
			        	 name=entry3.getKey();
			        }
			        List<Map<String, Object>> colorData=entry.getValue();
			        String color=null;
			        for(Map<String,Object> singleColor:colorData){
			        	String singleColorData="hsl(";
			        	for (Entry<String, Object> colorEntry : singleColor.entrySet()){
			        		if(colorEntry.getKey().equalsIgnoreCase("h")){
			        			singleColorData=singleColorData+colorEntry.getKey().toString()+",";
			        		}
			        		if(colorEntry.getKey().equalsIgnoreCase("s")){
			        			singleColorData=singleColorData+colorEntry.getKey().toString()+",";
			        		}
			        		if(colorEntry.getKey().equalsIgnoreCase("l")){
			        			singleColorData=singleColorData+colorEntry.getKey().toString()+")";
			        		}
			        		
			        	}
			        	color=color+singleColorData+";";
			        }
			        String key="trait_"+name;
			        originalData.put(key,color); 
			 }
			 
			 /**
			  * here convert this Map<String, Object> to csv
			  */
			 
//			   System.out.println(originalData);
			   
			   if(header.isEmpty()) {
				   header.addAll(originalData.keySet());
			   fileWriter.write(CSVUtils.getCsvString(header));
			   }
			   List<Object> values = new ArrayList<>();
			   for(String key : originalData.keySet())
				   values.add(originalData.get(key));
			   String csvString = CSVUtils.getCsvString(values);
			   fileWriter.write(csvString);
			 
			
	        }
	        fileWriter.close();
	        //end of object 2
	        // Always close files.
	        bufferedReader.close();         
	    }
	    catch(FileNotFoundException ex) {
	        System.out.println("Unable to open file '" + fileName + "'");                
	    }
	    catch(IOException ex1) {
	        System.out.println("Error reading file '" + fileName + "'");                  
	        // Or we could just do this: 
	         ex1.printStackTrace();
	    } 
		
		
	}

	
	
}

