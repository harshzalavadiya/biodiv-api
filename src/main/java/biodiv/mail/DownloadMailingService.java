package biodiv.mail;

import java.util.Map;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;



public class DownloadMailingService extends AbstractHtmlMailingService {
	
	@Inject
	freemarker.template.Configuration cfg;
	
	@Inject
	Configuration config;
	
	final String subject = "Download request";
	final String message = "Your data download request has been processed. The download link will be visible once you log in to your profile."; 
	@Inject
	public DownloadMailingService(Configuration config, MailProvider mailProvider) throws EmailException {
		super(mailProvider, config, "mail.download.subject", "mail.download.message");
		System.out.println("****************************************************8 "+config);
		System.out.println("****************************************************8 "+mailProvider);
	}
	
	public HtmlEmail buildDownloadMailMessage(String toEmail,Long id,String name) throws Exception{
		
		try{
		freemarker.template.Template temp = cfg.getTemplate("downloadMailTemp.ftlh");
		DownloadMailDataModel dm = new DownloadMailDataModel(id,name,config);
		Map<String,Object> root = dm.getRoot();
		HtmlEmail email = buildMessage(this.subject,this.message,toEmail,temp,root);
		return email;
		}catch(Exception e){
			throw e;
		}
	}
	
//	@Override
//	public void run(){
//		
//	}
}
