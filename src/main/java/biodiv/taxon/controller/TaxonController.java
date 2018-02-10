package biodiv.taxon.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.elasticsearch.search.suggest.Suggest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Intercept;
import biodiv.taxon.datamodel.dao.Classification;
import biodiv.taxon.datamodel.ui.TaxonRelation;
import biodiv.taxon.service.TaxonService;

@Path("/taxon")
public class TaxonController {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String IBP = "IBP Taxonomy Hierarchy";
	TaxonService taxonService = new TaxonService();

	/**
	 * 
	 * @param parent
	 * dummy
	 * @param classificationId
	 * dummy
	 * @param taxonIds
	 * dummy
	 * @param expand_taxon
	 * dummy
	 * @return 
	 * dummy
	 * method is responsible for displaying taxon list
	 */
	@GET
	@Path("/list")
	@Intercept
	@Produces(MediaType.APPLICATION_JSON)
	public List<TaxonRelation> list(@QueryParam("parent") Long parent,
			@QueryParam("classification") Long classificationId,
			@QueryParam("taxonIds") String taxonIds,
			@DefaultValue("false") @QueryParam("expand_taxon") Boolean expand_taxon) {

		Set<String> taxonID = null;
		
		if (taxonIds != null) {
			taxonID = new HashSet<String>();
			String[] taxonId = taxonIds.split(",");
			for (String data : taxonId) {
				taxonID.add(data);
			}
		}
		if(classificationId==null){
			
			classificationId=taxonService.classificationIdByName(IBP);
		}
		List<TaxonRelation> taxons = new ArrayList<TaxonRelation>();
		if(parent != null) {
			taxons = taxonService.list(parent, classificationId);
		} else if(taxonID != null && expand_taxon == true) {
			taxons = taxonService.list(taxonID, classificationId);
		}
		else{
			taxons = taxonService.list(classificationId);
		}
		return taxons;

	}
	
	/**
	 * 
	 * @param term
	 * dummy
	 * @return
	 * dummy
	 */
	
	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Intercept
	public Object search(@QueryParam("term") String term){
        
		 Object name=taxonService.search(term);
		 return name;
		
	}
	
	/**
	 * 
	 * @param term
	 * dummy
	 * @param classificationId
	 * dummy
	 * @param taxonid
	 * dummy
	 * @return
	 * dummy
	 */
	
	@GET
	@Path("/retrieve/specificSearch")
	@Produces(MediaType.APPLICATION_JSON)
	@Intercept
	public Set<String> specificSearch(@QueryParam("term") String term,
		@QueryParam("classification") Long classificationId,
		@QueryParam("taxonid") Long taxonid) {
		if(classificationId==null){
			classificationId=taxonService.classificationIdByName(IBP);
		}
		if(taxonid==null){
			Set<String> data = taxonService.specificSearch(classificationId, term,null);
			return data;
		}
		Set<String> data = taxonService.specificSearch(classificationId, term,taxonid);
		return data;
		
		
		
	}
	
	/**
	 * 
	 * @return 
	 * By default return the list of all classification 
	 */
	
	@GET
	@Path("/classification/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Intercept
	public List<Classification> classification() {
		List<Classification> data = taxonService.classification();
		return data;
	}
	/**
	 * 
	 * @param name
	 * dummy
	 * @return 
	 * find classificationId corresponding to a particular name
	 */
	
	@GET
	@Path("/classification/list/{name}")
	@Intercept
	@Produces(MediaType.APPLICATION_JSON)
	public Long classificationIdByName(@PathParam("name") String name) {
		Long data = taxonService.classificationIdByName(name);
		return data;
	}

}
