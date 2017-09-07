package biodiv.auth.token;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.auth.Constants;
import biodiv.auth.token.Token.TokenType;
import biodiv.user.User;
import biodiv.util.RandomString;

public class TokenService {

	private static final Logger log = LoggerFactory.getLogger(TokenService.class);

	private static TokenDao tokenDao;

	public TokenService() {
		tokenDao = new TokenDao();
	}

	public Map buildTokenResponse(CommonProfile profile, boolean getNewRefreshToken) {
		try {
			String jwtToken = generateAccessToken(profile);

			tokenDao.openCurrentSessionwithTransaction();
			// Create a proxy object for logged in user
			User user = tokenDao.getCurrentSession().get(User.class, Long.parseLong(profile.getId()));

			// Removing all existing refreshTokens			
			List<Token> existingRefreshToken = tokenDao.findByUser(user);
			for (Token t : existingRefreshToken) {
				tokenDao.delete(t);
			}

			// Return the access_token valid for 2 hrs and a new refreshToken on
			// the response
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("access_token", jwtToken);
			result.put("token_type", "bearer");
			result.put("expires_in", Duration.ofHours(2).getSeconds());
			// result.put("scope", "");
			if(getNewRefreshToken) {
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

	public String generateAccessToken(CommonProfile profile) {
		JwtGenerator<CommonProfile> generator = new JwtGenerator<>(
				new SecretSignatureConfiguration(Constants.JWT_SALT));
		String jwtToken = generator.generate(profile);
		return jwtToken;
	}

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

	public boolean isValidRefreshToken(String refreshToken, Long user_id) {
		User user = tokenDao.getCurrentSession().get(User.class, user_id);
		if(tokenDao.findByValueAndUser(refreshToken, user).size() > 0) return true;
		return false;
	}
}
