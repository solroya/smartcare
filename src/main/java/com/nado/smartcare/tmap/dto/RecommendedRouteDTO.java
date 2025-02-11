package com.nado.smartcare.tmap.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendedRouteDTO {
	private String type;
	private String description;
	private double distance;
	private int time;
	private String coordinates;
	private List<RouteDTO> detailedSteps;
}
