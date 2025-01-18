package com.example.sbtcsit6th;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.sbtcsit6th.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DashboardController {
	
	@Autowired
	private AuthService authService;

	@GetMapping("/dashboard")
	public String getDashboardPage(Model model, HttpServletRequest req) {
		
		User user = authService.getUser(req);
		
		if(user == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("user", user);
		
		return "dashboard.html";
	}
	
}
