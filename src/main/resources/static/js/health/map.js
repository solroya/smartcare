document.addEventListener("DOMContentLoaded", () => {
    if (typeof kakao === "undefined") {
        console.error("⚠ Kakao API가 로드되지 않았습니다.");
        return;
    }

    console.log("✅ Kakao API가 정상적으로 로드되었습니다!");

    const mapContainer = document.getElementById("map");
    let map;
    let marker;

    // 현재 위치 가져오기
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const userLat = position.coords.latitude;
                const userLng = position.coords.longitude;

                console.log("📌 현재 사용자 위치:", userLat, userLng);

                const mapOptions = {
                    center: new kakao.maps.LatLng(userLat, userLng), // 사용자 현재 위치로 초기화
                    level: 4 // 확대 수준
                };

                map = new kakao.maps.Map(mapContainer, mapOptions);

                // 사용자 위치에 마커 추가
                marker = new kakao.maps.Marker({
                    position: mapOptions.center,
                    map: map
                });

                console.log("✅ 지도 초기화 완료!");
            },
            (error) => {
                console.error("❌ 위치 정보를 가져오는 데 실패했습니다.", error);

                // 위치 실패 시 기본 위치(광주)로 초기화
                const defaultOptions = {
                    center: new kakao.maps.LatLng(35.15304, 126.84894), // 기본 위치
                    level: 4
                };

                map = new kakao.maps.Map(mapContainer, defaultOptions);

                // 기본 위치에 마커 추가
                marker = new kakao.maps.Marker({
                    position: defaultOptions.center,
                    map: map
                });

                console.log("✅ 기본 위치로 지도 초기화 완료!");
            }
        );
    } else {
        console.error("❌ Geolocation API가 지원되지 않는 브라우저입니다.");

        // 위치 실패 시 기본 위치(광주)로 초기화
        const defaultOptions = {
            center: new kakao.maps.LatLng(35.15304, 126.84894),
            level: 4
        };

        map = new kakao.maps.Map(mapContainer, defaultOptions);

        marker = new kakao.maps.Marker({
            position: defaultOptions.center,
            map: map
        });

        console.log("✅ 기본 위치로 지도 초기화 완료!");
    }

    // 모든 음식점 리스트 항목 가져오기
    const listItems = document.querySelectorAll(".foodListBox ul li");

    listItems.forEach((listItem) => {
        const latitude = listItem.getAttribute("data-latitude");
        const longitude = listItem.getAttribute("data-longitude");

        if (!latitude || !longitude) {
            console.error("❌ 데이터 속성이 없습니다:", listItem);
            return;
        }

        // 클릭 이벤트 추가
        listItem.addEventListener("click", () => {
            console.log("✅ 음식점 선택됨:", listItem.textContent);

            const lat = parseFloat(latitude);
            const lng = parseFloat(longitude);

            if (isNaN(lat) || isNaN(lng)) {
                console.error("❌ 유효하지 않은 좌표:", lat, lng);
                return;
            }

            const position = new kakao.maps.LatLng(lat, lng);

            // 마커 위치 변경
            marker.setPosition(position);

            // 지도 중심 이동
            map.panTo(position);
            console.log("✅ 지도와 마커 이동 완료!");
        });
    });

    // 윈도우 리사이즈 시 지도 크기 재조정
    window.addEventListener("resize", () => {
        map.relayout();
        console.log("✅ 지도 크기 재조정 완료!");
    });
});
