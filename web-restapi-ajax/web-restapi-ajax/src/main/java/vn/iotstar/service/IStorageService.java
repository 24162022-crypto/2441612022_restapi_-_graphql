package vn.iotstar.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {

	// Tao thu muc uploads neu chua ton tai
	void init();

	// Luu file, tra ve TEN FILE da sinh ra (dang p<UUID>.<ext>)
	String store(MultipartFile file);

	// Lay danh sach duong dan tat ca file trong thu muc uploads
	Stream<Path> loadAll();

	// Lay duong dan day du cua 1 file theo ten
	Path load(String filename);

	// Doc file thanh Resource de tra ve cho trinh duyet
	Resource loadAsResource(String filename);

	// Xoa 1 file theo ten
	void delete(String filename);

	// Xoa toan bo thu muc uploads
	void deleteAll();
}
