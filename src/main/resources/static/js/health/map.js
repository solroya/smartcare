document.addEventListener("DOMContentLoaded", () => {
    if (typeof kakao === "undefined") {
        console.error("Kakao API가 로드되지 않았습니다.");
        return;
    }
    console.log("Kakao API 로드됨!");

    const mapContainer = document.getElementById("map");
    if (!mapContainer) {
        console.warn("#map 요소를 찾을 수 없습니다.");
        return;
    }

    const defaultCenter = new kakao.maps.LatLng(35.15304, 126.84894);
    window.map = new kakao.maps.Map(mapContainer, {
        center: defaultCenter,
        level: 4
    });

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            position => {
                const userLat = position.coords.latitude;
                const userLng = position.coords.longitude;
                console.log("현재 사용자 위치:", userLat, userLng);
                const moveLatLon = new kakao.maps.LatLng(userLat, userLng);
                window.map.setCenter(moveLatLon);

                const marker = new kakao.maps.Marker({ position: moveLatLon });
                marker.setMap(window.map);
            },
            error => {
                console.error("위치정보 실패:", error);
            }
        );
    }

    window.addEventListener("resize", () => {
        if (window.map) {
            window.map.relayout();
            console.log("지도 크기 재조정!");
        }
    });
});
