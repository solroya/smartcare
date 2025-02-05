package com.nado.smartcare.bus.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nado.smartcare.bus.dto.BusStopDTO;
import com.nado.smartcare.bus.entity.BusStop;
import com.nado.smartcare.bus.repository.IBusStopRepository;
import com.nado.smartcare.bus.service.IBusStopService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class BusStopServiceImpl implements IBusStopService {
	
	@Autowired
	private final IBusStopRepository iStationRepository;
	
	@Autowired
	private final RestTemplate restTemplate;
	
	@Value("${gwangju.busStop.api.url}")
	private String busStopApiUrl;
	
	@Value("${gwangju.api.key}")
	private String apiKey;
	
//	@PostConstruct
//	public void init() {
//		saveBusStopFromApi();
//	}
	
	@Override
	@Transactional
	public void saveBusStop(List<BusStop> busStops) {
//		iStationRepository.saveAll(busStops);
	}
	
	@Override
	@Transactional
	public void saveBusStopFromApi() {
        String url = busStopApiUrl + "?serviceKey=" + apiKey;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
			String responseBody = responseEntity.getBody();
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode root = mapper.readTree(responseBody);
				JsonNode busStopListNode = root.path("STATION_LIST");
				
				List<BusStopDTO> busStopDTOList = new ArrayList<>();
				for (JsonNode busStopNode : busStopListNode) {
					BusStopDTO busStopDTO = mapper.treeToValue(busStopNode, BusStopDTO.class);
					busStopDTOList.add(busStopDTO);
				}
				
				List<BusStop> busStops = busStopDTOList.stream()
						.map(this::dtoToEntity)
						.collect(Collectors.toList());
				
				saveBusStop(busStops);
				
			} catch (Exception e) {
				log.error("JSON response가 제대로 파싱되지 않았습니다.", e);
			}
		} else {
			log.error("API 값을 데이터베이스에 담지 못했습니다.");
		}
        
    }

	@Override
	public BusStop findNearestBusStop(double lat, double lng) {
		List<BusStop> busStops = iStationRepository.findAll();
		if (busStops.isEmpty()) {
			log.warn("DB에 저장된 버스 정류장이 없습니다.");
			return null;
		}
		
		BusStop nearestBusStop = busStops.stream()
				.min(Comparator.comparingDouble(
						bs -> getDistance(lat, lng, bs.getLatitude(), bs.getLongitude())))
				.orElse(null);
				
		log.info("가장 가까운 버스 정류장 : {}", nearestBusStop != null ? nearestBusStop.getBusStopName() : "없음");
		return nearestBusStop;
	}
	
	private double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double R = 6371e3;
		double latRad1 = Math.toRadians(lat1);
		double latRad2 = Math.toRadians(lat2);
		double deltaLat = Math.toRadians(lat2 - lat1);
		double deltaLng = Math.toRadians(lng2 - lng1);
		
		double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
				+ Math.cos(latRad1) * Math.cos(latRad2)
				* Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		
		return R * c;
	}
	
}
