package biodiv.observation;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;
import com.google.inject.Inject;

import biodiv.Transactional;
import biodiv.auth.AuthUtils;
import biodiv.common.CommonMethod;
import biodiv.maps.MapAndBoolQuery;
import biodiv.maps.MapAndRangeQuery;
import biodiv.maps.MapBiodivResponse;
import biodiv.maps.MapExistQuery;
import biodiv.maps.MapOrBoolQuery;
import biodiv.maps.MapOrRangeQuery;
import biodiv.maps.MapSearchQuery;
import biodiv.user.User;
import biodiv.user.UserService;

@Path("/download")
public class DownloadController {

	@Inject
	UserService userService;
//	@Inject
//	SchedulerService schedulerService;
	
	@GET
	@Path("/{index}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients = "cookieClient,headerClient", authorizers = "isAuthenticated")
	@Transactional
	public MapBiodivResponse download(@PathParam("index") String index, @PathParam("type") String type,
			@DefaultValue("") @QueryParam("sGroup") String sGroup, @DefaultValue("") @QueryParam("taxon") String taxon,
			@DefaultValue("") @QueryParam("user") String user,
			@DefaultValue("") @QueryParam("userGroupList") String userGroupList,
			@DefaultValue("") @QueryParam("webaddress") String webaddress,
			@DefaultValue("") @QueryParam("speciesName") String speciesName,
			@DefaultValue("") @QueryParam("mediaFilter") String mediaFilter,
			@DefaultValue("") @QueryParam("months") String months,
			@DefaultValue("") @QueryParam("isFlagged") String isFlagged,

			@DefaultValue("lastrevised") @QueryParam("sort") String sortOn,

			@QueryParam("minDate") String minDate, @QueryParam("maxDate") String maxDate,
			@QueryParam("validate") String validate,

			@QueryParam("trait_8") String trait_8, @QueryParam("trait_9") String trait_9,
			@QueryParam("trait_10") String trait_10, @QueryParam("trait_11") String trait_11,
			@QueryParam("trait_12") String trait_12, @QueryParam("trait_13") String trait_13,
			@QueryParam("trait_15") String trait_15,

			@DefaultValue("1") @QueryParam("minDay") Integer minDay,
			@DefaultValue("31") @QueryParam("maxDay") Integer maxDay,
			@DefaultValue("265799") @QueryParam("classifdication") String classificationid,
			@DefaultValue("10") @QueryParam("max") Integer max, @DefaultValue("0") @QueryParam("offset") Integer offset,

			@DefaultValue("") @QueryParam("geoAggregationField") String geoAggregationField,
			@DefaultValue("1") @QueryParam("geoAggegationPrecision") Integer geoAggegationPrecision,

			@QueryParam("left") Double left, @QueryParam("right") Double right, @QueryParam("top") Double top,
			@QueryParam("bottom") Double bottom,
			@Context HttpServletRequest request

	) {

		CommonProfile profile = AuthUtils.currentUser(request);
		User suser = userService.findById(Long.parseLong(profile.getId()));
		
		MapSearchQuery mapSearchQuery = ObservationControllerHelper.getMapSearchQuery(sGroup, taxon, user, userGroupList, webaddress, speciesName, mediaFilter, months, isFlagged, sortOn, minDate, maxDate, validate, trait_8, trait_9, trait_10, trait_11, trait_12, trait_13, trait_15, classificationid, max, offset);

		return null;
	}
}
