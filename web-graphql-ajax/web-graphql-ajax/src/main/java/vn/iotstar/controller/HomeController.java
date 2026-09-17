package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller tra ve view Thymeleaf
 */
@Controller
public class HomeController {

	@GetMapping({ "/", "/home" })
	public String home() {
		return "home";
	}

	@GetMapping("/product-manage")
	public String productManage() {
		return "product-manage";
	}

	@GetMapping("/category-manage")
	public String categoryManage() {
		return "category-manage";
	}
}
