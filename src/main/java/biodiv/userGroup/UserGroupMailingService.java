package biodiv.userGroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import biodiv.mail.AbstractHtmlMailingService;
import biodiv.mail.MailProvider;
import biodiv.observation.Observation;
import biodiv.user.User;



public class UserGroupMailingService extends AbstractHtmlMailingService {
	
	@Inject
	freemarker.template.Configuration cfg;
	
	//final String subject = "Download request";
	final String message = "Your data download request has been processed. The download link will be visible once you log in to your profile."; 
	@Inject
	public UserGroupMailingService(Configuration config, MailProvider mailProvider) throws EmailException {
		super(mailProvider, config, "mail.download.subject", "mail.download.message");
		System.out.println("****************************************************8 "+config);
		System.out.println("****************************************************8 "+mailProvider);
	}
	
	public HtmlEmail buildUserGroupPostMailMessage(String toEmail,User follower,User postingUser,Observation obv,
			Set<UserGroup> userGroupsToBePostedIn,String submitType) throws Exception{
		
		try{
		String subject;
		if(submitType.equalsIgnoreCase("post")){
			subject = "Posted observation to group";
		}else{
			subject = "Removed observation from group";
		}
		freemarker.template.Template temp = cfg.getTemplate("postToGroupMailTemp.ftlh");
		PostToGroupsMailDataModel dm = new PostToGroupsMailDataModel(follower,postingUser,obv,userGroupsToBePostedIn,submitType);
		Map<String,Object> root = dm.getRoot();
		HtmlEmail email = buildMessage(subject,this.message,toEmail,temp,root);
		return email;
		}catch(Exception e){
			throw e;
		}
	}
	
}
