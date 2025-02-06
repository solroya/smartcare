package com.nado.smartcare.bus.service.impl;

import java.util.ArrayList;
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
import com.nado.smartcare.bus.dto.LineStationDTO;
import com.nado.smartcare.bus.entity.BusLine;
import com.nado.smartcare.bus.entity.LineStation;
import com.nado.smartcare.bus.repository.IBusLineRepository;
import com.nado.smartcare.bus.repository.ILineStationRepository;
import com.nado.smartcare.bus.service.ILineStationService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class LineStationServiceImpl implements ILineStationService{

	@Autowired
	private final ILineStationRepository iLineStationRepository;

	@Autowired
	private final IBusLineRepository iBusLineRepository;

	@Autowired
	private final RestTemplate restTemplate;

	@Value("${gwangju.lineStation.api.url}")
	private String lineStationApiUrl;

	@Value("${gwangju.api.key}")
	private String apiKey;

//	@PostConstruct
//	public void init() {
//		saveLineStationFromApi();
//	}

	@Override
	@Transactional
	public void saveLineStations(List<LineStation> lineStations) {
//		iLineStationRepository.saveAll(lineStations);
	}

	@Override
	@Transactional
	public void saveLineStationFromApi() {
		List<BusLine> busLines = iBusLineRepository.findAll();
		if (busLines.isEmpty()) {
			log.error("버스 노선 정보를 찾을 수 없습니다.");
			return;
		}

		ObjectMapper mapper = new ObjectMapper();
		List<LineStation> lineStations = new ArrayList<>();

		for (BusLine busLine : busLines) {
			String lineId = String.valueOf(busLine.getLineId());
			String url = lineStationApiUrl + "?serviceKey=" + apiKey + "&LINE_ID=" + lineId;

			ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				String responseBody = responseEntity.getBody();

				try {
					JsonNode root = mapper.readTree(responseBody);
					JsonNode lineStationListNode = root.path("BUSSTOP_LIST");

					List<LineStationDTO> lineStationDTOList = new ArrayList<>();
					for (JsonNode lineStationNode : lineStationListNode) {
						try {
							LineStationDTO lineStationDTO = mapper.treeToValue(lineStationNode, LineStationDTO.class);
							lineStationDTOList.add(lineStationDTO);
						} catch (Exception e) {
							log.error("정류소 정보 파싱 중 오류 발생 : {}", e.getMessage());
						}
					}

					List<LineStation> stations = lineStationDTOList.stream()
							.map(this::dtoToEntity)
							.collect(Collectors.toList());

					lineStations.addAll(stations);

				} catch (Exception e) {
					log.error("노선 {}의 정류소 정보 파싱 중 오류 발생 : {}", lineId, e.getMessage());
				}
			} else {
				log.error("노선 {}의 정류소 정보를 가져오는 중 오류 발생. 상태 코드: {}", lineId, responseEntity.getStatusCode());
			}
		}

		if (!lineStations.isEmpty()) {
			saveLineStations(lineStations);
		} else {
			log.error("저장할 정류소 정보가 없습니다.");
		}

		log.info("모든 데이터 값을 넣는 작업이 끝났습니다.");
	}

}
