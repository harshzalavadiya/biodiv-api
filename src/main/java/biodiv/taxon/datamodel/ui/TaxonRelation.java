package biodiv.taxon.datamodel.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

public class TaxonRelation {
	
	  
	    private long taxonid;
		private String path;
		private long parent;
		private String text;
		private long classification;
		private long id;
		private int rank;
		Set <String> ids=new HashSet<String>();
		
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
	    private List<TaxonRelation> children = new ArrayList<>();
		
	    public TaxonRelation(long taxonid, String path, long parent, String text, long classification, long id,
				int rank,Set<String> data) {
			super();
			this.taxonid = taxonid;
			this.path = path;
			this.parent = parent;
			this.text = text;
			this.classification = classification;
			this.id = id;
			this.rank = rank;
			this.ids=data;
		}

		public void addChild(TaxonRelation child) {
			children.add(child);
	    }
		
		public long getTaxonid() {
			return taxonid;
		}
		public void setTaxonid(long taxonid) {
			this.taxonid = taxonid;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public long getParent() {
			return parent;
		}
		public void setParent(long parent) {
			this.parent = parent;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public long getClassification() {
			return classification;
		}
		public void setClassification(long classification) {
			this.classification = classification;
		}
		public int getRank() {
			return rank;
		}
		public void setRank(int rank) {
			this.rank = rank;
		}

		public void setId(long id) {
			this.id = id;
		}
		public long getId() {
			return id;
		}
		public List<TaxonRelation> getChildren() {
				return children;
		}
		public void setChildren(List<TaxonRelation> children) {
				this.children = children;	
		}

		public Set<String> getIds() {
			return ids;
		}

		public void setIds(Set<String> ids) {
			this.ids = ids;
		}
	
}
