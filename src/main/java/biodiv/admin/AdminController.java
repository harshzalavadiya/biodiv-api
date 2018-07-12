package biodiv.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.observation.Observation;
import biodiv.observation.ObservationListService;
import biodiv.observation.ObservationService;
import biodiv.user.User;
import biodiv.user.UserController;
import biodiv.user.UserService;

@Path("/admin")
public class AdminController {

	private final Logger log = LoggerFactory.getLogger(UserController.class);

	@Inject
	ObservationListService observationListService;
	@Inject
	AdminService adminService;
	@Inject
	ObservationService observationService;
	
	@Inject
	UserService userService;

	/**
	 * 
	 * @param index
	 *            The this going to allow admin to upload setting and mapping
	 */
	@GET
	@Path("download")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public void downloadFile(@QueryParam("fileName") String fileName) {

		try {
			adminService.downloadFile(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@GET 
	@Path("publishObservationSearchIndex")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public void publishObservationSearchIndex(@QueryParam("obvIds") String obvIds){
		List<Observation> obvs=new ArrayList<Observation>();
		
		List<Long> longObvIds=Stream.of(obvIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
		for(Long id:longObvIds){
			obvs.add(observationService.show(id));
		}
		
		try {
			adminService.publishObservationSearchIndex(obvs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@GET 
	@Path("publishUserSearchIndex")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public void publishUserSearchIndex(@QueryParam("userIds") String userIds){
		List<User> userId=new ArrayList<User>();
		
		List<Long> longUserIds=Stream.of(userIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
		for(Long id:longUserIds){
			userId.add(userService.findById(id));
		}
		
		try {
			adminService.publishUserSearchIndex(userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
