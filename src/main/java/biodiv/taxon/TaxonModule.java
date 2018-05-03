package biodiv.taxon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import biodiv.taxon.controller.TaxonController;
import biodiv.taxon.dao.TaxonDao;
import biodiv.taxon.datamodel.dao.AcceptedSynonyms;
import biodiv.taxon.datamodel.dao.Classification;
import biodiv.taxon.datamodel.dao.ExternalLinks;
import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.taxon.datamodel.dao.TaxonomyRegistry;
import biodiv.taxon.datamodel.ui.TaxonRelation;
import biodiv.taxon.search.SearchTaxon;
import biodiv.taxon.service.TaxonService;

public class TaxonModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring TaxonModule Servlets");
		
		bind(Taxon.class);
		bind(TaxonDao.class).in(Singleton.class);
		bind(SearchTaxon.class).in(Singleton.class);
		bind(TaxonService.class).in(Singleton.class);
		bind(TaxonController.class).in(Singleton.class);
		
		bind(Classification.class);
		bind(AcceptedSynonyms.class);
		bind(ExternalLinks.class);
		bind(TaxonomyRegistry.class);
		
		bind(TaxonRelation.class);
	}
}
