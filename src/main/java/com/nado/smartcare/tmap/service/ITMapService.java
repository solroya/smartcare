package com.nado.smartcare.tmap.service;

public interface ITMapService {
	String getTransitRoutes(String startX, String startY, String endX, String endY);
	String getWalkingRoutes(String startX, String startY, String endX, String endY);
}
