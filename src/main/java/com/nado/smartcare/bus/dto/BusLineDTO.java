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
public class BusLineDTO {
	
	private Long id;
	
	@JsonProperty("LINE_ID")
	private int lineId;
	
	@JsonProperty("LINE_NAME")
	private String lineName;
	
	@JsonProperty("DIR_UP_NAME")
	private String dirUpName;
	
	@JsonProperty("DIR_DOWN_NAME")
	private String dirDownName;
	
	@JsonProperty("FIRST_RUN_TIME")
	private String firstRunTime;
	
	@JsonProperty("LAST_RUN_TIME")
	private String lastRunTime;
	
	@JsonProperty("RUN_INTERVAL")
	private int runInterval;
	
	@JsonProperty("LINE_KIND")
	private int lineKind;
	
}
