package com.nado.smartcare.bus.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nado.smartcare.bus.service.IBusLocationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class BusLocationServiceImpl implements IBusLocationService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public List<Integer> getBusLocations(int lineId) {
		String url = "http://api.gwangju.go.kr/json/busLocationInfo?key=TPyLI7QNNlQX2xKxUY56dfjRmY%2FaOoQIsW8KuoHIrw5OJYcfPZenny4lwy%2F7CfIykcte8qLKz9HcXypSfX2p6Q%3D%3D&LINE_ID=" + lineId;
		List<Integer> busLocationStops = new ArrayList<>();
		
		try {
			String response = restTemplate.getForObject(url, String.class);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			
			JsonNode busLocationListNode = rootNode.path("BUSLOCATION_LIST");
			if (busLocationListNode.isArray()) {
				for (JsonNode busLocationNode : busLocationListNode) {
					int busStopId = busLocationNode.path("CURR_STOP_ID").asInt();
					if (busStopId != 0) {
						busLocationStops.add(busStopId);
					}
				}
			}
			
			log.info("버스 위치 정보를 성공적으로 가져왔습니다 : {}", busLocationStops);
		} catch (Exception e) {
			log.error("버스 위치 정보를 가져오는 중 오류 발생 : ", e);
		}
		
		return busLocationStops;
	}

}
