package biodiv.license;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.License;
import biodiv.user.UserController;
@Path("/common")
public class LicenseController {

	private final Logger log = LoggerFactory.getLogger(UserController.class);
	
	LicenseService commonService = new LicenseService();
	@GET
	@Path("licence/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public License findByName(@PathParam("name") String name){
		License license=commonService.findByName(name);
		return null;
		
	}
}
