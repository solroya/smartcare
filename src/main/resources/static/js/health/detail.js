document.addEventListener("DOMContentLoaded", () => {
    const detailHeartClickHandler = async () => {
        const detailElement = document.querySelector(".detail");
		console.log("fno 값:", fno);
        if (!detailElement) {
            console.error(".detail 요소를 찾을 수 없습니다.");
            return;
        }

        const foodPlaceId = detailElement.getAttribute("data-fno");
        if (!foodPlaceId || foodPlaceId === "null") {
            console.error("data-fno 속성이 설정되지 않았거나 'null'입니다.");
            return;
        }

        const heartImg = document.querySelector(".heart-img");
        const likesCount = document.querySelector(".detail .clickBtn b");

        try {
            const response = await fetch(`/food/like/${foodPlaceId}`, { method: "POST" });
            if (response.ok) {
                const isLiked = await response.json();

                // 좋아요 상태 업데이트
                heartImg.src = isLiked
                    ? "/img/health/heartOn.png"
                    : "/img/health/heartOff.png";

                const currentLikes = parseInt(likesCount.textContent, 10);
                likesCount.textContent = isLiked
                    ? currentLikes + 1
                    : currentLikes - 1;

                // 리스트와 동기화
                const listHeartImg = document.querySelector(`.foodList [data-fno="${foodPlaceId}"] .heart img`);
                const listLikesCount = document.querySelector(`.foodList [data-fno="${foodPlaceId}"] .likes-count`);
                if (listHeartImg && listLikesCount) {
                    listLikesCount.textContent = likesCount.textContent;
                    listHeartImg.src = heartImg.src;
                }
            } else {
                console.error("좋아요 요청 실패", response.status);
            }
        } catch (error) {
            console.error("좋아요 요청 중 오류 발생", error);
        }
    };

    // 상세 페이지 하트 클릭 이벤트 바인딩
    const heartImg = document.querySelector(".heart-img");
    if (heartImg) {
        heartImg.addEventListener("click", (event) => {
            event.stopPropagation(); // 클릭 이벤트가 상위로 전파되지 않도록 차단
            detailHeartClickHandler();
        });
    }

});

document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ DOMContentLoaded 실행됨!");

    // ✅ `#detail` 요소 변경 감지
    const observer = new MutationObserver(() => {
        const comentForm = document.getElementById("comentForm");
        if (comentForm) {
            console.log("✅ `comentForm`이 동적으로 추가됨!", comentForm);
            observer.disconnect(); // 더 이상 감지할 필요 없음
            initComentForm(); // `comentForm` 이벤트 핸들러 초기화
        }
    });

    // `#detail` 내부의 변화를 감지
    observer.observe(document.getElementById("detail"), { childList: true, subtree: true });
});

