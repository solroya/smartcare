let map;
let mapOverlays = [];

document.addEventListener("DOMContentLoaded", async () => {
    console.log("walking.js DOMContentLoaded");

    const params = new URLSearchParams(window.location.search);
    const startLat = parseFloat(params.get("startLat"));
    const startLng = parseFloat(params.get("startLng"));
    const foodLat = parseFloat(params.get("foodLat"));
    const foodLng = parseFloat(params.get("foodLng"));

    if ([startLat, startLng, foodLat, foodLng].some(isNaN)) {
        console.error("유효하지 않은 좌표입니다.");
        return;
    }

    const routes = await fetchWalkingRoutes(startLng, startLat, foodLng, foodLat);

    if (!routes || routes.features.length === 0) {
        console.warn("추천 보행자 경로가 없습니다.");
        return;
    }

    console.log("보행자 경로 응답 :", routes);

    initMap(startLat, startLng);
    renderRouteList(routes);
    renderWalkingRoute(routes);
});

async function fetchWalkingRoutes(startX, startY, endX, endY) {
    const url = `/routes/walking?startX=${startX}&startY=${startY}&endX=${endX}&endY=${endY}`;
    console.log("보행자 API 요청 URL", url);

    try {
        const response = await fetch(url);
        if (!response.ok) {
            console.error("보행자 API 호출 실패 :", response.status);
            return null;
        }
        return await response.json();
    } catch (error) {
        console.error("보행자 API 요청 오류 :", error);
        return null;
    }
}

function initMap(startLat, startLng) {
    const mapContainer = document.getElementById("map");
    if (!mapContainer) {
        console.error("#Map 요소가 없습니다.");
        return;
    }

    const mapOption = {
        center: new kakao.maps.LatLng(startLat, startLng),
        level: 5
    };

    map = new kakao.maps.Map(mapContainer, mapOption);
}

function renderWalkingRoute(routeData) {
    mapOverlays.forEach((overlay) => overlay.setMap(null));
    mapOverlays = [];

    const features = routeData.features;

    if (!features || features.length === 0) {
        console.warn("도보 경로 데이터 없음");
        return;
    }

    const startPoint = features[0].geometry.coordinates;
    addMarker(startPoint[1], startPoint[0], "출발지");

    let endPoint = null;
    for (let i = features.length - 1; i >= 0; i--) {
        if (features[i].geometry.type === "LineString") {
            endPoint = features[i].geometry.coordinates.slice(-1)[0];
            break;
        }
    }
    if (endPoint) {
        addMarker(endPoint[1], endPoint[0], "도착지");
    }

    let pathCoords = [];

    features.forEach((feature) => {
        if (feature.geometry.type === "LineString") {
            pathCoords = feature.geometry.coordinates.map((coord) => new kakao.maps.LatLng(coord[1], coord[0]));
            drawRouteOnMap(pathCoords, "WALK");
        }
    });

    console.log("도보 경로 지도에 표시");
}

function drawRouteOnMap(pathCoords, mode) {
    let strokeColor = mode === "WALK" ? "#0000FF" : "#FF9800";

    if (pathCoords.length < 2) {
        console.warn("경로가 부족하여 그릴 수 없음");
        return;
    }

    const polyline = new kakao.maps.Polyline({
        path: pathCoords,
        strokeWeight: 5,
        strokeColor: strokeColor,
        strokeOpacity: 0.8,
        strokeStyle: "solid",
    });

    polyline.setMap(map);
    mapOverlays.push(polyline);
}

function addMarker(lat, lng, title) {
    const markerPosition = new kakao.maps.LatLng(lat, lng);
    const marker = new kakao.maps.Marker({
        position: markerPosition,
        map: map,
        title: title,
    });

    mapOverlays.push(marker);
}

function renderRouteList(routes) {
    const routeContainer = document.getElementById("routeContainer");
    if (!routeContainer) {
        console.error("#routeContainer 요소가 없습니다.");
        return;
    }

    routeContainer.innerHTML = "";

    const totalDistance = routes.features.reduce((sum, feature) => sum + (feature.properties.distance || 0), 0);
    const totalTime = routes.features.reduce((sum, feature) => sum + (feature.properties.time || 0), 0);
    const totalMinutes = Math.ceil(totalTime / 60);

    const routeItem = document.createElement("div");
    routeItem.classList.add("routeItem");

    routeItem.innerHTML = `
        <div class="routeSummary">
            <p><strong>${totalMinutes}분</strong> | ${(totalDistance / 1000).toFixed(1)}km</p>
            <button class="showRouteBtn">경로 보기</button>
        </div>
        <ul class="routeDetails" style="display: none;">
            ${routes.features.map(renderLeg).join("")}
        </ul>`;

    routeContainer.appendChild(routeItem);

    document.querySelector(".showRouteBtn").addEventListener("click", function () {
        const routeDetails = this.parentElement.nextElementSibling;
        routeDetails.style.display = routeDetails.style.display === "none" ? "block" : "none";
    });
}

function renderLeg(feature) {
    const description = feature.properties.description || "도보 이동";
    const distance = feature.properties.distance || 0;
    const time = Math.ceil((feature.properties.time || 0) / 60);
	
	if (distance === 0) return "";
	
    return `
        <li class="leg walk">
            <p><strong>🚶 ${description}</strong></p>
            <p>${distance}m (${time}분 소요)</p>
        </li>`;
}
