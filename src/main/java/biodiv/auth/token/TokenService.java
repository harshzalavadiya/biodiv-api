package biodiv.auth.token;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import biodiv.util.RandomString;

public class TokenService extends AbstractService<Token> {

	private static final Logger log = LoggerFactory.getLogger(TokenService.class);

	private TokenDao tokenDao;

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
	public Map buildTokenResponse(CommonProfile profile, boolean getNewRefreshToken) {
		try {
			String jwtToken = generateAccessToken(profile);

			tokenDao.openCurrentSessionwithTransaction();
			// Create a proxy object for logged in user
			User user = tokenDao.getCurrentSession().get(User.class, Long.parseLong(profile.getId()));

			// Return the access_token valid for 2 hrs and a new refreshToken on
			// the response
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("access_token", jwtToken);
			result.put("token_type", "bearer");
			result.put("expires_in", (AuthUtils.getAccessTokenExpiryDate().getTime() - (new Date()).getTime()));// Duration.ofDays(1).getSeconds()
			// result.put("scope", "");

			if (getNewRefreshToken) {

                // Removing all existing refreshTokens
                List<Token> existingRefreshToken = tokenDao.findByUser(user);
                for (Token t : existingRefreshToken) {
                    user.setToken(null);
                    tokenDao.delete(t);
                }
                tokenDao.getCurrentSession().flush();


				// Generating a fresh refreshToken
				String refreshToken = generateRefreshToken();
				
				Token rToken = new Token(refreshToken, TokenType.REFRESH, user);
				
				tokenDao.persist(rToken);
				
				result.put("refresh_token", refreshToken);
			}
			tokenDao.closeCurrentSessionwithTransaction();
			return result;
		} catch (Exception e) {
			throw e;
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
		User user = tokenDao.openCurrentSession().get(User.class, userId);
		Token token = tokenDao.findByValueAndUser(refreshToken, user);
		if (token == null) {
			log.warn("Refresh token is invalid.");
			return false;
		}
		if (token.getCreatedOn().before(AuthUtils.getRefreshTokenExpiryDate())) {
			log.warn("Refresh token expired.");
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param user
	 */
	public void removeRefreshToken(User user) {
		if(user == null) return;
		try {
			tokenDao.openCurrentSessionwithTransaction();

			// Removing all existing refreshTokens
			List<Token> existingRefreshToken = tokenDao.findByUser(user);
			for (Token t : existingRefreshToken) {
				user.setToken(null);
				tokenDao.delete(t);
			}
			tokenDao.getCurrentSession().flush();

			tokenDao.closeCurrentSessionwithTransaction();
		} catch (Exception e) {
			throw e;
		}
	}
}
