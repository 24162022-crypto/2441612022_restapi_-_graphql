package vn.iotstar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Format response chuan cho toan bo REST API theo quy dinh cua giang vien.
 * status : true neu thanh cong, false neu that bai
 * message: thong diep hien thi cho nguoi dung
 * body   : du lieu tra ve (object / list / map)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
	private Boolean status;
	private String message;
	private Object body;
}
