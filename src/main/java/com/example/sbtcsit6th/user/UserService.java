package com.example.sbtcsit6th.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@PostConstruct
	public void setup() {
		System.out.println("This setup() is called automatically after object construction");
		
		// TODO: write code for create ADMIN user.
		if (userRepository.count() == 0) {
			User admin = new User();
			admin.setFullname("John Doe");
			admin.setUsername("admin");
			admin.setEmail("admin@demo.com");
			admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
			admin.setType(UserType.ADMIN);
			userRepository.save(admin);
		}

	}

}
