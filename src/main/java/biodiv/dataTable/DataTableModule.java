package biodiv.dataTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;



public class DataTableModule extends ServletModule {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring ObservationModule Servlets");
		
		
		bind(DataTable.class);
		bind(DataTableDao.class).in(Singleton.class);
		bind(DataTableService.class).in(Singleton.class);
		//bind(DataTableController.class).in(Singleton.class);
		
		
		
	}
}
