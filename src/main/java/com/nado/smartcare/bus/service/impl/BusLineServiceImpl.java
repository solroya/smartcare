package com.nado.smartcare.bus.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nado.smartcare.bus.dto.BusLineDTO;
import com.nado.smartcare.bus.entity.BusLine;
import com.nado.smartcare.bus.repository.IBusLineRepository;
import com.nado.smartcare.bus.service.IBusLineService;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class BusLineServiceImpl implements IBusLineService {
	
	@Autowired
	private final IBusLineRepository iBusLineRepository;
	
	@Autowired
	private final RestTemplate restTemplate;
	
	@Value("${gwangju.busLine.api.url}")
	private String busLineApiUrl;
	
	@Value("${gwangju.api.key}")
	private String apiKey;
	
//	@PostConstruct
//	public void init() {
//		saveBusLinesFromApi();
//	}
	
	@Override
	@Transactional
	public void saveBusLine(List<BusLine> busLines) {
//		iBusLineRepository.saveAll(busLines);
	}

	@Override
	@Transactional
	public void saveBusLinesFromApi() {
	    String url = busLineApiUrl + "?serviceKey=" + apiKey;
	    ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
	    
	    if (responseEntity.getStatusCode() == HttpStatus.OK) {
			String responseBody = responseEntity.getBody();
			
			ObjectMapper mapper = new ObjectMapper();
			
			try {
				JsonNode root = mapper.readTree(responseBody);
				JsonNode busLineListNode = root.path("LINE_LIST");
				
				List<BusLineDTO> busLineDTOList = new ArrayList<>();
				for (JsonNode busLineNode : busLineListNode) {
					try {
						BusLineDTO busLineDTO = mapper.treeToValue(busLineNode, BusLineDTO.class);
						busLineDTOList.add(busLineDTO);
					} catch (Exception e) {
						log.error("기타 예외 발생", e);
					}
				}
				
				List<BusLine> busLines = busLineDTOList.stream()
						.map(this::dtoToEntity)
						.collect(Collectors.toList());
				
				saveBusLine(busLines);
				
			} catch (Exception e) {
				log.info("Json response가 제대로 파싱되지 않았습니다.", e);
			}
		} else {
			log.error("API 값을 데이터값에 제대로 넣지 못했습니다.");
		}
	    
	}
	
}
