package biodiv.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.observation.ObservationListService;
import biodiv.user.UserController;

@Path("/admin")
public class AdminController {
	
	private final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Inject
	ObservationListService observationListService;
	@Inject
	AdminService adminService;
	
	/**
	 * 
	 * @param index
	 * The this going to allow admin to upload setting and mapping 
	 */
	@GET
	@Path("download")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public void downloadFile(){
		
		adminService.downloadFile();
      
	}

}
