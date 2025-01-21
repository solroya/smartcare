package com.nado.smartcare.food.DataInitializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nado.smartcare.food.entity.FoodPlace;
import com.nado.smartcare.food.repository.IFoodPlaceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class CsvDataInitializer implements CommandLineRunner {
	
	private final IFoodPlaceRepository iFoodPlaceRepository;
	
	@Override
	public void run(String... args) throws Exception {
		
//		Path csvPath = Paths.get("D:\\spring boot\\문서파일\\foodPlace.csv");
//		
//		List<FoodPlace> foodPlaces = Files.lines(csvPath)
//			.skip(1)
//			.map(this::csvToFoodPlace)
//			.collect(Collectors.toList());
//		
//		iFoodPlaceRepository.saveAll(foodPlaces);
//		
//		log.info("CSV 데이터를 DB에 저장했습니다.");
		
	}
	
	private FoodPlace csvToFoodPlace(String line) {
		String[] data = line.split(",");
		return FoodPlace.builder()
			.foodName(data[1].trim())
			.foodAddressRoad(data[2].trim())
			.foodAddressJibun(data[3].trim())
			.foodbusinessHours(data[4].trim())
			.foodPhoneNumber(data[5].trim())
			.latitude(Double.parseDouble(data[6].trim()))
			.longitude(Double.parseDouble(data[7].trim()))
			.likes(Integer.parseInt(data[8].trim()))
			.views(Integer.parseInt(data[9].trim()))
			.category(data[10].trim())
			.imageUrl(data[11].trim())
			.build();
	}
	
}
