package biodiv.comment;

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
import biodiv.userGroup.PostToGroupsMailDataModel;



public class CommentMailingService extends AbstractHtmlMailingService {
	
	@Inject
	freemarker.template.Configuration cfg;
	
	@Inject
	Configuration config;
	
	//final String subject = "Download request";
	final String message = "Your data download request has been processed. The download link will be visible once you log in to your profile."; 
	@Inject
	public CommentMailingService(Configuration config, MailProvider mailProvider) throws EmailException {
		super(mailProvider, config, "mail.download.subject", "mail.download.message");
		System.out.println("****************************************************8 "+config);
		System.out.println("****************************************************8 "+mailProvider);
	}
	public HtmlEmail buildTaggingMailMessage(String toEmail, User taggedUser, User whoTaggedThem, Observation obv,
			String comment) throws Exception {
		try{
			String subject = "Tagged in observation comment";
			freemarker.template.Template temp = cfg.getTemplate("taggedMailTemp.ftlh");
			TaggedMailDataModel dm = new TaggedMailDataModel(taggedUser,whoTaggedThem,obv,comment,config);
			Map<String,Object> root = dm.getRoot();
			HtmlEmail email = buildMessage(subject,this.message,toEmail,temp,root);
     		return email;
		}catch(Exception e){
			throw e;
		}
		
	}
	public HtmlEmail buildCommentPostMailMessage(String toEmail, User follower, User postingUser, Observation obv,
			String commentBody) throws Exception {
		
		try{
			String subject = "New comment in observation";
			freemarker.template.Template temp = cfg.getTemplate("commentPostMailTemp.ftlh");
			CommentPostMailDataModel dm = new CommentPostMailDataModel(follower,postingUser,obv,commentBody,config);
			Map<String,Object> root = dm.getRoot();
			HtmlEmail email = buildMessage(subject,this.message,toEmail,temp,root);
			return email;
			}catch(Exception e){
				throw e;
			}
	}
	
}
