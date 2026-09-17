package vn.iotstar.exception;

/**
 * Nem ra khi co loi trong qua trinh luu / doc / xoa file
 */
public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public StorageException(String message) {
		super(message);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
