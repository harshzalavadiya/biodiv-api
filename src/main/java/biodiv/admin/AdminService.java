package biodiv.admin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import org.apache.commons.configuration2.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.compiler.STParser.singleElement_return;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import biodiv.Transactional;

import biodiv.common.ZipService;
import biodiv.customField.CustomField;
import biodiv.customField.CustomFieldService;
import biodiv.maps.MapIntegrationService;
import biodiv.observation.Observation;
import biodiv.observation.ObservationListMapper;
import biodiv.observation.ObservationService;
import biodiv.scheduler.CSVUtils;
import biodiv.traits.Trait;
import biodiv.traits.TraitService;

public class AdminService {
	
	private final ObjectMapper objectMapper = new ObjectMapper(); 
	private final Logger logger = LoggerFactory.getLogger(AdminService.class);
	
	
	
	
	@Inject
	private TraitService traitService;
	@Inject
	private CustomFieldService customFieldService;
	@Inject
	private ObservationService observationService;
	
	@Inject
	private AdminDao adminDao;
	
	@Inject
	Configuration config;
	private HttpClientContext context;
	
	
	@Transactional
	public String downloadFile(String fileName) throws IOException {
		// TODO Auto-generated method stub
		String traitsQuery = "select '{' || string_agg(format('\"%s\":{%s:%s}', to_json(tvs.id),to_json(tvs.name), to_json(tvs.values)), ',') || '}' as alltraits  from ( select  t.id as id,t.name as name,array_remove(array_agg(DISTINCT tv.value), NULL) as values from trait t left join trait_value  tv on tv.trait_instance_id = t.id group by t.id,t.name) as tvs";
		String customQuery = "select id,name,options from custom_field where options is not null";

		ArrayList<JSONObject> json = new ArrayList<JSONObject>();
		JSONObject obj;

		Boolean flag = true;

		String line = null;
		Map<String, Map<String, Object>> traitValues = traitService.getAllTraitsWithValues(traitsQuery);
		List<CustomField> allCustomFields = customFieldService.fetchAllCustomFields();

		List<Object> header = new ArrayList<>();
		Field[] allFields = ObservationListMapper.class.getDeclaredFields();
		for (Field field : allFields) {
			if (Modifier.isPrivate(field.getModifiers())) {
				header.add(field.getName());
			}
		}
		List<Object> newHeader = new ArrayList<Object>();
		String[] arr = fileName.split("\\.");
		String outfileName = arr[0] + ".csv";
		FileWriter fileWriter = new FileWriter(outfileName);

		try {
			// FileReader reads text files in the default encoding.
			ZipFile zipFile = new ZipFile(fileName);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			FileReader fileReader = new FileReader(fileName);

			while (entries.hasMoreElements()) {
				ZipEntry entrys = entries.nextElement();
				InputStream stream = zipFile.getInputStream(entrys);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
				while ((line = bufferedReader.readLine()) != null) {
					JSONObject obj2 = new JSONObject(line);
					// System.out.println(obj2.toString());
					// System.out.println(obj2.get("traits"));
					// System.out.println(obj2.get("custom_fields"));
					ObjectMapper objectMapper = new ObjectMapper();
					Map<String, Object> originalData = objectMapper.readValue(obj2.toString(), Map.class);

					/**
					 * Insert Dummy coulmn for customFields
					 */

					for (CustomField cf : allCustomFields) {
						if (cf.getOptions() != null) {
							String optionArr[] = cf.getOptions().split(",");
							for (String opt : optionArr) {
								String key = "cf_" + cf.getName() + "_" + opt;
								originalData.put(key, false);
							}
						} else {
							String key = "cf_" + cf.getName();
							originalData.put(key, null);
						}
					}
					/**
					 * Insert Dummy Column for Traits
					 */
					for (Entry<String, Map<String, Object>> entry : traitValues.entrySet()) {
						for (Entry<String, Object> entry2 : entry.getValue().entrySet()) {

							if (entry2.getValue() instanceof List) {
								List<String> objects = (List<String>) entry2.getValue();
								if (objects.size() > 0) {
									for (String obj3 : objects) {
										String key = "trait_" + entry2.getKey() + "_" + obj3;
										originalData.put(key, false);
									}
								} else {
									String key = "trait_" + entry2.getKey();
									originalData.put(key, null);
								}

							}

						}

					}

					/**
					 * read custom_fields,traits,traits_json,traits_season from
					 * observation object
					 */

					Map<String, Map<String, Object>> custoFieldsData = objectMapper
							.readValue(obj2.get("custom_fields").toString(), Map.class);

					Map<String, Object> traitsData = objectMapper.readValue(obj2.get("traits").toString(), Map.class);

					Map<String, Map<String, Object>> traits_season = objectMapper
							.readValue(obj2.get("traits_season").toString(), Map.class);

					Map<String, List<Map<String, Object>>> traits_json = objectMapper
							.readValue(obj2.get("traits_json").toString(), Map.class);
					originalData.remove("custom_fields");
					originalData.remove("traits");
					originalData.remove("traits_season");
					originalData.remove("traits_json");

					/**
					 * insert custom Field Data
					 */

					for (Entry<String, Map<String, Object>> entry : custoFieldsData.entrySet()) {
						String key = null;
						Object value = null;
						for (Entry<String, Object> entry2 : entry.getValue().entrySet()) {
							if (entry2.getKey().equalsIgnoreCase("key")) {
								key = entry2.getValue().toString();
							}
							if (entry2.getKey().equalsIgnoreCase("value")) {
								value = entry2.getValue();
							}
						}

						for (CustomField cf : allCustomFields) {
							if (cf.getName().equalsIgnoreCase(key)) {
								if (cf.getOptions() != null) {
									if (value instanceof List) {
										List<String> objects = (List<String>) value;
										if (objects.size() > 0) {
											for (String obj3 : objects) {
												String newkey = "cf_" + cf.getName() + "_" + obj3;
												originalData.put(newkey, true);
											}
										}
									} else {
										String newkey = "cf_" + cf.getName() + "_" + value;
										originalData.put(newkey, true);
									}
								} else {
									String newkey = "cf_" + cf.getName();
									originalData.put(newkey, value);
								}

							}
						}

					}

					/**
					 * insert trait data
					 */
					for (Entry<String, Object> entry : traitsData.entrySet()) {

						String traitId = entry.getKey().split("_")[1];
						Map<String, Object> trait = traitValues.get(traitId);

						Trait singleTrait = traitService.getSingleTrait(Long.valueOf(traitId));

						String name = null;
						for (Entry<String, Object> entry3 : trait.entrySet()) {
							name = entry3.getKey();
						}
						if (entry.getValue() instanceof List) {
							List<String> objects = (List<String>) entry.getValue();
							if (singleTrait.getTraitTypes().equalsIgnoreCase("RANGE")) {
								if (objects.size() >= 2) {
									String data = String.valueOf(objects.get(0)) + "-" + String.valueOf(objects.get(1));
									String key = "trait_" + name;
									originalData.put(key, data);
								}

							} else {
								for (String obj3 : objects) {
									String key = "trait_" + name + "_" + obj3;
									originalData.put(key, true);
								}
							}

						} else {
							String key = "trait_" + name;
							originalData.put(key, entry.getValue());
						}

					}
					/**
					 * Inset traits season data of type month
					 */
					for (Entry<String, Map<String, Object>> entry : traits_season.entrySet()) {
						String traitId = entry.getKey().split("_")[1];
						Map<String, Object> trait = traitValues.get(traitId);
						// Trait
						// singleTrait=traitService.getSingleTrait(Long.valueOf(traitId));
						// get the name of the traits
						String name = null;
						for (Entry<String, Object> entry3 : trait.entrySet()) {
							name = entry3.getKey();
						}
						String gte = null;
						String lte = null;
						for (Entry<String, Object> entry2 : entry.getValue().entrySet()) {
							if (entry2.getKey().equalsIgnoreCase("gte")) {
								gte = entry2.getValue().toString();
							}
							if (entry2.getKey().equalsIgnoreCase("lte")) {
								lte = entry2.getValue().toString();
							}
						}
						String key = "trait_" + name;

						originalData.put(key, gte + "-" + lte);

					}

					/**
					 * Insert traits_json in
					 */
					for (Entry<String, List<Map<String, Object>>> entry : traits_json.entrySet()) {
						String traitId = entry.getKey().split("_")[1];
						Map<String, Object> trait = traitValues.get(traitId);
						String name = null;
						for (Entry<String, Object> entry3 : trait.entrySet()) {
							name = entry3.getKey();
						}
						List<Map<String, Object>> colorData = entry.getValue();
						String color = null;
						for (Map<String, Object> singleColor : colorData) {
							String singleColorData = "hsl(";
							for (Entry<String, Object> colorEntry : singleColor.entrySet()) {
								if (colorEntry.getKey().equalsIgnoreCase("h")) {
									singleColorData = singleColorData + colorEntry.getKey().toString() + ",";
								}
								if (colorEntry.getKey().equalsIgnoreCase("s")) {
									singleColorData = singleColorData + colorEntry.getKey().toString() + ",";
								}
								if (colorEntry.getKey().equalsIgnoreCase("l")) {
									singleColorData = singleColorData + colorEntry.getKey().toString() + ")";
								}

							}
							color = color + singleColorData + ";";
						}
						String key = "trait_" + name;
						originalData.put(key, color);
					}

					/**
					 * here convert this Map<String, Object> to csv
					 */

					// System.out.println(originalData);

					if (flag) {
						for (String key : originalData.keySet()) {
							header.add(key);
						}
						newHeader = header.stream().distinct().collect(Collectors.toList());
						fileWriter.write(CSVUtils.getCsvString(newHeader));
						flag = false;
					}

					List<Object> values = new ArrayList<>();
					for (Object key : newHeader) {

						values.add(originalData.get(key));
					}
					String csvString = CSVUtils.getCsvString(values);
					fileWriter.write(csvString);
				}
				fileWriter.close();
				bufferedReader.close();

			}

		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex1) {
			System.out.println("Error reading file '" + fileName + "'");
			// Or we could just do this:
			ex1.printStackTrace();
		}
		String zipFileName = ZipService.zipFile(arr[0]);
		
		return zipFileName;

	}

	




