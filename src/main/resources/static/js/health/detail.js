// DOMContentLoaded 이벤트 후 실행: detail 페이지 초기화
document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ DOMContentLoaded 실행됨!");

    // 1. 'comentForm' 동적 추가 감지 및 초기화
    const commentObserver = new MutationObserver((mutationsList, observer) => {
        const commentForm = document.getElementById("comentForm");
        if (commentForm) {
            console.log("✅ `comentForm`이 동적으로 추가됨!", commentForm);
            observer.disconnect();
            initComentForm();
        }
    });
    const detailElement = document.getElementById("detail");
    if (detailElement) {
        commentObserver.observe(detailElement, { childList: true, subtree: true });
        console.log("✅ MutationObserver가 활성화되었습니다.");
    } else {
        console.error("❌ `#detail` 요소를 찾을 수 없습니다.");
    }

    // 2. 지도 버튼 초기화
    const detailWrap = document.querySelector(".detailwrap");
    if (detailWrap) {
        const mapObserver = new MutationObserver((mutations, observer) => {
            const detail = document.querySelector(".detail");
            if (detail) {
                console.log("✅ `.detail` 요소가 추가되었습니다:", detail);
                initializeMapButton(detail);
                observer.disconnect();
            }
        });
        mapObserver.observe(detailWrap, { childList: true, subtree: true });
        console.log("✅ 지도 버튼 감지 MutationObserver가 활성화되었습니다.");
    } else {
        console.error("❌ `.detailwrap` 요소를 찾을 수 없습니다.");
    }
});

// 댓글 폼 초기화 함수
function initComentForm() {
    const comentForm = document.getElementById("comentForm");
    if (!comentForm) {
        console.error("❌ `comentForm` 요소를 찾을 수 없습니다!");
        return;
    }
    console.log("✅ `comentForm` 찾음. 이벤트 바인딩 시작!");

    // 별점 이벤트 처리
    const stars = document.querySelectorAll(".stars .star");
    if (stars.length === 0) {
        console.error("❌ 별점 요소를 찾을 수 없습니다.");
        return;
    }
    console.log("✅ 별점 요소 발견:", stars);
    stars.forEach((star) => {
        star.addEventListener("click", () => {
            const rating = star.getAttribute("data-rating");
            console.log(`⭐ 선택된 평점: ${rating}`);
            document.getElementById("rating").value = rating;
            stars.forEach((s, index) => {
                s.classList.toggle("selected", index < rating);
            });
        });
    });

    // 댓글 등록 처리
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
                // 새 댓글을 목록에 추가
                const comentList = document.querySelector(".comentList ul");
                const newComentHTML = `
                    <li>
                        <p>${savedComent.content}</p>
                        <div class="comentInfo">
                            <b>${savedComent.author}</b>
                            <small>${savedComent.createdAt}</small>
                            <span>${"★".repeat(savedComent.rating)}</span>
                        </div>
                    </li>`;
                comentList.innerHTML = newComentHTML + comentList.innerHTML;
                // 댓글 개수 업데이트
                const comentHeader = document.querySelector(".comentHeader h3");
                const currentCount = parseInt(comentHeader.textContent.match(/\d+/)[0]);
                comentHeader.textContent = `댓글 ${currentCount + 1}개`;
                // 폼 초기화
                comentForm.reset();
                document.getElementById("rating").value = "0";
                stars.forEach((star) => star.classList.remove("selected"));
            } else {
                console.error("❌ 댓글 등록 실패", response.status);
            }
        } catch (error) {
            console.error("❌ 댓글 등록 요청 중 오류 발생", error);
        }
    });
}

// 지도 버튼 초기화 함수
function initializeMapButton(detailElement) {
    console.log("✅ '지도로 보기' 버튼 초기화 시작");
    const mapButton = detailElement.querySelector(".detailTitle button");
    if (!mapButton) {
        console.error("❌ '지도로 보기' 버튼을 찾을 수 없습니다.");
        return;
    }
    
    mapButton.addEventListener("click", () => {
        const fno = detailElement.getAttribute("data-fno");
        const foodLat = parseFloat(detailElement.getAttribute("data-latitude"));
        const foodLng = parseFloat(detailElement.getAttribute("data-longitude"));
        console.log("음식점 fno, 위도, 경도 :", fno, foodLat, foodLng);
        
        if (isNaN(foodLat) || isNaN(foodLng)) {
            console.error("❌ 음식점의 위치 정보가 유효하지 않습니다.");
            return;
        }
        
        if (!navigator.geolocation) {
            alert("현재 위치를 가져올 수 없습니다. 브라우저에서 위치 권한을 허용해주세요.");
            return;
        }
        
        navigator.geolocation.getCurrentPosition((position) => {
            let userLat = position.coords.latitude;
            let userLng = position.coords.longitude;
            console.log("📍 현재 위치 (디버깅): ", userLat, userLng);
            if (!userLat || !userLng) {
                console.error("❌ 현재 위치를 가져오지 못했습니다. 기본 위치를 설정합니다.");
                userLat = 35.1595454;
                userLng = 126.8526012;
            }
            console.log("📍 최종 사용 위치:", userLat, userLng);
            const url = `/bus/map?fno=${fno}&startLat=${userLat}&startLng=${userLng}&foodLat=${foodLat}&foodLng=${foodLng}`;
            console.log("이동할 URL:", url);
            window.location.href = url;
        }, (error) => {
            console.error("현재 위치를 가져오는 데 실패:", error);
            alert("현재 위치를 가져올 수 없습니다.");
        });
    });
}