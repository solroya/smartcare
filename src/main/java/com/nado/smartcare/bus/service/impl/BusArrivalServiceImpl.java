package com.nado.smartcare.bus.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nado.smartcare.bus.dto.BusArrivalDTO;
import com.nado.smartcare.bus.service.IBusArrivalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class BusArrivalServiceImpl implements IBusArrivalService {
	
	@Autowired
	private final RestTemplate restTemplate;
	
	@Value("${gwangju.arrival.api.url}")
	private String arrivalApiUrl;
	
	@Value("${gwangju.api.key}")
	private String apiKey;
	
	@Override
	public List<BusArrivalDTO> getArrivalInfoByBusStopId(int busStopId) {
		String url = arrivalApiUrl + "?key=" + apiKey + "&BUSSTOP_ID=" + busStopId;
		List<BusArrivalDTO> busArrivals = new ArrayList<>();
		
		try {
			String response = restTemplate.getForObject(url, String.class);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode busStopListNode = rootNode.path("BUSSTOP_LIST");
			if (busStopListNode.isArray()) {
				for (JsonNode busStopNode : busStopListNode) {
					BusArrivalDTO dto = objectMapper.treeToValue(busStopNode, BusArrivalDTO.class);
					busArrivals.add(dto);
				}
			}
			
			log.info("정류자 {}의 도착 정보를 성공적으로 가져왔습니다. {}", busStopId, busArrivals);
		} catch (Exception e) {
			log.error("정류장 도착 정보 호출 중 오류 발생 : ", e);
		}
		
		return busArrivals;
	}

}
