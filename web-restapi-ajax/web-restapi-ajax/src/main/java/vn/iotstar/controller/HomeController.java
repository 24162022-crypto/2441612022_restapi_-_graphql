package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller tra ve view Thymeleaf (khong phai REST)
 */
@Controller
public class HomeController {

	@GetMapping({ "/", "/category" })
	public String category() {
		return "category"; // -> templates/category.html
	}

	@GetMapping("/product")
	public String product() {
		return "product"; // -> templates/product.html
	}
}
