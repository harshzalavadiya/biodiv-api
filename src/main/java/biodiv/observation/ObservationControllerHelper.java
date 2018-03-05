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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import biodiv.common.CommonMethod;
import biodiv.maps.MapAndBoolQuery;
import biodiv.maps.MapAndRangeQuery;
import biodiv.maps.MapExistQuery;
import biodiv.maps.MapOrBoolQuery;
import biodiv.maps.MapOrRangeQuery;
import biodiv.maps.MapSearchQuery;

public class ObservationControllerHelper {

	public static MapSearchQuery getMapSearchQuery(
			String sGroup, String taxon,
			String user,
			String userGroupList,
			String webaddress,
			String speciesName,
			String mediaFilter,
			String months,
			String isFlagged,

			String sortOn,

			String minDate, String maxDate,
			String validate,

			String trait_8, String trait_9,
			String trait_10, String trait_11,
			String trait_12, String trait_13,
			String trait_15,

			String classificationid,
			Integer max, Integer offset
) {
		List<MapAndBoolQuery> boolAndLists = new ArrayList<MapAndBoolQuery>();

		List<MapOrBoolQuery> boolOrLists = new ArrayList<MapOrBoolQuery>();

		List<MapOrRangeQuery> rangeOrLists = new ArrayList<MapOrRangeQuery>();
		List<MapAndRangeQuery> rangeAndLists = new ArrayList<MapAndRangeQuery>();

		List<MapExistQuery> andMapExistQueries = new ArrayList<MapExistQuery>();

		if(offset == null) {
			offset = 0;
		}
		if(max == null) {
			max = 10;
		}
		if(classificationid == null) {
			classificationid = "265799";
		}
		
		CommonMethod commonMethod = new CommonMethod();

		Set<String> groupId = commonMethod.cSTSOT(sGroup);
		if (!groupId.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("speciesgroupid", groupId));
		}

