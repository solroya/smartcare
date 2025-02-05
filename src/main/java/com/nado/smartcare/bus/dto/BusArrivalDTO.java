package com.nado.smartcare.bus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusArrivalDTO {
	
	@JsonProperty("LINE_ID")
	private int lineId;
	
	@JsonProperty("LINE_NAME")
	private String lineName;
	
	@JsonProperty("BUS_ID")
	private String busId;
	
	@JsonProperty("CURR_STOP_ID")
	private int currStopId;
	
	@JsonProperty("BUSSTOP_NAME")
	private String busStopName;
	
	@JsonProperty("REMAIN_MIN")
	private int remainMin;
	
	@JsonProperty("REMAIN_STOP")
	private int remainStop;
	
	@JsonProperty("DIR_START")
	private String dirStart;
	
	@JsonProperty("DIR_START")
	private String dirEnd;
	
	@JsonProperty("LOW_BUS")
	private int lowBus;
	
}
