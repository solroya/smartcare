package com.nado.smartcare.tmap.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nado.smartcare.tmap.service.ITMapService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@Log4j2
@RequiredArgsConstructor
public class TMapServiceImpl implements ITMapService {
	
	@Value("${tmap.api.key}")
	private String tmapApiKey;
	
	public String getTransitRoutes(String startX, String startY , String endX, String endY) {
		OkHttpClient client = new OkHttpClient();
		
		try {
			MediaType mediaType = MediaType.parse("application/json");
			String requestBodyJson = String.format("{\"startY\":\"%s\",\"startX\":\"%s\",\"endY\":\"%s\",\"endX\":\"%s\",\"lang\":0,\"format\":\"json\",\"count\":10}",
					startX , startY , endX , endY );
			
			RequestBody body = RequestBody.create(requestBodyJson, mediaType);
			
			log.info("body안의 값은? ==> : {}" , body);
			
			
			String url = "https://apis.openapi.sk.com/transit/routes";
			
			
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.addHeader("accept", "application/json")
					.addHeader("content-type", "application/json")
					.addHeader("appKey", tmapApiKey)
					.build();
			log.info("request안의 값은? ==> : {}" , request);
			
			try(Response response = client.newCall(request).execute()) {
				log.info("response안의 값은? ==> : {}" , response);
				
				if (response.isSuccessful() && response.body() != null) {
					String responseBody = response.body().string();
					log.info("responseBody 값은? ==> : {}" , responseBody);
					return responseBody;
				} else {
					log.error("버스 API 호출 실패 - 상태 코드 : {}" , response.code());
					return "\"error\":\"API 호출 실패 - 상태 코드: " + response.code() + "\"}";
				}
			}
					
			
		} catch (Exception e) {
			log.error("버스 API 호출 중 예외 발생 : {}" , e.getMessage());
			return "{\"error\":\"예상치 못한 오류 발생: }" + e.getMessage() + "\"}";
		}
	}
    
}