package com.nado.smartcare.food.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CategoryDTO {
	
	private Long categoryNo;
	private String categoryName;
	private String categoryImage;
}
