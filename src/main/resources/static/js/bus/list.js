// 전역 변수: 추천 경로 목록, 카카오맵 인스턴스, 지도 오버레이 저장 배열
let routes = [];
window.map = null;
window.mapOverlays = [];

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

    routes = await fetchRecommendedRoutes(startLat, startLng, foodLat, foodLng);
    if (!routes || routes.length === 0) {
        console.warn("추천 경로가 없습니다.");
        return;
    }

    renderBusRoutes(routes);
    renderRoutesOnMap(routes, startLat, startLng, foodLat, foodLng);
});

async function fetchRecommendedRoutes(startLat, startLng, endLat, endLng) {
    const url = `/routes/recommend?startLat=${startLat}&startLng=${startLng}&endLat=${endLat}&endLng=${endLng}`;
    console.log("API 요청 URL:", url);
    try {
        const response = await fetch(url);
        if (!response.ok) {
            console.error("API 호출 실패:", response.status);
            return [];
        }
        const data = await response.json();
        console.log("추천 경로 응답:", data);
        // 추천 경로 객체에는 detailedSteps 필드가 포함되어 있다고 가정
        return data;
    } catch (error) {
        console.error("API 요청 오류:", error);
        return [];
    }
}

function renderBusRoutes(routes) {
    const busRoutesList = document.getElementById("busRoutes");
    if (!busRoutesList) {
        console.error("#busRoutes 요소가 없습니다.");
        return;
    }
    busRoutesList.innerHTML = "";

    routes.forEach((route, index) => {
        const routeItem = document.createElement("li");
        routeItem.classList.add("routeItem");
        routeItem.innerHTML = `
            <div class="routeHeader">
                <p>${route.time}분</p>
                <p>${route.distance}m</p>
            </div>
            <div class="routeContent">
                <p>${route.description}</p>
                <button class="detailButton" onclick="viewRouteDetail(${index})">상세보기</button>
            </div>
        `;
        busRoutesList.appendChild(routeItem);
    });
}

function viewRouteDetail(routeIndex) {
    const selectedRoute = routes[routeIndex];
    let detailedSteps = selectedRoute.detailedSteps;
    if (!detailedSteps || detailedSteps.length === 0) {
        detailedSteps = [
            {
                type: "도보",
                instruction: "출발지에서 도보로 이동",
                time: 4,
                distance: 211,
                transfer: 0,
                fare: 0,
                totalDistance: 0.211,
                coordinates: JSON.stringify([[126.8956354, 35.1575567], [126.8957000, 35.1576000]]),
                startLat: 35.1575567,
                startLng: 126.8956354
            },
            {
                type: "버스",
                instruction: "버스 탑승 (환승 1회)",
                time: 14,
                distance: 0,
                transfer: 1,
                fare: 1250,
                totalDistance: 0,
                coordinates: null,
                startLat: null,
                startLng: null
            },
            {
                type: "도보",
                instruction: "버스 정류장에서 도착지까지 도보 이동",
                time: 17,
                distance: 21,
                transfer: 0,
                fare: 0,
                totalDistance: 0.021,
                coordinates: JSON.stringify([[126.8880254, 35.14818001], [126.8881000, 35.1482000]]),
                startLat: 35.14818001,
                startLng: 126.8880254
            }
        ];
    }
    
    let detailHtml = '<div class="routeDetail">';
    detailedSteps.forEach((step) => {
        const instruction = step.instruction || "";
        const time = (step.time === undefined) ? 0 : step.time;
        const distance = (step.distance === undefined) ? 0 : step.distance;
        const transfer = (step.transfer === undefined || step.transfer <= 0) ? "없음" : step.transfer;
        const fare = (step.fare === undefined) ? 0 : step.fare;
        const totalDistance = (step.totalDistance === undefined) ? 0 : step.totalDistance;
        
        detailHtml += `
          <div class="routeStep">
            <p class="step-instruction">${instruction}</p>
            <p class="step-info">
              ${time}분, ${distance}m, 
              ${transfer !== "없음" ? '환승 ' + transfer + '회' : '환승 없음'}, 
              요금 ${fare}원, 총 거리 ${totalDistance}km
            </p>
          </div>
        `;
    });
    detailHtml += '</div>';
    
    const detailContainer = document.getElementById("routeDetailContainer");
    detailContainer.innerHTML = detailHtml;
    detailContainer.style.display = "block";
    
    updateMapWithDetailedRoute(detailedSteps);
}

