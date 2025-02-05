package com.nado.smartcare.bus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusStopDTO {
	
	private Long id;
	
	@JsonProperty("BUSSTOP_ID")
	private int busStopId;
	@JsonProperty("BUSSTOP_NAME")
	private String busStopName;
	@JsonProperty("LONGITUDE")
	private double longitude;
	@JsonProperty("LATITUDE")
	private double latitude;
	@JsonProperty("NEXT_BUSSTOP")
	private String nextBusStop;
}
