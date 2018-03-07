package biodiv.common;

import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;

public class LanguageService extends AbstractService<Language> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final Random NUMBER_GENERATOR = new Random();

	private Language defaultLanguage;

	private LanguageDao languageDao;

	@Inject
	LanguageService(LanguageDao languageDao) {
		super(languageDao);
		this.languageDao = languageDao;
	}

	@Transactional
	public Language findByName(String languageName) {
		if (languageName == null || languageName.trim() == "") {
			if (defaultLanguage == null)
				defaultLanguage = findByName(Language.DEFAULT_LANGUAGE);
			return defaultLanguage;
		} else {
			Language language = languageDao.findByPropertyWithCondition("name", languageName, "like");
			return language;
		}
	}

	@Transactional
	public Language findByThreeLetterCode(String code) {
		Language language = languageDao.findByPropertyWithCondition("threeLetterCode", code, "=");
		return language;
	}

	@Transactional
	public Language getOrCreateLanguage(String languageName) {
		Language lang = findByName(languageName);
		if (lang == null) {
			lang = new Language(languageName.trim(), _getThreeLetterCode(languageName), true);
			languageDao.save(lang);
		}
		return lang;
	}

	@Transactional
	public Language getCurrentLanguage(HttpServletRequest request) {
		// if(!defaultLanguage) defaultLanguage =
		// findByName(Language.DEFAULT_LANGUAGE);
		Locale locale = request.getLocale();
		// String langStr = (cuRLocale)? cuRLocale : LCH.getLocale();
		String lang = locale.getLanguage();
		Language languageInstance = languageDao.findByPropertyWithCondition("twoLetterCode", lang, "=");
		if (languageInstance != null)
			return languageInstance;
		else
			return defaultLanguage;
	}

	@Transactional
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

	@Transactional
	public Language findByTwoLetterCode(String code) {
		Language lang;
		try {
			lang = languageDao.findByTwoLetterCode(code);
			return lang;
		} catch (Exception e) {
			throw e;
		} finally {

		}
	}

}
