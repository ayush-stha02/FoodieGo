package com.example.sbtcsit6th.admin;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.sbtcsit6th.AuthService;
import com.example.sbtcsit6th.Mappings;
import com.example.sbtcsit6th.StorageService;
import com.example.sbtcsit6th.ValidationError;
import com.example.sbtcsit6th.product.CreateProductForm;
import com.example.sbtcsit6th.product.Product;
import com.example.sbtcsit6th.product.ProductCategory;
import com.example.sbtcsit6th.product.ProductCategoryRepository;
import com.example.sbtcsit6th.product.ProductRepository;
import com.example.sbtcsit6th.user.User;
import com.example.sbtcsit6th.user.UserType;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AdminController {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductCategoryRepository productCategoryRepository;

	@Autowired
	private AuthService authService;

	@Autowired
	private StorageService storageService;

	@GetMapping(Mappings.ADMIN)
	public String getDashboard(HttpServletRequest request) {

		User user = authService.getUser(request);

		// Authentication
		if (user == null) {
			System.out.println("No logged in user found");
			return "redirect:" + Mappings.LOGIN;
		}

		// Authorization
		if (user.getType() != UserType.ADMIN) {
			System.out.println("ADMIN type expected");
			return "redirect:" + Mappings.LOGIN;
		}

		return "admin-dashboard.html";
	}

	@GetMapping(Mappings.ADMIN_PRODUCT)
	public String getProductPage(Model model, HttpServletRequest request) {
		User user = authService.getUser(request);

		// Authentication & Authorization respectively
		if (user == null || user.getType() != UserType.ADMIN) {
			System.out.println("No logged in user found");
			return "redirect:" + Mappings.LOGIN;
		}

		model.addAttribute("products", productRepository.findAll());
		return "view-product.html";
	}

	@GetMapping(Mappings.ADMIN_CREATE_PRODUCT)
	public String getAddProductPage(HttpServletRequest request, Model model) {

		User user = authService.getUser(request);

		// Authentication & Authorization respectively
		if (user == null || user.getType() != UserType.ADMIN) {
			System.out.println("No logged in user found");
			return "redirect:" + Mappings.LOGIN;
		}

		model.addAttribute("product", new Product());
		model.addAttribute("error", new ValidationError());
		model.addAttribute("productCategories", productCategoryRepository.findAll());
		model.addAttribute("forUpdate", false);
		return "add-product.html";
	}

	@GetMapping(Mappings.ADMIN_UPDATE_PRODUCT + "/{id}")
	public String getUpdateProductPage(@PathVariable("id") int id, HttpServletRequest request, Model model) {

		User user = authService.getUser(request);

		// Authentication & Authorization respectively
		if (user == null || user.getType() != UserType.ADMIN) {
			System.out.println("No logged in user found");
			return "redirect:" + Mappings.LOGIN;
		}

		Optional<Product> optionalProduct = productRepository.findById(id);
		Product product = optionalProduct.isPresent() ? optionalProduct.get() : new Product();

		model.addAttribute("product", product);
		model.addAttribute("error", new ValidationError());
		model.addAttribute("productCategories", productCategoryRepository.findAll());
		model.addAttribute("forUpdate", true);
		return "add-product.html";
	}

	@PostMapping(Mappings.ADMIN_CREATE_PRODUCT)
	public String addNewProduct(@ModelAttribute CreateProductForm form, Model model, HttpServletRequest request) {
//	public String addNewProduct(Product product, Model model, HttpServletRequest request) {

		User user = authService.getUser(request);

		// Authentication & Authorization respectively
		if (user == null || user.getType() != UserType.ADMIN) {
			System.out.println("No logged in user found");
			return "redirect:" + Mappings.LOGIN;
		}

		form.setName(form.getName().strip());
		form.setUnit(form.getUnit().strip());
		form.setDescription(form.getDescription().strip());

		ValidationError error = null;

		if (form.getName().isEmpty()) {
			error = new ValidationError("Product name is required");
		}

		// check if product name already exists
		Optional<Product> optionalProduct = productRepository.findByName(form.getName());
		if (optionalProduct.isPresent()) {
			error = new ValidationError("Product with given name already exists");
		}

		if (form.getUnit().isEmpty()) {
			error = new ValidationError("Unit is required");
		}

		if (form.getPrice() == null || form.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			error = new ValidationError("Price must be positive");
		}

		if (error != null) {
			model.addAttribute("error", error);
			model.addAttribute("product", form);
			model.addAttribute("forUpdate", false);
			return "add-product.html";
		}

		Product product = new Product();
		product.setCategory(form.getCategory());
		product.setDescription(form.getDescription());
		product.setName(form.getName());
		product.setPrice(form.getPrice());
		product.setUnit(form.getUnit());

		String fileName = storageService.store(form.getImage());
		product.setImage(fileName);

		// save the product
		productRepository.save(product);

		return "redirect:" + Mappings.ADMIN_PRODUCT;
	}

	@PostMapping(Mappings.ADMIN_UPDATE_PRODUCT + "/{id}")
	public String updateProduct(@PathVariable("id") int id, @ModelAttribute CreateProductForm form, Model model,
			HttpServletRequest request) {

		User user = authService.getUser(request);

		// Authentication & Authorization respectively
		if (user == null || user.getType() != UserType.ADMIN) {
			System.out.println("No logged in user found");
			return "redirect:" + Mappings.LOGIN;
		}

		form.setName(form.getName().strip());
		form.setUnit(form.getUnit().strip());
		form.setDescription(form.getDescription().strip());

		ValidationError error = null;

		if (form.getName().isEmpty()) {
			error = new ValidationError("Product name is required");
		}

		// check if product name already exists
		if (productRepository.countByName(form.getName()) > 1) {
			error = new ValidationError("Product with given name already exists");
		}

		if (form.getUnit().isEmpty()) {
			error = new ValidationError("Unit is required");
		}

		if (form.getPrice() == null || form.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			error = new ValidationError("Price must be positive");
		}

		if (error != null) {
			model.addAttribute("error", error);
			model.addAttribute("product", form);
			model.addAttribute("forUpdate", true);
			return "add-product.html";
		}

		Optional<Product> optionalProduct = productRepository.findById(id);

		if (optionalProduct.isPresent()) {
			Product product = optionalProduct.get();
			product.setCategory(form.getCategory());
			product.setDescription(form.getDescription());
			product.setName(form.getName());
			product.setPrice(form.getPrice());
			product.setUnit(form.getUnit());

			String fileName = storageService.store(form.getImage());
			product.setImage(fileName);

			// save the product
			productRepository.save(product);

		}

		return "redirect:" + Mappings.ADMIN_PRODUCT;
	}

	@PostMapping(Mappings.ADMIN_DELETE_PRODUCT)
	public String deleteProduct(Product product, Model model, HttpServletRequest request) {

		User user = authService.getUser(request);

		// Authentication & Authorization respectively
		if (user == null || user.getType() != UserType.ADMIN) {
			System.out.println("No logged in user found");
			return "redirect:" + Mappings.LOGIN;
		}

		try {
			productRepository.deleteById(product.getId());
		} catch (Exception e) {
		}

		return "redirect:" + Mappings.ADMIN_PRODUCT;
	}

	@GetMapping(Mappings.ADMIN_PRODUCT_CATEGORY)
	public String getProductCategoryPage(Model model, HttpServletRequest request) {
		User user = authService.getUser(request);

		// Authentication & Authorization respectively
		if (user == null || user.getType() != UserType.ADMIN) {
			System.out.println("No logged in user found");
			return "redirect:" + Mappings.LOGIN;
		}

		model.addAttribute("productCategories", productCategoryRepository.findAll());
		return "view-productCategory.html";
	}

	@GetMapping(Mappings.ADMIN_CREATE_PRODUCT_CATEGORY)
	public String getAddProductCategoryPage(HttpServletRequest request, Model model) {

		User user = authService.getUser(request);

		// Authentication & Authorization respectively
		if (user == null || user.getType() != UserType.ADMIN) {
			System.out.println("No logged in user found");
			return "redirect:/login";
		}

		model.addAttribute("productCategory", new ProductCategory());
		model.addAttribute("error", new ValidationError());
		return "add-productCategory.html";
	}

	@PostMapping(Mappings.ADMIN_CREATE_PRODUCT_CATEGORY)
	public String addNewProductCategory(ProductCategory productCategory, Model model, HttpServletRequest request) {

		User user = authService.getUser(request);

		// Authentication & Authorization respectively
		if (user == null || user.getType() != UserType.ADMIN) {
			System.out.println("No logged in user found");
			return "redirect:" + Mappings.LOGIN;
		}

		productCategory.setName(productCategory.getName().strip());
		productCategory.setDescription(productCategory.getDescription().strip());

		ValidationError error = null;

		if (productCategory.getName().isEmpty()) {
			error = new ValidationError("Product Category name is required");
		}

		if (error != null) {
			model.addAttribute("error", error);
			return "add-productCategory.html";
		}

		// save the product
		productCategoryRepository.save(productCategory);

		return "redirect:/admin/productCategory";
	}

	@PostMapping(Mappings.ADMIN_DELETE_PRODUCT_CATEGORY)
	public String deleteProductCategory(ProductCategory category, Model model, HttpServletRequest request) {

		User user = authService.getUser(request);

		// Authentication & Authorization respectively
		if (user == null || user.getType() != UserType.ADMIN) {
			System.out.println("No logged in user found");
			return "redirect:" + Mappings.LOGIN;
		}

		try {
			productCategoryRepository.deleteById(category.getId());
		} catch (Exception e) {
		}
		return "redirect:/admin/productCategory";
	}

}
