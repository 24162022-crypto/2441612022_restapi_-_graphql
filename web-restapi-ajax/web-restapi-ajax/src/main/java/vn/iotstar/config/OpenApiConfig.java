package vn.iotstar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Cau hinh Swagger 3 (OpenAPI) cho Spring Boot 3.
 *
 * VI SAO CHI CAN springdoc-openapi-starter-webmvc-ui?
 * - Spring Boot 3 chay tren Jakarta EE 9+ (jakarta.*), trong khi springfox
 *   (swagger2 cu) van dung javax.* va da ngung phat trien -> KHONG tuong thich,
 *   them vao se loi khi khoi dong.
 * - springdoc 2.x tu quet toan bo @RestController va sinh tai lieu OpenAPI,
 *   nen KHONG can @EnableSwagger2 hay bat ky annotation bat cong tac nao.
 * - Lop nay chi de bo sung tieu de/mo ta cho dep, co the bo di van chay.
 *
 * Truy cap: http://localhost:8082/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info()
				.title("API Quan ly Category & Product")
				.version("1.0")
				.description("Bai tap mon Lap trinh Web - WEBPR330479"));
	}
}
