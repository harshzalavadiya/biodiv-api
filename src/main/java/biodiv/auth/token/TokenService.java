package biodiv.auth.token;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.auth.AuthUtils;
import biodiv.auth.Constants;
import biodiv.auth.token.Token.TokenType;
import biodiv.common.AbstractService;
import biodiv.user.User;
import biodiv.user.UserService;
import biodiv.util.RandomString;

public class TokenService extends AbstractService<Token> {

	private static final Logger log = LoggerFactory.getLogger(TokenService.class);

	private TokenDao tokenDao;
	private UserService userService = new UserService();

	public TokenService() {
		this.tokenDao = new TokenDao();
	}

	public TokenDao getDao() {
		return tokenDao;
	}

	/**
	 * Builds a response for authentication. On success it returns a access
	 * token and optionally a refresh token
	 * 
	 * @param profile
	 * @param getNewRefreshToken
	 * @return
	 */
	public Map<String, Object> buildTokenResponse(CommonProfile profile, User user, boolean getNewRefreshToken) {
		try {
			log.debug("Building token response for "+user);
			String jwtToken = generateAccessToken(profile);

			tokenDao.openCurrentSessionWithTransaction();
			// Return the access_token valid for 2 hrs and a new refreshToken on
			// the response
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("access_token", jwtToken);
			result.put("token_type", "bearer");
			result.put("expires_in", (AuthUtils.getAccessTokenExpiryDate().getTime() - (new Date()).getTime()));// Duration.ofDays(1).getSeconds()
			// result.put("scope", "");

			if (getNewRefreshToken) {
				log.debug("Generating new refresh token for "+user);
				// Removing all existing refreshTokens
				/*
				 * List<Token> existingRefreshToken = tokenDao.findByUser(user);
				 * for (Token t : existingRefreshToken) { user.setToken(null);
				 * tokenDao.delete(t); } tokenDao.getCurrentSession().flush();
				 */

				// Generating a fresh refreshToken
				String refreshToken = generateRefreshToken();

				Token rToken = new Token(refreshToken, TokenType.REFRESH, user);

				tokenDao.save(rToken);

				result.put("refresh_token", refreshToken);
			}
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			tokenDao.closeCurrentSessionWithTransaction();
		}
	}

	/**
	 * Generates access token in JWT format encrypted with JWT_SALT as secret
	 * for the profile.
	 * 
	 * @param profile
	 * @return TODO : use bcrypt encryption for token
	 */
	public String generateAccessToken(CommonProfile profile) {
		log.debug("generateAccessToken .... ");
		JwtGenerator<CommonProfile> generator = new JwtGenerator<>(
				new SecretSignatureConfiguration(Constants.JWT_SALT));
		String jwtToken = generator.generate(profile);
		return jwtToken;
	}

	/**
	 * Generates a refresh token which is a plain string used to identify user.
	 * 
	 * @return
	 */
	public String generateRefreshToken() {
		// Random random = new SecureRandom();
		// String token = new BigInteger(130, random).toString(32);

		// Algorithm : To generate a random string, concatenate characters drawn
		// randomly from the set of acceptable symbols until the string reaches
		// the desired length.
		log.debug("generateRefreshToken .... ");
		String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
		RandomString tickets = new RandomString(23, new SecureRandom(), easy);

		return tickets.nextString();
	}

	/**
	 * 
	 * @param refreshToken
	 * @param userId
	 * @return
	 */
	public boolean isValidRefreshToken(String refreshToken, Long userId) {
		if (refreshToken == null || userId == null)
			return false;
		try {
			tokenDao.openCurrentSession();
			log.debug("isValidRefreshToken .... "+userId);
			Token token = tokenDao.findByValueAndUser(refreshToken, userId);
			if (token == null) {
				log.warn("Refresh token is invalid.");
				return false;
			}
			if (token.getCreatedOn().before(AuthUtils.getRefreshTokenExpiryDate())) {
				log.warn("Refresh token expired.");
				return false;
			}
			return true;
		} catch (Exception e) {
			throw e;
		} finally {
			tokenDao.closeCurrentSession();
		}
	}

	/**
	 * 
	 * @param user
	 */
    public void removeRefreshToken(String refreshToken) {
		if (refreshToken== null)
			return;
		try {
			tokenDao.openCurrentSessionWithTransaction();
			log.debug("Removing refresh token "+refreshToken);
            // Removing refreshToken
            Token existingRefreshToken = tokenDao.findByValue(refreshToken);
            if(existingRefreshToken != null) {
			    //User user = userService.findById(userId);
				//user.setTokens().remove(existingRefreshToken);
				tokenDao.delete(existingRefreshToken);
            
			log.debug("Flushing session on delete tokens");
			tokenDao.getCurrentSession().flush();
            }

		} catch (Exception e) {
			throw e;
		} finally {
			tokenDao.closeCurrentSessionWithTransaction();
		}
	}


	public void removeRefreshToken(Long userId, String refreshToken) {
		if (userId == null || refreshToken== null)
			return;
		try {
			tokenDao.openCurrentSessionWithTransaction();
			log.debug("Removing refresh token "+refreshToken+" for user "+userId);
			// Removing refreshToken
            Token existingRefreshToken = tokenDao.findByValueAndUser(refreshToken, userId);
            if(existingRefreshToken != null) {
			    //User user = userService.findById(userId);
				//user.getTokens().remove(existingRefreshToken);
				tokenDao.delete(existingRefreshToken);
            }
			/*List<Token> existingRefreshToken = tokenDao.findByUser(userId);
			User user = userService.findById(userId);
			for (Token t : existingRefreshToken) {
				user.setTokens(null);
				tokenDao.delete(t);
			}*/
			log.debug("Flushing session on delete tokens");
			tokenDao.getCurrentSession().flush();

		} catch (Exception e) {
			throw e;
		} finally {
			tokenDao.closeCurrentSessionWithTransaction();
		}
	}

	public Token findByValue(String value) {
		try {
			tokenDao.openCurrentSession();
			Token token = tokenDao.findByValue(value);
			return token;
		} catch (Exception e) {
			throw e;
		} finally {
			tokenDao.closeCurrentSession();
		}
	}

}
