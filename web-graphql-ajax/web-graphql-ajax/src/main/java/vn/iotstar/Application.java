package vn.iotstar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import vn.iotstar.config.StorageProperties;
import vn.iotstar.service.IStorageService;

/**
 * Lop khoi dong ung dung.
 * @EnableConfigurationProperties de Spring nap StorageProperties
 * (vi StorageProperties khong gan @Component).
 */
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Chay ngay sau khi ung dung khoi dong: tao thu muc uploads neu chua co.
	 * LUU Y: KHONG goi storageService.deleteAll() o day, neu goi thi moi lan
	 * restart server toan bo anh da upload se bi xoa sach.
	 */
	@Bean
	CommandLineRunner init(IStorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}
}