	public void publishObservationSearchIndex(List<Observation> obvs) {
		// TODO Auto-generated method stub
		for(Observation obv:obvs){
			List<Map<String,Object>> data_to_elastic=new ArrayList<Map<String,Object>>();
			Map<String, Object> traits=new HashMap<String,Object>();
			Map<String, Object> traits_json=new HashMap<String,Object>();
			Map<String, Object> traits_season=new HashMap<String,Object>();
			Map<String, Object> custom_fields=new HashMap<String,Object>();
			Map<String,String> pathClassificationData=new HashMap<String,String>();
			List<String> customFieldUserGroupArray=new ArrayList<String>();
			
			  String observationJoinQuery=getObservationJoinQuery(obv.getId());
			  Map<String,Object> singleObv=adminDao.getObservationObject(observationJoinQuery);
			
			  String customFieldTableQuery=getCustomFieldTableQuery(obv.getId());
			  customFieldUserGroupArray =adminDao.getCustomFieldUserGroupArray(customFieldTableQuery);
			  
			  String queryForPathAndClassification=getQueryForPathAndClassification(obv.getId());
			  pathClassificationData=adminDao.getPathClassificationData(queryForPathAndClassification);
			  
			  
			  String traitKeyValueQuery=getTraitKeyValueQuery(obv.getId());
			  traits=adminDao.getTraitKeyValue(traitKeyValueQuery);
			  
			  String traitRangeQuery=getTraitRangeQueryString(obv.getId());
			  traits= adminDao.getTraitRangeValue(traitRangeQuery,traits);
			  
			  String traitDateQuery=getTraitDateQuery(obv.getId());
			  traits= adminDao.getTraitDateValue(traitDateQuery,traits);
			  
			  String traitColorQuery=getTraitColorQuery(obv.getId());
			  traits_json=adminDao.getTraitColorValue(traitColorQuery);
			  
			  String traitSeasonDateQuery=traitSeasonDate(obv.getId());
			  traits_season=adminDao.getTraitSeasonDateValue(traitSeasonDateQuery);
			 
			  List<Map<String, Object>> custom_fields_one_obv =observationService.getCustomFields(obv.getId());
			  custom_fields=getCustomFieldsMap(custom_fields_one_obv);
			 
			  	List<Object> location=new ArrayList<Object>();
			  	
			  	location.add(singleObv.get("longitude"));
			  	location.add(singleObv.get("latitude"));
			  	
			  	singleObv.put("location", location);
			  	
			  	if(singleObv.get("fromdate")!=null){
			  		singleObv.put("frommonth",new SimpleDateFormat("M").format(singleObv.get("fromdate")));
			  		singleObv.put("fromdate",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(singleObv.get("fromdate")));
			  		
			  	}
			  	if(singleObv.get("lastrevised")!=null){
			  		singleObv.put("lastrevised",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(singleObv.get("lastrevised")));
			  	}
			  	if(singleObv.get("createdon")!=null){
			  		singleObv.put("createdon",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(singleObv.get("createdon")));
			  	}
			  	if(singleObv.get("todate")!=null){
			  		singleObv.put("todate",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(singleObv.get("todate")));
			  	}
				singleObv.put("checklistannotations",null);
			  
			  if(pathClassificationData.size()>0){
				  singleObv.put("path", pathClassificationData.get("path"));
				  singleObv.put("classificationid", pathClassificationData.get("classificationid"));
				  
			  }
			  else{
				  singleObv.put("path", null);
				  singleObv.put("classificationid", null);
			  }
			  
			  String noofaudio=singleObv.get("noofaudio").toString();
			  String noofimages=singleObv.get("noofimages").toString();
			  String noofvideos=singleObv.get("noofvideos").toString();
			  
			  if(noofaudio.equalsIgnoreCase("0") && noofimages.equalsIgnoreCase("0") && noofvideos.equalsIgnoreCase("0")){
				  singleObv.put("nomedia", "1");
			  }else{
				  singleObv.put("nomedia", "0");
			  }
			  
			  singleObv.put("traits", traits);
			  singleObv.put("traits_season", traits_season);
			  singleObv.put("traits_json",traits_json );
			  singleObv.put("custom_fields", custom_fields);
			  
			  String imageresource=singleObv.get("imageresource").toString();
			  String usergroupname=singleObv.get("usergroupname").toString();
			  String usergroupid=singleObv.get("usergroupid").toString();
			  String urlresource=singleObv.get("urlresource").toString();
			  String featurednotes=singleObv.get("featurednotes").toString();
			  String featuredgroups=singleObv.get("featuredgroups").toString();
			  
			  imageresource=imageresource.substring(1, imageresource.length()-1);
			  singleObv.put("imageresource", imageresource.split(","));
			  
			  usergroupname=usergroupname.substring(1, usergroupname.length()-1);
			  singleObv.put("usergroupname", usergroupname.split(","));
			  
			  usergroupid=usergroupid.substring(1, usergroupid.length()-1);
			  singleObv.put("usergroupid", usergroupid.split(","));
			  
			  urlresource=urlresource.substring(1, urlresource.length()-1);
			  singleObv.put("urlresource", urlresource.split(","));
			  
			  featurednotes=featurednotes.substring(1, featurednotes.length()-1);
			  singleObv.put("featurednotes", featurednotes.split(","));
			  
			  featuredgroups=featuredgroups.substring(1, featuredgroups.length()-1);
			  singleObv.put("featuredgroups", featuredgroups.split(","));
			  
			  String isdeletd=singleObv.get("isdeleted").toString();
			  
			  if(isdeletd.equalsIgnoreCase("f")){
				  singleObv.put("isdeleted", "false");
			  }
			  if(isdeletd.equalsIgnoreCase("t")){
				  singleObv.put("isdeleted", "true");
			  }
			  
			  String ischecklist=singleObv.get("ischecklist").toString();
			  if(ischecklist.equalsIgnoreCase("f")){
				  singleObv.put("ischecklist", "false");
			  }
			  if(ischecklist.equalsIgnoreCase("t")){
				  singleObv.put("ischecklist", "true");
			  }
			  
			  String isshowable=singleObv.get("isshowable").toString();
			  if(isshowable.equalsIgnoreCase("f")){
				  singleObv.put("isshowable", "false");
			  }
			  if(isshowable.equalsIgnoreCase("t")){
				  singleObv.put("isshowable", "true");
			  }
			  
			  String agreeterms=singleObv.get("agreeterms").toString();
			  if(agreeterms.equalsIgnoreCase("f")){
				  singleObv.put("agreeterms", "false");
			  }
			  if(agreeterms.equalsIgnoreCase("t")){
				  singleObv.put("agreeterms", "true");
			  }
			  
			  String geoprivacy=singleObv.get("geoprivacy").toString();
			  if(geoprivacy.equalsIgnoreCase("f")){
				  singleObv.put("geoprivacy", "false");
			  }
			  if(geoprivacy.equalsIgnoreCase("t")){
				  singleObv.put("geoprivacy", "true");
			  }
			  
			  String islocked=singleObv.get("islocked").toString();
			  if(islocked.equalsIgnoreCase("f")){
				  singleObv.put("islocked", "false");
			  }
			  if(islocked.equalsIgnoreCase("t")){
				  singleObv.put("islocked", "true");
			  }
			  
			  
			  data_to_elastic.add(singleObv);
			  postToElastic(data_to_elastic);
			
		}
		
		
		
	}

