package com.nado.smartcare.food.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComentDTO {
	private Long cno;
	private String content;
	private String author;
	private int rating;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