// ✅ `comentForm`이 동적으로 추가되면 실행할 함수
function initComentForm() {
    const comentForm = document.getElementById("comentForm");
    if (!comentForm) {
        console.error("❌ `comentForm` 요소를 찾을 수 없습니다!");
        return;
    }

    console.log("✅ `comentForm` 찾음. 이벤트 바인딩 시작!");

    // ✅ 별점 요소 가져오기
    const stars = document.querySelectorAll(".stars .star");
    if (stars.length === 0) {
        console.error("❌ 별점 요소를 찾을 수 없습니다.");
        return;
    }
    console.log("✅ 별점 요소 발견:", stars);

    // ⭐ 별점 클릭 이벤트 핸들러
    stars.forEach((star) => {
        star.addEventListener("click", () => {
            const rating = star.getAttribute("data-rating");
            console.log(`⭐ 선택된 평점: ${rating}`);
            document.getElementById("rating").value = rating;

            stars.forEach((s, index) => {
                if (index < rating) {
                    s.classList.add("selected");
                } else {
                    s.classList.remove("selected");
                }
            });
        });
    });

    // 📩 댓글 등록 이벤트 핸들러
    comentForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const fnoElement = document.querySelector(".detail");
        if (!fnoElement) {
            console.error("❌ `.detail` 요소를 찾을 수 없습니다.");
            return;
        }
        const fno = fnoElement.getAttribute("data-fno");
        if (!fno) {
            console.error("❌ `data-fno` 값이 존재하지 않습니다.");
            return;
        }

        const content = document.getElementById("comentContent").value.trim();
        const author = document.getElementById("comentAuthor").value.trim();
        const rating = document.getElementById("rating").value;

        if (!content || !author) {
            alert("내용과 작성자 이름을 입력해주세요.");
            return;
        }
        if (rating === "0") {
            alert("평점을 선택하세요.");
            return;
        }

        console.log(`📨 댓글 등록 요청 - fno: ${fno}, content: ${content}, author: ${author}, rating: ${rating}`);

        try {
            const response = await fetch(`/food/coment/${fno}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ content, author, rating }),
            });

            if (response.ok) {
                const { savedComent, averageRating } = await response.json();
                console.log("✅ 저장된 댓글:", savedComent, "📊 평균 평점:", averageRating);

                // 새 댓글 추가
                const comentList = document.querySelector(".comentList ul");
                const newComent = `
                    <li>
                        <p>${savedComent.content}</p>
                        <div class="comentInfo">
                            <b>${savedComent.author}</b>
                            <small>${savedComent.createdAt}</small>
                            <span>${"★".repeat(savedComent.rating)}</span>
                        </div>
                    </li>`;
                comentList.innerHTML = newComent + comentList.innerHTML;

                // 댓글 개수 업데이트
                const comentHeader = document.querySelector(".comentHeader h3");
                const currentCount = parseInt(comentHeader.textContent.match(/\d+/)[0]);
                comentHeader.textContent = `댓글 ${currentCount + 1}개`;

                // 폼 초기화
				comentForm.reset();
                document.getElementById("rating").value = "0"; // 별점 초기화

                // ✅ 별점 선택 초기화
                stars.forEach((star) => star.classList.remove("selected"));
				
            } else {
                console.error("❌ 댓글 등록 실패", response.status);
            }
        } catch (error) {
            console.error("❌ 댓글 등록 요청 중 오류 발생", error);
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ DOMContentLoaded 실행됨");

    const observer = new MutationObserver((mutationsList, observer) => {
        const detailElement = document.querySelector(".detail");
        if (detailElement) {
            console.log("✅ '.detail' 요소가 추가되었습니다:", detailElement);
            initializeMapButton(detailElement);
            observer.disconnect();
        }
    });

    const detailWrap = document.querySelector(".detailwrap");
    if (detailWrap) {
        observer.observe(detailWrap, { childList: true, subtree: true });
        console.log("✅ MutationObserver가 활성화되었습니다.");
    } else {
        console.error("❌ '.detailwrap' 요소를 찾을 수 없습니다.");
    }
});

function initializeMapButton(detailElement) {
    console.log("✅ '지도로 보기' 버튼 초기화 시작");
    const mapButton = detailElement.querySelector(".detailTitle button");
    if (!mapButton) {
        console.error("❌ '지도로 보기' 버튼을 찾을 수 없습니다.");
        return;
    }
    mapButton.addEventListener("click", async () => {
        console.log("✅ '지도로 보기' 버튼 클릭됨");
        const fno = detailElement.getAttribute("data-fno");
        const latitude = parseFloat(detailElement.getAttribute("data-latitude"));
        const longitude = parseFloat(detailElement.getAttribute("data-longitude"));
        console.log("음식점 위도, 경도 : ", latitude, longitude);
        if (isNaN(latitude) || isNaN(longitude)) {
            console.error("❌ 음식점의 위치 정보가 유효하지 않습니다.");
            return;
        }
        if (!navigator.geolocation) {
            alert("현재 위치를 가져올 수 없습니다. 브라우저에서 위치 권한을 허용해주세요.");
            return;
        }
        navigator.geolocation.getCurrentPosition(async (position) => {
            let userLat = position.coords.latitude;
            let userLng = position.coords.longitude;
            console.log("📍 현재 위치 (디버깅): ", userLat, userLng);
            if (!userLat || !userLng) {
                console.error("❌ 현재 위치를 가져오지 못했습니다. 기본 위치를 설정합니다.");
                userLat = 35.1595454;
                userLng = 126.8526012;
            }
            console.log("📍 최종 사용 위치:", userLat, userLng);
            const routes = await fetchRecommendedRoutes(userLat, userLng, latitude, longitude);
            if (routes && routes.length > 0) {
                renderRoutesOnMap(routes, userLat, userLng, latitude, longitude);
            }
        });
    });
}

async function fetchRecommendedRoutes(startLat, startLng, endLat, endLng) {
    const url = `/routes/recommend?startLat=${startLat}&startLng=${startLng}&endLat=${endLat}&endLng=${endLng}`;
    console.log("API 요청 URL:", url);
    try {
        const response = await fetch(url);
        if (response.ok) {
            const routes = await response.json();
            console.log("추천 경로 응답: ", routes);
            return routes;
        } else {
            console.error("추천 경로 API 호출 실패", response.status);
        }
    } catch (error) {
        console.error("추천 경로 요청 중 오류 발생", error);
    }
    return [];
}

function renderRoutesOnMap(routes, userLat, userLng, foodLat, foodLng) {
    console.log("🗺 지도에 경로 렌더링 시작");
    const mapContainer = document.getElementById("map");
    const map = new kakao.maps.Map(mapContainer, {
        center: new kakao.maps.LatLng(userLat, userLng),
        level: 4,
    });
    new kakao.maps.Marker({
        position: new kakao.maps.LatLng(userLat, userLng),
        map: map,
        title: "현재 위치"
    });
    new kakao.maps.Marker({
        position: new kakao.maps.LatLng(foodLat, foodLng),
        map: map,
        title: "음식점 위치"
    });
    routes.forEach((route) => {
        // bus 경로의 경우 좌표가 없을 수 있으므로 체크
        if (!route.coordinates) return;
        const polylinePath = JSON.parse(route.coordinates);
        const path = polylinePath.map(coord => new kakao.maps.LatLng(coord[1], coord[0]));
        const strokeColor = route.type === "도보" ? "#FF0000" : "#0000FF";
        new kakao.maps.Polyline({
            path: path,
            strokeWeight: 5,
            strokeColor: strokeColor,
            strokeOpacity: 0.8,
            strokeStyle: "solid",
            map: map
        });
        if (route.type === "버스") {
            const endStation = path[path.length - 1];
            new kakao.maps.Marker({
                position: endStation,
                map: map,
                title: "버스 하차 정류장",
                image: new kakao.maps.MarkerImage(
                    "https://i.imgur.com/x9ZlI8x.png",
                    new kakao.maps.Size(32, 32)
                )
            });
        }
    });
    console.log("✅ 지도에 경로 렌더링 완료");
}