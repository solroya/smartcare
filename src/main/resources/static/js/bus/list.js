document.addEventListener("DOMContentLoaded", async () => {
    // URL 쿼리 파라미터에서 좌표 값을 가져옵니다.
    const params = new URLSearchParams(window.location.search);
    const startLat = parseFloat(params.get("startLat"));
    const startLng = parseFloat(params.get("startLng"));
    const foodLat = parseFloat(params.get("foodLat"));
    const foodLng = parseFloat(params.get("foodLng"));

    if (isNaN(startLat) || isNaN(startLng) || isNaN(foodLat) || isNaN(foodLng)) {
        console.error("유효하지 않은 좌표입니다.");
        return;
    }

    // 추천 경로 API 호출
    const routes = await fetchRecommendedRoutes(startLat, startLng, foodLat, foodLng);
    if (routes.length > 0) {
        renderBusRoutes(routes);
        renderRoutesOnMap(routes, startLat, startLng, foodLat, foodLng);
    } else {
        console.error("추천 경로 데이터를 가져오지 못했습니다.");
    }
});

// API에서 추천 경로 가져오기
async function fetchRecommendedRoutes(startLat, startLng, endLat, endLng) {
    const url = `/routes/recommend?startLat=${startLat}&startLng=${startLng}&endLat=${endLat}&endLng=${endLng}`;
    console.log("API 요청 URL:", url);
    try {
        const response = await fetch(url);
        if (response.ok) {
            const data = await response.json();
            console.log("추천 경로 응답:", data);
            return data;
        } else {
            console.error("API 호출 실패:", response.status);
        }
    } catch (error) {
        console.error("API 요청 중 오류 발생:", error);
    }
    return [];
}

// 추천 경로 리스트 렌더링
function renderBusRoutes(routes) {
    const busRoutesList = document.getElementById("busRoutes");
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
    alert(`경로 ${routeIndex + 1} 상세 정보를 표시합니다.`);
}

// 지도에 추천 경로 렌더링
function renderRoutesOnMap(routes, userLat, userLng, foodLat, foodLng) {
    const mapContainer = document.getElementById("map");
    const map = new kakao.maps.Map(mapContainer, {
        center: new kakao.maps.LatLng(userLat, userLng),
        level: 4,
    });

    // 출발지 및 음식점 마커 표시
    new kakao.maps.Marker({
        position: new kakao.maps.LatLng(userLat, userLng),
        map: map,
        title: "출발지"
    });
    new kakao.maps.Marker({
        position: new kakao.maps.LatLng(foodLat, foodLng),
        map: map,
        title: "음식점"
    });

    routes.forEach((route) => {
        if (!route.coordinates) return;
        const path = JSON.parse(route.coordinates).map(coord => new kakao.maps.LatLng(coord[1], coord[0]));
        const polyline = new kakao.maps.Polyline({
            path: path,
            strokeWeight: 5,
            strokeColor: route.type === "도보" ? "#FF0000" : "#0000FF",
            strokeOpacity: 0.8,
            strokeStyle: "solid",
        });
        polyline.setMap(map);
    });
}
