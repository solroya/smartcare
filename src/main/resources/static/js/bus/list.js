let map;
let mapOverlays = [];

document.addEventListener("DOMContentLoaded", async () => {
	console.log("bus/list.js DOMContentLoaded");
	
	const params = new URLSearchParams(window.location.search);
	const startLat = parseFloat(params.get("startLat"));
	const startLng = parseFloat(params.get("startLng"));
	const foodLat = parseFloat(params.get("foodLat"));
	const foodLng = parseFloat(params.get("foodLng"));
	
	if ([startLat, startLng, foodLat, foodLng].some(isNaN)) {
		console.error("유효하지 않은 좌표입니다.");
		return;
	}
	
	const routes = await fetchRecommendedRoutes(startLat, startLng, foodLat, foodLng);
	
	if (!routes || routes.length === 0) {
		console.warn("추천 경로가 없습니다.");
		return;
	}
	
	renderRouteList(routes);
	initMap(startLat, startLng);
});

async function fetchRecommendedRoutes(startX, startY, endX, endY) {
	const url = `/routes/recommend?startX=${startX}&startY=${startY}&endX=${endX}&endY=${endY}`;
	console.log("API 요청 URL", url);
	
	try {
		const response = await fetch(url);
		if (!response.ok) {
			console.error("API 호출 실패 :", response.status);
			return [];
		}
		const data = await response.json();
		console.log("추천 경로 응답 : ", data);
		
		return data.metaData.plan.itineraries || [];
	} catch (error) {
		console.error("API 요청 오류 :", error);
		return [];
	}
}

function initMap(startLat, startLng) {
	const mapContainer = document.getElementById("map");
	if (!mapContainer) {
		console.error("#Map 요소가 없습니다.");
		return;
	} const mapOption = {
		center : new kakao.maps.LatLng(startLat, startLng),
		lever : 5
	};
	
	map = new kakao.maps.Map(mapContainer, mapOption);
	
}

function renderRouteList(routes) {
	const routeContainer = document.getElementById("routeContainer");
	if (!routeContainer) {
		console.error("#routeContainer 요소가 없습니다.");
		return;
	}
	
	routeContainer.innerHTML = "";
	
	routes.forEach((route, index) => {
		const routeItem = document.createElement("div");
		routeItem.classList.add("routeItem");
		routeItem.dataset.index = index;
		
		const totalFare = route.fare?.regular?.totalFare || 0;
		const totalTime = route.totalTime || 0;
		const totalDistance = route.totalDistance || 0;
		const transferCount = route.transferCount || 0;
		const totalMinutes = Math.ceil(totalTime / 60);
		
		routeItem.innerHTML = `
			<div class="routeSummary">
				<p><strong>총 요금:</strong> ${totalFare}원</p>
				<p><strong>총 이동 시간:</strong> ${totalMinutes}분</p>
				<p><strong>총 이동 거리:</strong> ${(totalDistance / 1000).toFixed(1)}km</p>
				<p><strong>환승 횟수:</strong> ${transferCount}회</p>
			</div>
			<button class="showRouteBtn" data-index="${index}">경로 보기</button>
			<ul class="routeDetails">
				${route.legs.map((leg) => renderLeg(leg)).join("")}
			</ul>`;
			
		routeContainer.appendChild(routeItem);
		console.log("올바르게 가져온 버스 요금:", totalFare);
	});
	
	document.querySelectorAll(".showRouteBtn").forEach(button => {
		button.addEventListener("click", function () {
			const index = this.dataset.index;
			showRouteOnMap(routes[index]);
		});
	});
}

function showRouteOnMap(route) {
	if (!map) return;
	
	mapOverlays.forEach(overlay => overlay.setMap(null));
	mapOverlays = [];
	
	addMarker(route.legs[0].start.lat, route.legs[0].start.lon, "출발지");
	addMarker(route.legs[route.legs.length - 1].end.lat, route.legs[route.legs.length - 1].end.lon, "도착지");
	
	route.legs.forEach(leg => {
		let pathCoords = [];
		
		if (leg.passShape && leg.passShape.linestring) {
			pathCoords = parseLinestring(leg.passShape.linestring);
		}
		
		if (leg.mode === "WALK") {
			if (pathCoords.length < 2) {
				console.warn("도보 경로가 부족하여 출발지부터 정류장까지 연결합니다.");
				pathCoords = [
					new kakao.maps.LatLng(leg.start.lat, leg.start.lon),
					new kakao.maps.LatLng(leg.end.lat, leg.end.lon)
				];
			}
		}
		
		drawRouteOnMap(pathCoords, leg.mode);
	});
}

function addMarker(lat, lng, title) {
	const markerPosition = new kakao.maps.LatLng(lat, lng);
	const marker = new kakao.maps.Marker({
		position : markerPosition,
		map : map,
		title : title
	});
	
	mapOverlays.push(marker);
}

function drawRouteOnMap(pathCoords, mode) {
	let strokeColor = mode === "WALK" ? "#0000FF" : "#FF9800";
	
	const polyline = new kakao.maps.Polyline({
		path : pathCoords,
		strokeWeight : 5,
		strokeColor : strokeColor,
		strokeOpacity : 0.8,
		strokeStyle : "solid"
	});
	
	polyline.setMap(map);
	mapOverlays.push(polyline);
}

function parseLinestring(linestring) {
	const coordinates = linestring.split(" ").map(coord => {
		const [lng, lat] = coord.split(",").map(Number);
		return new kakao.maps.LatLng(lat, lng);
	});
	
	return coordinates;
}

function renderLeg(leg) {
	const mode = leg.mode;
	const startName = leg.start.name || "출발지";
	const endName = leg.end.name || "도착지";
	const distance = leg.distance || 0;
	const sectionTime = Math.ceil(leg.sectionTime / 60);
	const fare = leg.fare?.totalFare || 0;
	
	if (mode === "WALK") {
		return `
			<li class="leg walk">
				<p>도보 이동</p>
				<p>출발: ${startName}</p>
				<p>도착: ${endName}</p>
				<p>${distance}m (${sectionTime}분 소요)</p>
			</li>`;
	} else if (mode === "BUS") {
		const routeName = leg.route || "버스";
		const routeColor = leg.routeColor ? `#${leg.routeColor}` : "#000000";
		
		return `
			<li class="leg bus">
				<p><strong style=""color:${routeColor}">${routeName}</strong></p>
				<p>출발 정류장: ${startName}></p>
				<p>도착 정류장: ${endName}></p>
				<p>${(distance / 1000).toFixed(1)}km (${sectionTime}분 소요)</p>
				<p>요금: ${fare}원</p>
			</li>`;
	}
	return "";
}