package com.example.springBootExample.utils;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.message.AuthException;

import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.stereotype.Component;

import com.example.springBootExample.entity.Member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;

@Component
public class JwtTokenUtils {

	    private static final long EXPIRATION_TIME = 1 * 60 * 1000;
	    
	    /**
	     * JWT SECRET KEY
	     */
	    @Getter
	    @Setter
	    private String SECRET = "learn to dance in the rain";

	    /**
	     * 簽發JWT
	     */
	    public String generateToken(Member member) {
	        Map<String, Object> claims = new HashMap<String, Object>();
	        claims.put( "userName", member.getUsrName() );

	        return Jwts.builder()
	                .setClaims(claims)
	                .setExpiration( new Date( Instant.now().toEpochMilli() + EXPIRATION_TIME  ) )
	                .signWith( SignatureAlgorithm.HS512, SECRET )
	                .compact();
	    }

	    /**
	     * 驗證JWT
	     */
	    public void validateToken(String token) throws AuthException {
//	        try {
	            Jwts.parser()
	                    .setSigningKey( SECRET )
	                    .parseClaimsJws( token );
//	        } catch (SignatureException e) {
//	            throw new AuthException("Invalid JWT signature.");
//	        }
//	        catch (MalformedJwtException e) {
//	            throw new AuthException("Invalid JWT token.");
//	        }
//	        catch (ExpiredJwtException e) {
//	            throw new AuthException("Expired JWT token");
//	        }
//	        catch (UnsupportedJwtException e) {
//	            throw new AuthException("Unsupported JWT token");
//	        }
//	        catch (IllegalArgumentException e) {
//	            throw new AuthException("JWT token compact of handler are invalid");
//	        }
	    }
}
