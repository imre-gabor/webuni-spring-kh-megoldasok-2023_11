package hu.webuni.university.security;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {

	private static final String COURSE_IDS = "courseIds";
	private static final String AUTH = "auth";
	private Algorithm alg = Algorithm.HMAC256("mysecret");
	private String issuer = "UniversityApp";
	
	public String creatJwtToken(UserDetails principal) {
		UserInfo userInfo = (UserInfo) principal; 
		
		List<Integer> courseIds = userInfo.getCourseIds();
		return JWT.create()
			.withSubject(principal.getUsername())
			.withArrayClaim(AUTH, principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))
			.withArrayClaim(COURSE_IDS, courseIds == null ? new Integer[0] : courseIds.toArray(Integer[]::new))
			.withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20)))
			.withIssuer(issuer)
			.sign(alg);
		
	}

	public UserDetails parseJwt(String jwtToken) {
		
		DecodedJWT decodedJwt = JWT.require(alg)
			.withIssuer(issuer)
			.build()
			.verify(jwtToken);
		return new UserInfo(decodedJwt.getSubject(), "dummy", 
				decodedJwt.getClaim(AUTH).asList(String.class)
				.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()),
				decodedJwt.getClaim(COURSE_IDS).asList(Integer.class)
				);
		
	}

}
