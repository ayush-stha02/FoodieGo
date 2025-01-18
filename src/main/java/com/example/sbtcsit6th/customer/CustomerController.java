package com.example.sbtcsit6th.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.sbtcsit6th.AuthService;
import com.example.sbtcsit6th.product.ProductRepository;
import com.example.sbtcsit6th.user.User;
import com.example.sbtcsit6th.user.UserType;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomerController {

	@Autowired
	private AuthService authService;

	@Autowired
	private ProductRepository productRepository;

	@GetMapping("/customer")
	public String getDashboard(Model model, HttpServletRequest request) {

		User user = authService.getUser(request);

		// Authentication
		if (user == null) {
			System.out.println("No logged in user found");
			return "redirect:/login";
		}

		// Authorization
		if (user.getType() != UserType.CUSTOMER) {
			System.out.println("CUSTOMER type expected");
			return "redirect:/login";
		}

		model.addAttribute("user", user);
		model.addAttribute("products", productRepository.findAll());

		return "customer-dashboard.html";
	}

}
