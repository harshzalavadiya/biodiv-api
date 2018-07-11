package biodiv.admin;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ArrayType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.StringType;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.type.MapType;

import biodiv.maps.MapResponse;

public class AdminDao {

	//private Session currentSession;
	
		@Inject
		private SessionFactory sessionFactory;

		public Map<String,Object> getObservationObject(String observationSql) {
			// TODO Auto-generated method stub
			 SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(observationSql);
		      query.addScalar("id",StringType.INSTANCE);
		      query.addScalar("version",StringType.INSTANCE);
		      query.addScalar("authorid",StringType.INSTANCE);
		      query.addScalar("authorname",StringType.INSTANCE);
		      query.addScalar("authorprofilepic",StringType.INSTANCE);
		      query.addScalar("createdon",DateType.INSTANCE);
		      query.addScalar("speciesgroupid",StringType.INSTANCE);
		      query.addScalar("speciesgroupname",StringType.INSTANCE);
		      query.addScalar("latitude",DoubleType.INSTANCE);
		      query.addScalar("longitude",DoubleType.INSTANCE);
		      query.addScalar("notes",StringType.INSTANCE);
		      query.addScalar("fromdate",DateType.INSTANCE);
		      query.addScalar("placename",StringType.INSTANCE);
		      query.addScalar("rating",StringType.INSTANCE);
		      query.addScalar("reversegeocodedname",StringType.INSTANCE);
		      query.addScalar("flagcount",StringType.INSTANCE);
		      query.addScalar("geoprivacy",StringType.INSTANCE);
		      query.addScalar("habitatid",StringType.INSTANCE);
		      query.addScalar("habitatname",StringType.INSTANCE);
		      query.addScalar("isdeleted",StringType.INSTANCE);
		      query.addScalar("lastrevised",DateType.INSTANCE);
		      query.addScalar("locationaccuracy",StringType.INSTANCE);
		      query.addScalar("visitcount",StringType.INSTANCE);
		      query.addScalar("maxvotedrecoid",StringType.INSTANCE);
		      query.addScalar("agreeterms",StringType.INSTANCE);
		      query.addScalar("ischecklist",StringType.INSTANCE);
		      query.addScalar("isshowable",StringType.INSTANCE);
		      query.addScalar("sourceid",StringType.INSTANCE);
		      query.addScalar("todate",DateType.INSTANCE);
		      query.addScalar("topology",StringType.INSTANCE);
		      query.addScalar("checklistannotations",StringType.INSTANCE);
		      query.addScalar("featurecount",StringType.INSTANCE);
		      query.addScalar("islocked",StringType.INSTANCE);
		      query.addScalar("licenseid",StringType.INSTANCE);
		      query.addScalar("licensename",StringType.INSTANCE);
		      query.addScalar("languageid",StringType.INSTANCE);
		      query.addScalar("languagename",StringType.INSTANCE);
		      query.addScalar("locationscale",StringType.INSTANCE);
		      query.addScalar("accessrights",StringType.INSTANCE);
		      query.addScalar("catalognumber",StringType.INSTANCE);
		      query.addScalar("datasetid",StringType.INSTANCE);
		      query.addScalar("externaldatasetkey",StringType.INSTANCE);
		      query.addScalar("externalid",StringType.INSTANCE);
		      query.addScalar("externalurl",StringType.INSTANCE);
		      query.addScalar("informationwithheld",StringType.INSTANCE);
		      query.addScalar("lastcrawled",StringType.INSTANCE);
		      query.addScalar("lastinterpreted",StringType.INSTANCE);
		      query.addScalar("originalauthor",StringType.INSTANCE);
		      query.addScalar("publishingcountry",StringType.INSTANCE);
		      query.addScalar("reprimageid",StringType.INSTANCE);
		      query.addScalar("viacode",StringType.INSTANCE);
		      query.addScalar("viaid",StringType.INSTANCE);
		      query.addScalar("protocol",StringType.INSTANCE);
		      query.addScalar("basisofrecord",StringType.INSTANCE);
		      query.addScalar("noofimages",StringType.INSTANCE);
		      query.addScalar("noofvideos",StringType.INSTANCE);
		      query.addScalar("noofaudio",StringType.INSTANCE);
		      query.addScalar("noofidentifications",StringType.INSTANCE);
		      query.addScalar("taxonconceptid",StringType.INSTANCE);
		      query.addScalar("acceptednameid",StringType.INSTANCE);
		      query.addScalar("taxonomycanonicalform",StringType.INSTANCE);
		      query.addScalar("status",StringType.INSTANCE);
		      query.addScalar("name",StringType.INSTANCE);
		      query.addScalar("position",StringType.INSTANCE);
		      query.addScalar("rank",StringType.INSTANCE);
		      query.addScalar("thumbnail",StringType.INSTANCE);
		      query.addScalar("usergroupid",StringType.INSTANCE);
		      query.addScalar("usergroupname",StringType.INSTANCE);
		      query.addScalar("imageresource",StringType.INSTANCE);
		      query.addScalar("urlresource",StringType.INSTANCE);
		      query.addScalar("featuredgroups",StringType.INSTANCE);
		      query.addScalar("featurednotes",StringType.INSTANCE);
		      
		      query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		     
			 List<Map<String,Object>> result = query.getResultList();
			 	
				if(result.size() == 0 || result.get(0) == null){
					return null;
				}
				else {
					
					return result.get(0);
				}
			
			
		}

		public List<String> getCustomFieldUserGroupArray(String customFieldTableQuery) {
			// TODO Auto-generated method stub
			 Query query=sessionFactory.getCurrentSession().createSQLQuery(customFieldTableQuery);
			 List result = query.getResultList();
			 return result;
		}

