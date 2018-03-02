package biodiv.observation;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import biodiv.common.CommonMethod;
import biodiv.maps.MapBiodivResponse;
import biodiv.maps.MapExistQuery;
import biodiv.maps.MapAndBoolQuery;
import biodiv.maps.MapHttpResponse;
import biodiv.maps.MapOrBoolQuery;
import biodiv.maps.MapOrRangeQuery;
import biodiv.maps.MapAndRangeQuery;
import biodiv.maps.MapResponse;
import biodiv.maps.MapSearchQuery;

@Path("/naksha")
public class ObservationListController {

	@POST
	@Path("/{index}/{type}/{documentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public MapResponse list(@PathParam("index") String index, @PathParam("type") String type,
			@PathParam("documentId") String documentId, String document) {

		ObservationList observationList = new ObservationList();

		MapResponse mapResponse = observationList.create(index, type, documentId, document);

		return mapResponse;
	}

	/**
	 * Call to update a particular document with given id and document
	 * 
	 * @param index
	 * @param type
	 * @param documentId
	 * @param document
	 * @return
	 */

	@PUT
	@Path("/{index}/{type}/{documentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public MapResponse updateDocument(@PathParam("index") String index, @PathParam("type") String type,
			@PathParam("documentId") String documentId, String document) {

		ObservationList observationList = new ObservationList();

		MapResponse mapResponse = observationList.update(index, type, documentId, document);

		return mapResponse;
	}

	@GET
	@Path("/{index}/{type}/{documentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public MapHttpResponse list(@PathParam("index") String index, @PathParam("type") String type,
			@PathParam("documentId") String documentId) {
		System.out.println(index);
		ObservationList observationList = new ObservationList();

		MapHttpResponse content = observationList.fetch(index, type, documentId);
		return content;
	}

	@GET
	@Path("/{index}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public MapBiodivResponse list(@PathParam("index") String index, @PathParam("type") String type,
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

			@DefaultValue("265799") @QueryParam("classifdication") String classificationid,
			@DefaultValue("10") @QueryParam("max") Integer max, @DefaultValue("0") @QueryParam("offset") Integer offset,

			@DefaultValue("") @QueryParam("geoAggregationField") String geoAggregationField,
			@DefaultValue("1") @QueryParam("geoAggegationPrecision") Integer geoAggegationPrecision,

			@QueryParam("left") Double left, @QueryParam("right") Double right, @QueryParam("top") Double top,
			@QueryParam("bottom") Double bottom,
			@QueryParam("onlyFilteredAggregation") Boolean onlyFilteredAggregation

	) {



		ObservationList observationList = new ObservationList();

		MapSearchQuery mapSearchQuery = ObservationControllerHelper.getMapSearchQuery(sGroup, taxon, user, userGroupList, webaddress, speciesName, mediaFilter, months, isFlagged, sortOn, minDate, maxDate, validate, trait_8, trait_9, trait_10, trait_11, trait_12, trait_13, trait_15, classificationid, max, offset);
		
		MapBiodivResponse mapResponse = observationList.search(index, type, mapSearchQuery , max, offset, sortOn,
				geoAggregationField, geoAggegationPrecision, left, right, top, bottom, onlyFilteredAggregation);

		return mapResponse;
	}

}
