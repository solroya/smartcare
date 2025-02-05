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
public class BusStopAndLineStationDTO {
	
	private Long id;
	
	@JsonProperty("LINE_ID")
	private int lineId;
	@JsonProperty("LINE_NAME")
	private String lineName;
	@JsonProperty("BUSSTOP_ID")
	private int busStopId;
	@JsonProperty("BUSSTOP_NAME")
	private String busStopName;
	
}
