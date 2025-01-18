package com.example.sbtcsit6th;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sbtcsit6th.user.User;
import com.example.sbtcsit6th.user.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

	private static final String SESSION_KEY = "sessionid";
	private static final int SESSION_LEN = 20;
	public static String sampleSpace = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int SAMPLE_LEN = sampleSpace.length();
	
	@Autowired
	private UserRepository userRepository;

	public void setSession(HttpServletResponse httpResponse, User user) {

		Random random = new Random();

		char[] c = new char[SESSION_LEN];

		while (true) {
			for (int i = 0; i < SESSION_LEN; i++) {
				c[i] = sampleSpace.charAt(random.nextInt(SAMPLE_LEN));
			}

			if (!userRepository.existsBySession(String.valueOf(c))) {
				break;
			}
		}

		String session = String.valueOf(c);
		Cookie cookie = new Cookie(SESSION_KEY, session);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(86400); // for one day
		httpResponse.addCookie(cookie);

		user.setSession(session);
		userRepository.save(user);

	}

	public User getUser(HttpServletRequest httpRequest) {

		Cookie[] cookies = httpRequest.getCookies();

		String session = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(SESSION_KEY)) {
					session = cookie.getValue();
					break;
				}
			}
		}

		if (session != null) {

			Optional<User> optionalUser = userRepository.findBySession(session);

			return optionalUser.isPresent() ? optionalUser.get() : null;

		}

		return null;
	}

	public void revokeSession(HttpServletRequest httpRequest) {
		User user = getUser(httpRequest);

		if (user != null) {
			user.setSession(null);
			userRepository.save(user);
		}

	}

}
