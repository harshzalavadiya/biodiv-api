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

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.auth.AuthUtils;
import biodiv.taxon.service.TaxonService;

@Path("/trait")

public class TraitController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String IBP = "IBP Taxonomy Hierarchy";

	@Inject
	private TraitService traitService;

	@Inject
	private TaxonService taxonService;

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
			classificationId = taxonService.classificationIdByName(IBP);
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
	public List<TraitValue> getTraitValue(@PathParam("id") Long id) {
		List<TraitValue> results = traitService.getTraitValue(id);
		return results;
	}

}
