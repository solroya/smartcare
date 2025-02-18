document.addEventListener("DOMContentLoaded", () => {
    console.log("detail.js DOMContentLoaded 실행됨!");
    restoreLikeStatus();  // 좋아요 상태 복원

    const foodListContainer = document.querySelector(".foodList");
    if (!foodListContainer) return;

    foodListContainer.addEventListener("click", (event) => {
        // 리스트 아이템 클릭 시, 아이템 전체가 상세 페이지로 이동하도록 설정
        const listItem = event.target.closest("li");
        if (listItem) {
            const fno = listItem.getAttribute("data-fno");
            const latitude = listItem.getAttribute("data-latitude");
            const longitude = listItem.getAttribute("data-longitude");
            if (fno && latitude && longitude) {
                // 좋아요 버튼 클릭은 상세 페이지에서만 처리하므로 여기서는 무시
                loadDetail(fno, parseFloat(latitude), parseFloat(longitude))
                    .catch(error => console.error("loadDetail 에러:", error));
            }
        }
    });

    window.loadDetail = async function (fno, latitude, longitude) {
        try {
            // 로딩 상태 표시
            const detailDiv = document.querySelector("#detail");
            if (detailDiv.classList.contains("visible")) {
                // 이미 상세 정보가 표시되고 있는 경우 로딩 오버레이 추가
                const loadingOverlay = document.createElement("div");
                loadingOverlay.className = "loading-overlay";
                loadingOverlay.innerHTML = '<div class="loading-spinner"></div>';
                detailDiv.appendChild(loadingOverlay);
            } else {
                // 첫 번째 상세 정보 표시인 경우
                detailDiv.classList.remove("hidden");
                detailDiv.classList.add("visible", "loading");
            }

            const response = await fetch(`/food/detail/${fno}`);
            if (!response.ok) {
                console.error("상세 정보 로드 실패", response.status);
                return;
            }

            // 조회수 증가 API 호출 (여기에서만 조회수를 증가시킴)
            try {
                await fetch(`/food/views/${fno}`, { method: "POST" });
            } catch (viewError) {
                console.warn("조회수 증가 요청 실패", viewError);
                // 조회수 증가 실패는 치명적이지 않으므로 계속 진행
            }

            const html = await response.text();

            // 로딩 상태 제거
            detailDiv.classList.remove("loading");
            const loadingOverlay = detailDiv.querySelector(".loading-overlay");
            if (loadingOverlay) {
                loadingOverlay.remove();
            }

            // 콘텐츠 업데이트
            detailDiv.innerHTML = html;

            const innerWrap = detailDiv.querySelector(".detailwrap");
            if (innerWrap) {
                innerWrap.classList.add("visible");
                innerWrap.style.display = "block";
            }

            // 지도의 크기 조절
            adjustMapSize();
            bindDetailHeartClick();
            restoreDetailLikeStatus(fno);
            initComentObserver();
            initMapButtonObserver();

            // 지도 중심 이동 및 마커 표시
            if (window.map && typeof window.map.setCenter === "function") {
                const newCenter = new kakao.maps.LatLng(latitude, longitude);
                window.map.setCenter(newCenter);
                console.log(`지도 중심 이동: 위도 ${latitude}, 경도 ${longitude}`);

                // 마커 생성 또는 위치 업데이트
                if (window.detailMarker) {
                    // 기존 마커가 있다면 위치만 업데이트
                    window.detailMarker.setPosition(newCenter);
                } else {
                    // 마커 생성 후 지도에 표시
                    window.detailMarker = new kakao.maps.Marker({
                        position: newCenter
                    });
                    window.detailMarker.setMap(window.map);
                }
            } else {
                console.warn("지도 객체가 정의되지 않았습니다.");
            }

        } catch (error) {
            console.error("상세 정보 요청 중 오류 발생", error);
            const detailDiv = document.querySelector("#detail");
            detailDiv.classList.remove("loading");
            const loadingOverlay = detailDiv.querySelector(".loading-overlay");
            if (loadingOverlay) {
                loadingOverlay.remove();
            }
        }
    };

	window.toggleLike = async function (button) {
        if (!button) return;

        let fno = button.getAttribute("data-fno");
        if (!fno) {
            const parent = button.closest(".clickBtn");
            if (parent) {
                fno = parent.getAttribute("data-fno");
            }
        }

        if (!fno) {
            console.error("음식점 ID(fno)를 찾을 수 없습니다.");
            return;
        }

        try {
            const response = await fetch(`/food/like/${fno}`, {
                method: "POST"
            });
            if (!response.ok) {
                console.error("좋아요 요청 실패", response.status);
                return;
            }
            const newLikeStatus = await response.json();

            // 좋아요 상태를 로컬 스토리지에 저장
            localStorage.setItem(`food_like_${fno}`, newLikeStatus);

            // 상세 페이지 UI 업데이트
            updateDetailLikeUI(fno, newLikeStatus);

            // 리스트 페이지 UI도 함께 업데이트 (리스트가 보이는 경우)
            updateListLikeUI(fno, newLikeStatus);
        } catch (error) {
            console.error("좋아요 요청 중 오류 발생", error);
        }
	};

    // 상세 페이지 좋아요 UI 업데이트 함수
    function updateDetailLikeUI(fno, newLikeStatus) {
        const detailHeartImg = document.querySelector("#detail .detailTitle .heart-img");
        const detailLikesCount = document.querySelector("#detail .detailTitle .clickBtn b");

        if (!detailHeartImg || !detailLikesCount) return;

        let currentLikes = parseInt(detailLikesCount.dataset.count || detailLikesCount.textContent, 10);
        if (isNaN(currentLikes)) currentLikes = 0;

        if (newLikeStatus) {
            currentLikes++;
            detailHeartImg.src = "/img/health/heartOn.png";
            detailHeartImg.dataset.liked = 'true';
        } else {
            currentLikes = Math.max(0, currentLikes - 1); // 음수 방지
            detailHeartImg.src = "/img/health/heartOff.png";
            detailHeartImg.dataset.liked = 'false';
        }

        detailLikesCount.dataset.count = currentLikes;
        detailLikesCount.textContent = currentLikes;
    }

    // 리스트 페이지 좋아요 UI 업데이트 함수 (상세 페이지에서 좋아요 후 리스트 페이지 동기화용)
    function updateListLikeUI(fno, newLikeStatus) {
        const heartImageList = document.querySelector(`.foodList [data-fno="${fno}"] .heart img`);
        const likesCountList = document.querySelector(`.foodList [data-fno="${fno}"] .likes-count`);

        if (!heartImageList || !likesCountList) return;

        let currentLikes = parseInt(likesCountList.dataset.count || likesCountList.textContent, 10);
        if (isNaN(currentLikes)) currentLikes = 0;

        if (newLikeStatus) {
            currentLikes++;
            heartImageList.src = "/img/health/heartOn.png";
            heartImageList.dataset.liked = 'true';
        } else {
            currentLikes = Math.max(0, currentLikes - 1); // 음수 방지
            heartImageList.src = "/img/health/heartOff.png";
            heartImageList.dataset.liked = 'false';
        }

        likesCountList.dataset.count = currentLikes;
        likesCountList.textContent = currentLikes;
        // likesCountList.style.display = "inline"; // 좋아요 수 표시
    }


    function bindDetailHeartClick() {
        const detailHeartImg = document.querySelector("#detail .detailTitle .heart-img");
        if (!detailHeartImg) {
            console.error("상세페이지의 좋아요 아이콘(.heart-img)을 찾을 수 없습니다.");
            return;
        }
        console.log("상세페이지의 좋아요 아이콘 이벤트 바인딩됨.");
        detailHeartImg.addEventListener("click", () => {
            const detailButton = detailHeartImg.closest(".clickBtn");
            toggleLike(detailButton);
        });
    }

    function restoreLikeStatus() {
        // 모든 음식점 항목 가져오기
        const foodItems = document.querySelectorAll('.foodList li[data-fno]');

        foodItems.forEach(item => {
            const fno = item.getAttribute('data-fno');
            const heartImg = item.querySelector('.heart img');
            const likesCount = item.querySelector('.likes-count');

            // 로컬 스토리지에서 좋아요 상태 확인
            const likedStatus = localStorage.getItem(`food_like_${fno}`);
            if (likedStatus === 'true' && heartImg) {
                heartImg.src = "/img/health/heartOn.png";
                heartImg.dataset.liked = 'true';
            }
        });
    }

    function restoreDetailLikeStatus(fno) {
        // 상세 페이지의 좋아요 상태 복원
        const detailHeartImg = document.querySelector("#detail .detailTitle .heart-img");
        if (!detailHeartImg) return;

        const likedStatus = localStorage.getItem(`food_like_${fno}`);
        if (likedStatus === 'true') {
            detailHeartImg.src = "/img/health/heartOn.png";
            detailHeartImg.dataset.liked = 'true';
        }
    }

	function initMapButtonObserver() {
	    const mapObserver = new MutationObserver((mutations, observer) => {
	        const mapButton = document.querySelector("#detail .detailTitle button");
	        if (mapButton) {
	            console.log("'지도로 보기' 버튼이 추가됨 -> 초기화 진행");
	            initializeMapButton();
	            observer.disconnect();
	        }
	    });
	    const detailContainer = document.querySelector("#detail");
	    if (detailContainer) {
	        mapObserver.observe(detailContainer, { childList: true, subtree: true });
	        console.log("MutationObserver(지도 버튼) 활성화됨.");
	    } else {
	        console.error("#detail 요소를 찾을 수 없습니다.");
	    }
	}

    function initializeMapButton() {
        const mapButton = document.querySelector("#detail .detailTitle button");
        if (!mapButton) {
            console.error("'지도로 보기' 버튼을 찾을 수 없습니다.");
            return;
        }
        mapButton.addEventListener("click", () => {
            // 지도 컨테이너에 로딩 오버레이 추가
            const mapContainer = document.getElementById("map-container");
            let loadingOverlay;
            if (mapContainer) {
                loadingOverlay = document.createElement("div");
                loadingOverlay.className = "map-loading-overlay"; // CSS에서 스타일 지정
                loadingOverlay.innerHTML = '<div class="loading-spinner"></div>';
                mapContainer.appendChild(loadingOverlay);
            }

            const clickBtn = document.querySelector("#detail .clickBtn");
            if (!clickBtn) {
                console.error("data-fno가 있는 클릭 버튼을 찾을 수 없습니다.");
                if (loadingOverlay) loadingOverlay.remove();
                return;
            }
            const fno = clickBtn.getAttribute("data-fno");

            const detailImage = document.querySelector("#detail .detail");
            if (!detailImage) {
                console.error("상세 이미지 요소를 찾을 수 없습니다.");
                if (loadingOverlay) loadingOverlay.remove();
                return;
            }
            const foodLat = parseFloat(detailImage.getAttribute("data-latitude"));
            const foodLng = parseFloat(detailImage.getAttribute("data-longitude"));

            console.log("음식점 fno, 위도, 경도:", fno, foodLat, foodLng);
            if (!navigator.geolocation) {
                alert("현재 위치를 가져올 수 없습니다. 브라우저에서 위치 권한을 허용해주세요.");
                if (loadingOverlay) loadingOverlay.remove();
                return;
            }
            navigator.geolocation.getCurrentPosition((position) => {
                const userLat = position.coords.latitude;
                const userLng = position.coords.longitude;
                if (loadingOverlay) loadingOverlay.remove();
                const url = `/bus/map?fno=${fno}&startLat=${userLat}&startLng=${userLng}&foodLat=${foodLat}&foodLng=${foodLng}`;
                window.location.href = url;
            }, (error) => {
                console.error("현재 위치 가져오기 실패:", error);
                alert("현재 위치를 가져올 수 없습니다.");
                if (loadingOverlay) loadingOverlay.remove();
            });
        });
    }


    function initComentObserver() {
        const commentObserver = new MutationObserver((mutationsList, observer) => {
            const commentForm = document.getElementById("comentForm");
            if (commentForm) {
                console.log("comentForm이 동적으로 추가됨!");
                observer.disconnect();
                initComentForm();
            }
        });
        const detailElement = document.querySelector("#detail");
        if (detailElement) {
            commentObserver.observe(detailElement, { childList: true, subtree: true });
            console.log("MutationObserver(댓글 폼) 활성화됨.");
        } else {
            console.error("#detail 요소를 찾을 수 없습니다.");
        }
    }

    function initComentForm() {
        const comentForm = document.getElementById("comentForm");
        if (!comentForm) return;

        const stars = comentForm.querySelectorAll(".stars .star");
        stars.forEach((star) => {
            star.addEventListener("click", () => {
                const rating = star.getAttribute("data-rating");
                document.getElementById("rating").value = rating;
                stars.forEach((s, idx) => {
                    s.classList.toggle("selected", idx < rating);
                });
            });
        });

        comentForm.addEventListener("submit", async (event) => {
            event.preventDefault();

            const detailEl = document.querySelector(".detail");
            if (!detailEl) {
                console.error(".detail 요소가 없습니다.");
                return;
            }
            const fno = detailEl.getAttribute("data-fno");
            if (!fno) {
                console.error("data-fno 값이 없습니다.");
                return;
            }

            const content = document.getElementById("comentContent").value.trim();
            const author = document.getElementById("comentAuthor").value.trim();
            const rating = document.getElementById("rating").value;
            if (!content || !author) {
                alert("내용과 작성자 이름을 입력하세요.");
                return;
            }
            if (rating === "0") {
                alert("평점을 선택하세요.");
                return;
            }

            try {
                const response = await fetch(`/food/coment/${fno}`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ content, author, rating })
                });
                if (!response.ok) {
                    console.error("댓글 등록 실패", response.status);
                    return;
                }
                const { savedComent, averageRating } = await response.json();
                console.log("댓글 등록 성공:", savedComent, averageRating);

                const comentList = document.querySelector(".comentList ul");
                if (comentList) {
                    // 날짜 포멧팅
                    const formattedDate = new Date(savedComent.createdAt).toLocaleDateString('ko-KR', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit'
                    }).replace(/\. /g, '-').replace(/\.$/g, '');
                    const newComentHTML = `
                        <li>
                            <p>${savedComent.content}</p>
                            <div class="comentInfo">
                                <b>${savedComent.author}</b>
                                <small>${formattedDate}</small>
                                <span>${"★".repeat(savedComent.rating)}</span>
                            </div>
                        </li>`;
                    comentList.innerHTML = newComentHTML + comentList.innerHTML;
                }
                const comentHeader = document.querySelector(".comentHeader h3");
                if (comentHeader) {
                    const match = comentHeader.textContent.match(/\d+/);
                    if (match) {
                        const currentCount = parseInt(match[0], 10);
                        comentHeader.textContent = `댓글 ${currentCount + 1}개`;
                    }
                }

                // 평균평점 업데이트

                comentForm.reset();
                document.getElementById("rating").value = "0";
                stars.forEach(s => s.classList.remove("selected"));

            } catch (err) {
                console.error("댓글 등록 중 오류", err);
            }
        });
    }

    function adjustMapSize() {
        const detailWrap = document.querySelector("#detail");
        const mapContainer = document.getElementById("map-container");
        if (!mapContainer) return;

        if (detailWrap && detailWrap.classList.contains("visible")) {
            mapContainer.style.flex = "0 0 calc(100% - 900px)";
        } else {
            mapContainer.style.flex = "0 0 calc(100% - 450px)";
        }

        if (window.map && typeof window.map.relayout === "function") {
            window.map.relayout();
            console.log("지도 크기 재조정 완료!");
        }
    }
});
