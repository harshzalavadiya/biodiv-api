package biodiv.common;

import java.util.Date;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.vividsolutions.jts.geom.Geometry;

import biodiv.dataset.Dataset;

@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Metadata {

	public enum LocationScale {
		APPROXIMATE ("Approximate"),
		ACCURATE ("Accurate"),
		LOCAL ("Local"),
		REGION ("Region"),
		COUNTRY ("Country");
		
		private String value;

		LocationScale(String value) {
			this.value = value;
		}

		String value() {
			return this.value;
		}
		
		static LocationScale getEnum(String value){
			if(value ==null || value.isEmpty()) return null;

			value = value.toUpperCase().trim();

			switch(value){
				case "APPROXIMATE":
					return LocationScale.APPROXIMATE;
				case "ACCURATE":
					return LocationScale.ACCURATE;
				case "LOCAL":
					return LocationScale.LOCAL;
				case "REGION":
					return LocationScale.REGION;
				case "COUNTRY":
					return LocationScale.COUNTRY;
				default:
					return null;
			}
		}
	}
	
	//Geographic Coverage
	String placeName;
	String reverseGeocodedName;
	
	boolean geoPrivacy = false;
	//XXX to be removed after locationScale migration
	String locationAccuracy;
	LocationScale locationScale;
    Geometry topology;
     
	double latitude;
	double longitude;
	
    //Taxonomic Coverage
    SpeciesGroup group;
	Habitat habitat;

    //Temporal Coverage
	Date fromDate;
	Date toDate;
	
    Date createdOn = new Date();
	Date lastRevised = createdOn;

    // Language
    Language language;

	License license;

    String externalId;
    String externalUrl;
    String viaId;
    String viaCode;

    Date lastInterpreted;
    Date lastCrawled;

    Dataset dataset;


}
