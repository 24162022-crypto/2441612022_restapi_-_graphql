package vn.iotstar.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.config.StorageProperties;
import vn.iotstar.exception.StorageException;
import vn.iotstar.exception.StorageFileNotFoundException;

/**
 * Luu file len he thong file (thu muc uploads ngang hang voi thu muc project)
 */
@Service
public class FileSystemStorageServiceImpl implements IStorageService {

	// Thu muc goc luu file, doc tu storage.location
	private final Path rootLocation;

	@Autowired
	public FileSystemStorageServiceImpl(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Khong the khoi tao thu muc luu tru", e);
		}
	}

	@Override
	public String store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Khong the luu file rong");
			}

			// Lay phan mo rong cua file goc: png, jpg, jpeg...
			String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

			// Sinh ten file moi theo quy uoc cua tai lieu: p + UUID + . + ext
			// Muc dich: tranh trung ten va tranh loi ten file tieng Viet co dau
			String generatedFileName = "p" + UUID.randomUUID().toString().replace("-", "");
			generatedFileName = generatedFileName + "." + fileExtension;

			Path destinationFile = rootLocation.resolve(Paths.get(generatedFileName))
					.normalize().toAbsolutePath();

			// Chan tan cong path traversal (vi du ten file dang ../../etc/passwd)
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				throw new StorageException("Khong the luu file ra ngoai thu muc quy dinh");
			}

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}

			return generatedFileName;

		} catch (IOException e) {
			throw new StorageException("Loi khi luu file", e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
					.filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new StorageException("Loi khi doc danh sach file", e);
		}
	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Khong doc duoc file: " + filename);
			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Khong doc duoc file: " + filename, e);
		}
	}

	@Override
	public void delete(String filename) {
		try {
			Path file = load(filename);
			Files.deleteIfExists(file);
		} catch (IOException e) {
			throw new StorageException("Loi khi xoa file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}
}
