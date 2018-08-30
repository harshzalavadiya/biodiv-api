package biodiv.traits;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.auth.AuthUtils;
import biodiv.common.Language;
import biodiv.common.LanguageService;
import biodiv.taxon.service.TaxonService;

@Path("/trait")

public class TraitController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String WIKWIO = "WIKWIO Taxonomy Hierarchy";

	@Inject
	private TraitService traitService;

	@Inject
	private TaxonService taxonService;
	
	@Inject 
	private LanguageService languageService;
	
	

	/**
	 * 
	 * @param sGroup
	 *            dummy
	 * @param classificationId
	 *            dummy
	 * @param isNotObservationTrait
	 *            dummy
	 * @param showInObservation
	 *            dummy
	 * @param objectType
	 *            dummy
	 * @param objectId
	 *            dummy
	 * @return dummy
	 */

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public List<TraitFactUi> list(@QueryParam("sGroup") Long sGroup,
			@QueryParam("classification") Long classificationId,
			@DefaultValue("false") @QueryParam("isNotObservationTrait") Boolean isNotObservationTrait,
			@DefaultValue("true") @QueryParam("showInObservation") Boolean showInObservation,
			@QueryParam("objectType") String objectType, @QueryParam("objectId") Long objectId) {
		if (classificationId == null) {
			classificationId = taxonService.classificationIdByName(WIKWIO);
		}
		List<TraitFactUi> traitList = traitService.list(objectId, objectType, sGroup, classificationId,
				isNotObservationTrait, showInObservation);
		return traitList;
	}

	/**
	 * 
	 * @param id
	 *            dummy
	 * @param objectType
	 *            dummy
	 * @return dummy
	 */
	@GET
	@Path("/observation/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public List<Fact> slist(@PathParam("id") Long id,
			@DefaultValue("species.participation.Observation") @QueryParam("objectType") String objectType) {

		List<Fact> traitList = traitService.slist(id, objectType);
		return traitList;
	}

	/**
	 * 
	 * @param trait
	 *            dummy
	 * @param traitId
	 *            dummy
	 * @param objectId
	 *            dummy
	 * @param objectType
	 *            dummy
	 * @param profile
	 *            dummy
	 * @return dummy
	 */
	@POST
	@Path("/fact/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@Pac4JSecurity(clients = "cookieClient,headerClient", authorizers = "isAuthenticated")
	public Serializable list(@QueryParam("traits") String trait, @QueryParam("traitId") Long traitId,
			@QueryParam("objectId") Long objectId, @QueryParam("objectType") String objectType,
			@Context HttpServletRequest request) {

		CommonProfile profile = AuthUtils.currentUser(request);
		String[] arr = trait.trim().split(",");

		Set<Long> traits = null;

		if (trait != null) {
			traits = new HashSet<Long>();
			for (String data : arr) {
				traits.add(Long.parseLong(data));
			}
		}

		Serializable response = traitService.updateFact(traits, traitId, objectId, objectType, profile);
		return response;
	}

	/**
	 * 
	 * @param id
	 *            dummy
	 * @return dummy
	 */

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Trait list(@PathParam("id") Long id) {
		Trait result = traitService.getSingleTrait(id);
		return result;
	}

	/**
	 * 
	 * @param id
	 *            dummy
	 * @return dummy
	 */
	@GET
	@Path("fact/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Fact listFact(@PathParam("id") Long id) {
		Fact result = traitService.listFact(id);
		return result;

	}

	@GET
	@Path("observation/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public List<Trait> listObservationTrait() {
		List<Trait> results = traitService.listObservationTrait();
		return results;
	}

	@GET
	@Path("/traitvalue/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public List<TraitValueTranslation> getTraitValue(@PathParam("id") Long id,@QueryParam("lan") String lan) {
		int length=lan.length();
		Language language=new Language();
		if(length==2){
			 language=languageService.findByTwoLetterCode(lan);
		}
		else if(length==3){
			language=languageService.findByThreeLetterCode(lan);
		}
		else{
			log.error("invalid size of string lenght");
		}
		
		if(language!=null){
			List<TraitValueTranslation> results = traitService.getTraitValue(id,language.getId());
			return results;
		}
		return null;
		
	}
	@GET
	@Path("/species/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response getAllTraits(@QueryParam("lan") String lan) {
		int length=lan.length();
		Language language=new Language();
		if(length==2){
			 language=languageService.findByTwoLetterCode(lan);
		}
		else if(length==3){
			language=languageService.findByThreeLetterCode(lan);
		}
		else{
			log.error("invalid size of string lenght");
		}
		
		if(language!=null){
			List<TraitTranslation> results = traitService.getAllTraits(language.getId());
			return Response.ok(results).build();
		}

			return Response.status(Response.Status.NOT_FOUND).entity("langauge not found for 3 char status code "+ lan).build();
			
	
		
		
	}

}
