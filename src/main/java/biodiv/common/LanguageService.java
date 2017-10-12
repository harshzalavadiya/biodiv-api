package biodiv.common;

import java.util.Locale;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import biodiv.user.User;

public class LanguageService extends AbstractService<Language> {

	private static final Random NUMBER_GENERATOR = new Random();
	
	private Language defaultLanguage;

	private LanguageDao languageDao;

	public LanguageService() {
		this.languageDao = new LanguageDao();
	}

	@Override
	public LanguageDao getDao() {
		return languageDao;
	}

	public Language findByName(String languageName) {
		try {
			languageDao.openCurrentSession();
			if (languageName == null || languageName.trim() == "") {
				if (defaultLanguage == null)
					defaultLanguage = findByName(Language.DEFAULT_LANGUAGE);
				return defaultLanguage;
			} else {
				Language language = languageDao.findByPropertyWithCondition("name", languageName, "like");
				return language;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			languageDao.closeCurrentSession();
		}
	}

	public Language findByThreeLetterCode(String code) {
		try {
			languageDao.openCurrentSession();
			Language language = languageDao.findByPropertyWithCondition("threeLetterCode", code, "=");
			return language;
		} catch (Exception e) {
			throw e;
		} finally {
			languageDao.closeCurrentSession();
		}
	}
	
	public Language getOrCreateLanguage(String languageName) {
		Language lang = findByName(languageName);
	    if(lang == null){
	    	lang = new Language(languageName.trim(), _getThreeLetterCode(languageName), true);
	    	languageDao.save(lang);
	    }
	    return lang;
	}

	public Language getCurrentLanguage(HttpServletRequest request){
		//if(!defaultLanguage) defaultLanguage = findByName(Language.DEFAULT_LANGUAGE);
		Locale locale = request.getLocale();
		//String langStr = (cuRLocale)? cuRLocale : LCH.getLocale();
		String lang = locale.getLanguage();
		System.out.println(lang);
		Language languageInstance = languageDao.findByPropertyWithCondition("twoLetterCode", lang, "=");
		if(languageInstance != null) 
			return languageInstance;
		else 
			return defaultLanguage;
	}

	private String _getThreeLetterCode(String languageName) {
		// TODO fix this
		int i = 0;
		while (++i < 100) {
			// getting a 3 digit number
			String number = "" + (Math.abs((NUMBER_GENERATOR.nextInt() + 100) % 1000));
			if (languageDao.findByPropertyWithCondition("threeLetterCode", number, "=") == null) {
				return number;
			}
		}
		// println "Invalid ThreeLetterCode. please give unique code"
		return null;
	}


}