function updateMapWithDetailedRoute(steps) {
    if (!window.map) {
        console.error("지도 인스턴스가 없습니다.");
        return;
    }
    
    // 기존 오버레이 제거
    if (window.mapOverlays && window.mapOverlays.length > 0) {
        window.mapOverlays.forEach(overlay => overlay.setMap(null));
        window.mapOverlays = [];
    } else {
        window.mapOverlays = [];
    }
    
    let bounds = new kakao.maps.LatLngBounds();
    
    steps.forEach(step => {
        if (step.coordinates) {
            try {
                const pathArr = JSON.parse(step.coordinates);
                let enhancedPath = [];
                // 버스 구간이면 Catmull-Rom 보간 적용, 그렇지 않으면 원래 배열 사용
                if (step.type === "버스") {
                    enhancedPath = getCatmullRomSpline(pathArr, 20);
                } else {
                    enhancedPath = pathArr;
                }
                
                // enhancedPath의 각 요소가 유효한 배열(길이가 2 이상)인지 확인 후 변환
                const path = enhancedPath
                    .filter(coord => Array.isArray(coord) && coord.length >= 2)
                    .map(coord => {
                        const latlng = new kakao.maps.LatLng(coord[1], coord[0]);
                        bounds.extend(latlng);
                        return latlng;
                    });
                
                // step.type에 따라 색상 지정 (도보: 파란색, 버스: 빨간색)
                const strokeColor = (step.type === "도보") ? "#0000FF" : "#FF0000";
                const polyline = new kakao.maps.Polyline({
                    path: path,
                    strokeWeight: 5,
                    strokeColor: strokeColor,
                    strokeOpacity: 0.8,
                    strokeStyle: "solid"
                });
                polyline.setMap(window.map);
                window.mapOverlays.push(polyline);
            } catch (e) {
                console.error("폴리라인 그리기 실패:", e);
            }
        }
        // 각 단계의 시작점 마커 추가
        if (step.startLat && step.startLng) {
            const markerPosition = new kakao.maps.LatLng(step.startLat, step.startLng);
            bounds.extend(markerPosition);
            const marker = new kakao.maps.Marker({
                position: markerPosition,
                map: window.map,
                title: step.instruction || "상세 경로"
            });
            window.mapOverlays.push(marker);
        }
    });
    
    window.map.setBounds(bounds);
}

function renderRoutesOnMap(routes, userLat, userLng, foodLat, foodLng) {
    const mapContainer = document.getElementById("map");
    if (!mapContainer) {
        console.error("#map 요소가 없습니다.");
        return;
    }
    const mapOption = {
        center: new kakao.maps.LatLng(userLat, userLng),
        level: 4
    };
    window.map = new kakao.maps.Map(mapContainer, mapOption);
    
    // 출발지 및 음식점 마커 표시
    new kakao.maps.Marker({
        position: new kakao.maps.LatLng(userLat, userLng),
        map: window.map,
        title: "출발지"
    });
    new kakao.maps.Marker({
        position: new kakao.maps.LatLng(foodLat, foodLng),
        map: window.map,
        title: "음식점"
    });
    
    routes.forEach(route => {
        if (route.coordinates) {
            try {
                const pathArr = JSON.parse(route.coordinates);
                const path = pathArr.map(coord => new kakao.maps.LatLng(coord[1], coord[0]));
                const strokeColor = (route.type === "도보") ? "#0000FF" : "#FF0000";
                const polyline = new kakao.maps.Polyline({
                    path: path,
                    strokeWeight: 5,
                    strokeColor: strokeColor,
                    strokeOpacity: 0.8,
                    strokeStyle: "solid"
                });
                polyline.setMap(window.map);
            } catch (e) {
                console.error("폴리라인 그리기 실패:", e);
            }
        }
    });
}

// 선형 보간 함수: 두 좌표 사이에 stepsCount개의 중간 좌표 생성
function interpolateCoordinates(coord1, coord2, stepsCount) {
    const result = [];
    const [lon1, lat1] = coord1;
    const [lon2, lat2] = coord2;
    for (let i = 1; i < stepsCount; i++) {
        const t = i / stepsCount;
        const lon = lon1 + (lon2 - lon1) * t;
        const lat = lat1 + (lat2 - lat1) * t;
        result.push([lon, lat]);
    }
    return result;
}

function getCatmullRomSpline(points, resolution) {
    let spline = [];
    for (let i = 0; i < points.length - 1; i++) {
        let p0 = points[i - 1] || points[i];
        let p1 = points[i];
        let p2 = points[i + 1];
        let p3 = points[i + 2] || points[i + 1];
        for (let t = 0; t < 1; t += 1 / resolution) {
            let t2 = t * t;
            let t3 = t2 * t;
            let x = 0.5 * ((2 * p1[0]) +
                (-p0[0] + p2[0]) * t +
                (2 * p0[0] - 5 * p1[0] + 4 * p2[0] - p3[0]) * t2 +
                (-p0[0] + 3 * p1[0] - 3 * p2[0] + p3[0]) * t3);
            let y = 0.5 * ((2 * p1[1]) +
                (-p0[1] + p2[1]) * t +
                (2 * p0[1] - 5 * p1[1] + 4 * p2[1] - p3[1]) * t2 +
                (-p0[1] + 3 * p1[1] - 3 * p2[1] + p3[1]) * t3);
            spline.push([x, y]);
        }
    }
    spline.push(points[points.length - 1]); // 마지막 점 추가
    return spline;
}

function combineCoordinates(steps) {
    let combined = [];
    for (let i = 0; i < steps.length; i++) {
        const coordsStr = steps[i].coordinates;
        if (coordsStr != null && coordsStr.trim().length > 0) {
            try {
                let coords = JSON.parse(coordsStr);
                combined = combined.concat(coords);
            } catch (e) {
                console.error("좌표 파싱 오류: ", e);
            }
        }
    }
    try {
        return JSON.stringify(combined);
    } catch (e) {
        console.error("좌표 직렬화 오류: ", e);
        return null;
    }
}