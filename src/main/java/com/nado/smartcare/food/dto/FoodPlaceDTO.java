package com.nado.smartcare.food.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FoodPlaceDTO {
	private Long fno;
	private String foodName;
	private String foodAddressRoad;
	private String foodAddressJibun;
	private String foodBusinessHours;
	private String foodPhoneNumber;
	private Double latitude;
	private Double longitude;
	private Integer likes;
	private boolean liked;
	private Integer views;
	private String category;
	private String imageUrl;
}
