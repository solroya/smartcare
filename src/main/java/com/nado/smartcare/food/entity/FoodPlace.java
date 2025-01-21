package com.nado.smartcare.food.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FoodPlace {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fno;
	
	private String foodName;
	private String foodAddressRoad;
	private String foodAddressJibun;
	private String foodbusinessHours;
	private String foodPhoneNumber;
	private Double latitude;
	private Double longitude;
	private Integer likes = 0;
	private Integer views = 0;
	private String category;
	private String imageUrl;
	
}
