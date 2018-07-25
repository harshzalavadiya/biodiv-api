package biodiv.observation;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import biodiv.mail.AbstractHtmlMailingService;
import biodiv.mail.MailProvider;
import biodiv.user.User;
import biodiv.userGroup.PostToGroupsMailDataModel;
import biodiv.userGroup.UserGroup;

public class ObservationMailingService extends AbstractHtmlMailingService{

	@Inject
	freemarker.template.Configuration cfg;
	
	@Inject
	public ObservationMailingService(Configuration config, MailProvider mailProvider) throws EmailException {
		super(mailProvider, config, "mail.download.subject", "mail.download.message");
	}
	
	@Inject
	Configuration config;
	
	public HtmlEmail buildSuggestIdMailMessage(String toEmail,User follower,User postingUser,Observation obv,
			RecommendationVote recoVote) throws Exception{
		
		try{
			String subject = "Species name suggested";
			freemarker.template.Template temp = cfg.getTemplate("suggestIdMailTemp.ftlh");
			Recommendation givenName;
			String recovote;
			if(recoVote.getRecommendationByRecommendationId()!=null){
				givenName = recoVote.getRecommendationByRecommendationId();
			}else{
				givenName = recoVote.getRecommendationByCommonNameRecoId();
			}
			if(recoVote.getGivenSciName()!=null){
				recovote = recoVote.getGivenSciName();
			}else{
				recovote = recoVote.getGivenCommonName();
			}
			SuggestIdMailDataModel dm = new SuggestIdMailDataModel(follower,postingUser,obv,givenName,recovote,config);
			Map<String,Object> root = dm.getRoot();
			HtmlEmail email = buildMessage(subject,this.message,toEmail,temp,root);
			return email;
		}catch(Exception e){
			throw e;
		}
	}
}
