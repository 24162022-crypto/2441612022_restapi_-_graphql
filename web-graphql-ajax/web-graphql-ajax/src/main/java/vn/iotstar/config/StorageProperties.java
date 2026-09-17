package vn.iotstar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Doc cau hinh storage.location trong application.properties
 * Vi du: storage.location=uploads
 */
@Getter
@Setter
@ConfigurationProperties("storage")
public class StorageProperties {

	// Thu muc goc luu file upload, mac dinh la "uploads"
	private String location = "uploads";
}