		Set<String> taxonId = commonMethod.cSTSOT(taxon);
		if (!taxonId.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("path", taxonId));
		}

		Set<String> authorId = commonMethod.cSTSOT(user);

		if (!authorId.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("authorid", authorId));
		}

		Set<String> userGroupId = commonMethod.cSTSOT(userGroupList);
		if (!userGroupId.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("usergroupid", userGroupId));
		}

		Set<String> userGroupName = commonMethod.cSTSOT(webaddress);
		if (!userGroupName.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("usergroupname", userGroupName));

		}

		Set<String> month = commonMethod.cSTSOT(months);
		if (!month.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("frommonth", month));

		}

		Set<String> speciesNames = commonMethod.cSTSOT(speciesName);
		if (!speciesNames.isEmpty()) {
			if (speciesNames.size() < 2) {
				String first = (String) speciesNames.toArray()[0];
				if (first.equalsIgnoreCase("UNIDENTIFED")) {
					andMapExistQueries.add(new MapExistQuery("name", false));
				}
				if (first.equalsIgnoreCase("IDENTIFED")) {
					andMapExistQueries.add(new MapExistQuery("name", true));
				}
			}

		}
		Set<String> validates = commonMethod.cSTSOT(validate);
		if (!validates.isEmpty()) {
			if (validates.size() < 2) {
				String first = (String) validates.toArray()[0];
				if (first.equalsIgnoreCase("invalidate")) {
					Set<String> data = new HashSet<>();
					data.add("false");
					boolAndLists.add(new MapAndBoolQuery("islocked", data));
				}
				if (first.equalsIgnoreCase("validate")) {
					Set<String> data = new HashSet<>();
					data.add("true");
					boolAndLists.add(new MapAndBoolQuery("islocked", data));
				}
			}

		}

		/**
		 * Date Filter
		 */

		String minDateValue = null;
		String maxDateValue = null;

		Date date = new Date();
		SimpleDateFormat in = new SimpleDateFormat("EEE MMM dd YYYY HH:mm:ss");
		SimpleDateFormat out = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");
		String minDates = null;
		String maxDates = null;
		try {

			if (minDate != null) {
				minDates = java.net.URLDecoder.decode(minDate, "UTF-8");

				minDateValue = out.format(in.parse(minDates));
			}
			if (maxDate != null) {
				maxDates = java.net.URLDecoder.decode(maxDate, "UTF-8");
				maxDateValue = out.format(in.parse(maxDates));
			}

		} catch (UnsupportedEncodingException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (minDateValue != null && maxDateValue != null) {

			rangeAndLists.add(new MapAndRangeQuery("fromdate", minDateValue, maxDateValue));
		}
		if (minDateValue != null && maxDateValue == null) {
			rangeAndLists.add(new MapAndRangeQuery("fromdate", minDateValue, out.format(date)));
		}
		if (minDateValue == null && maxDateValue != null) {
			rangeAndLists.add(new MapAndRangeQuery("fromdate", out.format(date), maxDateValue));
		}

		/**
		 * General conditions
		 * 
		 * General condition
		 */

		String isDeleted = "false";
		Set<String> isdeleted = commonMethod.cSTSOT(isDeleted);
		if (!isdeleted.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("isdeleted", isdeleted));

		}
		String isCheckList = "false";
		Set<String> ischecklist = commonMethod.cSTSOT(isCheckList);
		if (!ischecklist.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("ischecklist", ischecklist));

		}
		// String isshowables = "true";
		// Set<String> isshowable = commonMethod.cSTSOT(isshowables);
		// if (!isshowable.isEmpty()) {
		// boolAndLists.add(new MapAndBoolQuery("isshowable", isshowable));
		//
		// }

		// Set<String> isDataSetId=new HashSet<String>();
		// isDataSetId.add(null);
		/**
		 * we need to get the observation only those have dataidkey set to null
		 */

		// boolAndLists.add(new MapAndBoolQuery("datasetid", null));

		/**
		 * ##########################################################################
		 * Range Querues
		 */
		Set<String> mediaFilters = commonMethod.cSTSOT(mediaFilter);
		if (!mediaFilters.isEmpty()) {
			// remove no media value
			for (String filter : mediaFilters) {
				rangeOrLists.add(new MapOrRangeQuery(filter, 1, Long.MAX_VALUE));
			}

		}

		Set<String> flagged = commonMethod.cSTSOT(isFlagged);
		if (!flagged.isEmpty()) {

			if (flagged.size() < 2) {
				String first = (String) flagged.toArray()[0];
				if (first.equalsIgnoreCase("1")) {
					rangeAndLists.add(new MapAndRangeQuery("flagcount", first, Long.MAX_VALUE));
				}
				if (first.equalsIgnoreCase("0")) {
					rangeAndLists.add(new MapAndRangeQuery("flagcount", first, first));
				}

			}
		}
		/**
		 * Filter to implements the traits the traits filter is boolAndQuertype
		 * filters
		 */
		Set<String> traits_8 = commonMethod.cSTSOT(trait_8);
		if (!traits_8.isEmpty()) {
			System.out.print(traits_8);
			boolAndLists.add(new MapAndBoolQuery("trait_8", traits_8));
		}

		Set<String> traits_9 = commonMethod.cSTSOT(trait_9);
		if (!traits_9.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("trait_9", traits_9));
		}
		Set<String> traits_10 = commonMethod.cSTSOT(trait_10);
		if (!traits_10.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("trait_10", traits_10));
		}
		Set<String> traits_11 = commonMethod.cSTSOT(trait_11);
		if (!traits_11.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("trait_11", traits_11));
		}
		Set<String> traits_12 = commonMethod.cSTSOT(trait_12);
		if (!traits_12.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("trait_12", traits_12));
		}
		Set<String> traits_13 = commonMethod.cSTSOT(trait_13);
		if (!traits_13.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("trait_13", traits_13));
		}
		Set<String> traits_15 = commonMethod.cSTSOT(trait_15);
		if (!traits_15.isEmpty()) {
			boolAndLists.add(new MapAndBoolQuery("trait_15", traits_15));
		}
		/**
		 * lft, right, top bottom
		 */

		/**
		 * combine all the queries
		 * 
		 */

		MapSearchQuery mapSearchQuery = new MapSearchQuery(boolAndLists, boolOrLists, rangeAndLists, rangeOrLists,
				andMapExistQueries);

		return mapSearchQuery;
	}
}