	private void postToElastic(List<Map<String, Object>> data_to_elastic) {
		// TODO Auto-generated method stub
		  CloseableHttpResponse response = null;
		  String url=config.getString("nakshaUrl")+"/services/bulk-upload/observation/observation";
		  try {
			HttpPost post = new HttpPost(url);
			String jsonData = objectMapper.writeValueAsString(data_to_elastic);
			System.out.println(jsonData);
			StringEntity entity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);
			post.setEntity(entity);
			CloseableHttpClient httpclient = HttpClients.createDefault();

			try {
				response = httpclient.execute(post, context);
				HttpEntity entity1 = response.getEntity();

				String responseString = EntityUtils.toString(entity1);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Error while trying to send request at URL {}", url);
				e.printStackTrace();
			}
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			logger.error("Error while trying to send request at URL {}", url);
			e.printStackTrace();
		}
		
	}






	private String getObservationJoinQuery(Long id) {
		
		String observationSql="SELECT obs.id AS id,"+
	            "obs.version AS version,"+
	            "obs.author_id AS authorid,"+
	            "su.name AS authorname,"+
	                "CASE "+
	                    "WHEN su.icon IS NULL THEN su.profile_pic "+
	                    "ELSE su.icon "+
	               "END AS authorprofilepic,"+
	            "obs.created_on AS createdon,"+
	            "obs.group_id AS speciesgroupid,"+
	            "sg.name AS speciesgroupname,"+
	            "obs.latitude AS latitude,"+
	            "obs.longitude AS longitude,"+
	            "obs.notes AS notes,"+
	            "obs.from_date AS fromdate,"+
	            "obs.place_name AS placename,"+
	            "obs.rating AS rating,"+
	            "obs.reverse_geocoded_name AS reversegeocodedname,"+
	            "obs.flag_count AS flagcount,"+
	            "obs.geo_privacy AS geoprivacy,"+
	            "obs.habitat_id AS habitatid,"+
	            "h.name AS habitatname,"+
	            "obs.is_deleted AS isdeleted,"+
	            "obs.last_revised AS lastrevised,"+
	            "obs.location_accuracy AS locationaccuracy,"+
	            "obs.visit_count AS visitcount,"+
	            "obs.search_text AS searchtext,"+
	            "obs.max_voted_reco_id AS maxvotedrecoid,"+
	            "obs.agree_terms AS agreeterms,"+
	            "obs.is_checklist AS ischecklist,"+
	            "obs.is_showable AS isshowable,"+
	            "obs.source_id AS sourceid,"+
	            "obs.to_date AS todate,"+
	            "obs.topology AS topology,"+
	            "obs.checklist_annotations AS checklistannotations,"+
	            "obs.feature_count AS featurecount,"+
	            "obs.is_locked AS islocked,"+
	            "obs.license_id AS licenseid,"+
	            "ll.name AS licensename,"+
	            "obs.language_id AS languageid,"+
	            "l.name AS languagename,"+
	            "obs.location_scale AS locationscale,"+
	            "obs.access_rights AS accessrights,"+
	            "obs.catalog_number AS catalognumber,"+
	            "obs.dataset_id AS datasetid,"+
	            "obs.external_dataset_key AS externaldatasetkey,"+
	            "obs.external_id AS externalid,"+
	            "obs.external_url AS externalurl,"+
	            "obs.information_withheld AS informationwithheld,"+
	            "obs.last_crawled AS lastcrawled,"+
	            "obs.last_interpreted AS lastinterpreted,"+
	            "obs.original_author AS originalauthor,"+
	            "obs.publishing_country AS publishingcountry,"+
	            "obs.repr_image_id AS reprimageid,"+
	            "obs.via_code AS viacode,"+
	            "obs.via_id AS viaid,"+
	            "obs.protocol AS protocol,"+
	            "obs.basis_of_record AS basisofrecord,"+
	            "obs.no_of_images AS noofimages,"+
	            "obs.no_of_videos AS noofvideos,"+
	            "obs.no_of_audio AS noofaudio,"+
	            "obs.no_of_identifications AS noofidentifications,"+
	            "r.taxon_concept_id AS taxonconceptid,"+
	            "r.accepted_name_id AS acceptednameid,"+
	            "t.canonical_form AS taxonomycanonicalform,"+
	            "t.status as status,"+
	            	"CASE "+
	                    " WHEN t. normalized_form IS NULL THEN r.name "+
	                    "ELSE  t. normalized_form "+
	                "END AS name,"+
	            "t.position AS position,"+
	            "t.rank AS rank,"+
	            "resp.file_name AS thumbnail, "+
	            "array_remove(array_agg(DISTINCT ug.id), NULL \\:\\: bigint) AS usergroupid, "+
	            "array_remove(array_agg(DISTINCT ug.name), NULL \\:\\: character varying) AS usergroupname,"+
	            "array_remove(array_agg(DISTINCT res.file_name), NULL \\:\\: character varying) AS imageresource,"+
	            "array_remove(array_agg(DISTINCT res.url), NULL \\:\\: character varying) AS urlresource,"+
	            "array_remove(array_agg(DISTINCT f.user_group_id), NULL \\:\\: bigint) AS featuredgroups,"+
	            "array_remove(array_agg(DISTINCT f.notes), NULL \\:\\: character varying) AS featurednotes "+
	           "FROM observation obs "+
	           "LEFT JOIN observation_resource obvres ON obs.id = obvres.observation_id "+
	           "LEFT JOIN resource resp ON obs.repr_image_id = resp.id "+
	           "LEFT JOIN resource res ON obvres.resource_id = res.id "+
	           "LEFT JOIN language l ON obs.language_id = l.id "+
	           "LEFT JOIN suser su ON obs.author_id = su.id "+
	           "LEFT JOIN habitat h ON obs.habitat_id = h.id "+
	           "LEFT JOIN species_group sg ON obs.group_id = sg.id "+
	           "LEFT JOIN license ll ON obs.license_id = ll.id "+
	           "LEFT JOIN recommendation r ON obs.max_voted_reco_id = r.id "+
	           "LEFT JOIN taxonomy_definition t ON r.taxon_concept_id = t.id "+
	           "LEFT JOIN user_group_observations ugo ON obs.id = ugo.observation_id "+
	           "LEFT JOIN user_group ug ON ug.id = ugo.user_group_id "+
	           "LEFT JOIN featured f ON f.object_id = obs.id "+
	           "WHERE obs.is_deleted=false and obs.id="+id +
	          " GROUP BY obs.id, su.name, su.icon, su.profile_pic, sg.name, h.name, ll.name, l.name, t.canonical_form,t.normalized_form , r.name,r.taxon_concept_id,"+
	          "r.accepted_name_id,t.status, t.position, t.rank, resp.file_name";
		
		return observationSql;
	}






	private Map<String, Object> getCustomFieldsMap(List<Map<String, Object>> custom_fields_one_obv) {
		Map<String, Object> custom_fields=new HashMap<String,Object>();
		for(Map<String,Object> custom_field:custom_fields_one_obv ){
			  String allowedMultiple=custom_field.get("allowedMultiple").toString();
			  String id=custom_field.get("id").toString();
			  String value=custom_field.get("value").toString();
			  String options=null;
			  if(custom_field.get("options")!=null){
				  System.out.println(custom_field.get("options").toString());
				 options=custom_field.get("options").toString();
			  }
			  String dataType=custom_field.get("dataType").toString();
			  String key=custom_field.get("key").toString();
			  if(dataType.equalsIgnoreCase("DATE")){
				  DateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  DateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				  
				  try {
					value= output.format(input.parse(value));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			  }
			  Map<String, Object> keyValues=new HashMap<String,Object>();
			  
			  if(allowedMultiple.equalsIgnoreCase("true") && options!=null ){
					keyValues.put("value",value.split(","));
				    keyValues.put("key",key);
				    custom_fields.put(id,keyValues);
				  
			  }
			 else{
				 	keyValues.put("value",value);
			        keyValues.put("key",key);
			        custom_fields.put(id,keyValues);
				  }
			  
		  }
		
		return custom_fields;
	}






	private String traitSeasonDate(Long id) {
		// TODO Auto-generated method stub
		 String traitSeasonDateQuery=
				    "select g.traits from ( "+
				  	"select "+
				  	 "x.object_id,  '[' || string_agg(format('{%s:%s}', to_json(x.tid), to_json(x.dates)), ',') || ']' as traits "+
				  	 "from ( "+
				  	 "select f.object_id,t.id as tid, ARRAY[f.from_date,f.to_date] as dates from fact f, trait t "+
				  	 "where f.trait_instance_id = t.id and (t.data_types='DATE') and (t.units='MONTH')  and f.object_type='species.participation.Observation' "+
				  	 ") x group by x.object_id "+
				  	 ") g where g.object_id="+id;
		return traitSeasonDateQuery;
	}






	private String getTraitDateQuery(Long id) {
		// TODO Auto-generated method stub
		 String traitDateQuery=
				  "select g.traits from ( "+
					"select "+
					 "x.object_id,  '[' || string_agg(format('{%s:%s}', to_json(x.tid), to_json(x.dates)), ',') || ']' as traits "+
					 "from ( "+
					"select f.object_id,t.id as tid, ARRAY[f.from_date,f.to_date] as dates from fact f, trait t "+
					 "where f.trait_instance_id = t.id and (t.data_types='DATE') and (t.units!='MONTH') and f.object_type='species.participation.Observation' "+
					") x group by x.object_id "+
					") g where g.object_id="+id;
		return traitDateQuery;
	}






	private String getTraitColorQuery(Long id) {
		// TODO Auto-generated method stub

		  String traitColorQuery=
				  "select g.traits from ( "+
					"select "+
					 "x.object_id,  '[' || string_agg(format('{%s:%s}', to_json(x.tid), to_json(x.tvalues)), ',') || ']'  as traits "+
					 "from ( "+
					 "select f.object_id,t.id as tid,  array_agg(DISTINCT f.value) AS tvalues from fact f, trait t "+
					 "where  f.trait_instance_id = t.id and (t.data_types='COLOR')  and f.object_type='species.participation.Observation'  group by f.object_id,t.id "+
				") x group by x.object_id "+
				") g where g.object_id="+id;
		return traitColorQuery;
	}






	private String getCustomFieldTableQuery(Long id) {
		// TODO Auto-generated method stub
		 String customFieldTableQuery="select table_name from information_schema.tables where table_name like 'custom_fields_group%' ";
		return customFieldTableQuery;
	}






	private String getQueryForPathAndClassification(Long id) {
		// TODO Auto-generated method stub
		 String queryForPathAndClassification="select obv.id,tres.path,tres.classification_id as classificationid from observation obv "+
                 "left join recommendation r on obv.max_voted_reco_id=r.id "+
                 "left join taxonomy_definition t on r.taxon_concept_id=t.id "+
                 "left join taxonomy_registry tres on tres.taxon_definition_id=t.id "+
                  "where (tres.classification_id=265799 or tres.classification_id=null) "+
                  "and obv.id="+id;
		return queryForPathAndClassification;
	}






	private String getTraitKeyValueQuery(Long id) {
		// TODO Auto-generated method stub
		 String traitKeyValueQuery ="select g.traits  from ( "+
				  	"select "+
				  	"  x.object_id ,  '[' || string_agg(format('{%s:%s}', to_json(x.tid), to_json(x.tvalues)), ',') || ']'  as traits "+
				  	"from ( "+
				  	"select f.object_id, t.id as tid ,json_agg(DISTINCT tv.value) AS tvalues from fact f, trait t, trait_value tv "+
				        " where f.trait_instance_id = t.id and f.trait_value_id = tv.id and f.object_type='species.participation.Observation'  group by f.object_id,t.id "+
				  	" ) x group by x.object_id "+
				  	") g where g.object_id="+id;
		return traitKeyValueQuery;
	}






	private String getTraitRangeQueryString(Long id) {
		// TODO Auto-generated method stub
		String traitRangeNumericQuery=
				 " select g.traits from ( "+
					"select "+
					" x.object_id,  '[' || string_agg(format('{%s:%s}', to_json(x.tid), to_json(x.tvalues)), ',') || ']' as traits "+
					"from ( "+
					"select  f.object_id, t.id as tid,ARRAY[f.value,f.to_value] as tvalues from fact f, trait t "+
					"where f.trait_instance_id = t.id and (t.data_types='NUMERIC') and f.object_type='species.participation.Observation' "+
				") x group by x.object_id "+
				") g where g.object_id="+id;
		return traitRangeNumericQuery;
	}

}
