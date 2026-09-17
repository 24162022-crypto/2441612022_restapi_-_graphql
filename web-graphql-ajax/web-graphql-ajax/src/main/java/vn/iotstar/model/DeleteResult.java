package vn.iotstar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ket qua tra ve cho cac mutation xoa
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteResult {
	private Boolean status;
	private String message;
}