		public Map<String, String> getPathClassificationData(String queryForPathAndClassification) {
			// TODO Auto-generated method stub
			Query query=sessionFactory.getCurrentSession().createSQLQuery(queryForPathAndClassification);
			
			 List<Object[]> results = query.getResultList();
			 Map<String,String> pathClassificationData=new HashMap<String,String>();
			 for(Object[] result:results){
				 pathClassificationData.put("id", result[0].toString());
				 pathClassificationData.put("path", result[1].toString());
				 pathClassificationData.put("classificationid", result[2].toString());
			 }
			return pathClassificationData;
		}

		public Map<String, Object> getTraitKeyValue(String traitKeyValueQuery) {
			// TODO Auto-generated method stub
			Map<String,Object> traits=new HashMap<String,Object>();
			SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(traitKeyValueQuery);
			query.addScalar("traits");
			List result=query.list();
				if(result.size()>0){
					 JSONArray array = new JSONArray(result.get(0).toString());
					  for(int i=0; i<array.length(); i++){
					      JSONObject jsonObj = array.getJSONObject(i);
					      	for(Object key:jsonObj.keySet()){
					      		 String keyValue="trait_"+key;
						    	  Object value=jsonObj.get(key.toString());
						    	  traits.put(keyValue, value);
					      	}
					     }
				}
			 
			
			return traits;
		}

		public Map<String, Object> getTraitRangeValue(String traitRangeQuery, Map<String, Object> traits) {
			// TODO Auto-generated method stub
			
			SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(traitRangeQuery);
			query.addScalar("traits");
			List result=query.list();
			if(result.size()>0){
				JSONArray array = new JSONArray(result.get(0).toString());
				  for(int i=0; i<array.length(); i++){
				      JSONObject jsonObj = array.getJSONObject(i);
				      	for(Object key:jsonObj.keySet()){
				      		 String keyValue="trait_"+key;
					    	  Object value=jsonObj.get(key.toString());
					    	  traits.put(keyValue, value);
				      	}
				     }
			}
			  
			
			return traits;
		}
		public Map<String, Object> getTraitDateValue(String traitDateQuery, Map<String, Object> traits) {
			SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(traitDateQuery);
			query.addScalar("traits");
			List result=query.list();
				if(result.size()>0){
					 JSONArray array = new JSONArray(result.get(0).toString());
					  for(int i=0; i<array.length(); i++){
					      JSONObject jsonObj = array.getJSONObject(i);
					      	for(Object key:jsonObj.keySet()){
					      		 String keyValue="trait_"+key;
						    	  Object value=jsonObj.get(key.toString());
						    	  traits.put(keyValue, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(value));
					      	}
					     }
				}
			 
			
			return traits;
		}
		public Map<String, Object> getTraitSeasonDateValue(String traitSeasonDateQuery) {
			// TODO Auto-generated method stub
			Map<String, Object> traits_season=new HashMap<String,Object>();
			SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(traitSeasonDateQuery);
			query.addScalar("traits");
			List result=query.list();
				if(result.size()>0){
					 JSONArray array = new JSONArray(result.get(0).toString());
					  for(int i=0; i<array.length(); i++){
					      JSONObject jsonObj = array.getJSONObject(i);
					      	for(Object key:jsonObj.keySet()){
					      		 String keyValue="trait_"+key;
						    	  Object value=jsonObj.get(key.toString());
						    	  JSONArray v= jsonObj.getJSONArray(key.toString());
						    	  List<String> data=new ArrayList<String>();
						    	  for(int j=0;j<v.length();j++){
						    		data.add(v.getString(j));
						    	  }
						    	  	Map<String,Object> dates=new HashMap<String,Object>();
				                    dates.put("gte",data.get(0));
				                    dates.put("lte",data.get(1));
				                    traits_season.put(keyValue,dates);
					      	}
					     }
				}
			 
			return traits_season;
		}

		public Map<String, Object> getTraitColorValue(String traitColorQuery) {
			// TODO Auto-generated method stub
			Map<String,Object> traits_json=new HashMap<String,Object>();
			SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(traitColorQuery);
			query.addScalar("traits");
			List result=query.list();
			if(result.size()>0){
				JSONArray array = new JSONArray(result.get(0).toString());
				 for(int i=0; i<array.length(); i++){
				      JSONObject jsonObj = array.getJSONObject(i);
				      	for(Object key:jsonObj.keySet()){
				      		String keyValue="trait_"+key;
				      		List<Map<String,Object>> ColorArray=new ArrayList<Map<String,Object>>();
				      		JSONArray color=new JSONArray(jsonObj.get(key.toString()));
				      		
				      		List<String> v = new ArrayList<String>();
				      		for(int j = 0; j < color.length(); j++){
				      		    v.add(color.getString(j));
				      		}
				      		
				      	    	for(String x:v){
				      	    		 String newrgb=x.trim().substring(4,x.length()-1);
						                String[] items =newrgb.split(",");
						                List<Integer> rgbList=new ArrayList<Integer>();
			                			for(String y:items){
			                				rgbList.add(Integer.parseInt(y));
			                			}
			                			float[] hsv = new float[3];
			                			Color.RGBtoHSB(rgbList.get(0),rgbList.get(1),rgbList.get(2),hsv);
			                			Map<String,Object> hslMap=new HashMap<String,Object>();
			                			hslMap.put("h", hsv[0]);
			                			hslMap.put("s", hsv[1]);
			                			hslMap.put("l", hsv[2]);
			                      ColorArray.add(hslMap);
				      	    	}
				      	    	traits_json.put(keyValue, ColorArray);
				      		
				      	}
				     }
			}
			
			return traits_json;
			
		}
		
		
}
