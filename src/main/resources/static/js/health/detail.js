document.addEventListener("DOMContentLoaded", () => {
    console.log("detail.js DOMContentLoaded 실행됨!");

    window.loadDetail = async function (fno) {
        try {
            const response = await fetch(`/food/detail/${fno}`);
            if (!response.ok) {
                console.error("상세 정보 로드 실패", response.status);
                return;
            }

            const html = await response.text();
            const detailDiv = document.querySelector("#detail");
            detailDiv.innerHTML = html;
            detailDiv.classList.remove("hidden");
            detailDiv.classList.add("visible");

            const innerWrap = detailDiv.querySelector(".detailwrap");
            if (innerWrap) {
                innerWrap.classList.add("visible");
                innerWrap.style.display = "block";
            }
			
            adjustMapSize();
            bindDetailHeartClick();
            initComentObserver();
            initMapButtonObserver();

        } catch (error) {
            console.error("상세 정보 요청 중 오류 발생", error);
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

	    const isDetail = document.querySelector("#detail .clickBtn") !== null;

	    try {
	        const response = await fetch(`/food/like/${fno}`, {
	            method: "POST"
	        });
	        if (!response.ok) {
	            console.error("좋아요 요청 실패", response.status);
	            return;
	        }
	        const newLikeStatus = await response.json();
	        updateLikeUI(button, fno, newLikeStatus, isDetail);
	    } catch (error) {
	        console.error("좋아요 요청 중 오류 발생", error);
	    }
	};

	function updateLikeUI(button, fno, newLikeStatus, isDetail) {
	    const heartImageList = document.querySelector(`.foodList [data-fno="${fno}"] .heart img`);
	    const likesCountList = document.querySelector(`.foodList [data-fno="${fno}"] .likes-count`);

		const detailHeartImg = document.querySelector("#detail .detailTitle .heart-img");
	    const detailLikesCount = document.querySelector("#detail .detailTitle .clickBtn b");

	    let currentLikes = 0;
	    if (likesCountList) {
	        currentLikes = parseInt(likesCountList.dataset.count || likesCountList.textContent, 10);
	    } else if (detailLikesCount) {
	        currentLikes = parseInt(detailLikesCount.dataset.count || detailLikesCount.textContent, 10);
	    }
	    if (isNaN(currentLikes)) currentLikes = 0;

		if (newLikeStatus) {
	    	currentLikes++;
	    	if (heartImageList) heartImageList.src = "/img/health/heartOn.png";
	    	if (detailHeartImg) detailHeartImg.src = "/img/health/heartOn.png";
	    } else {
	    	currentLikes--;
	    	if (heartImageList) heartImageList.src = "/img/health/heartOff.png";
	    	if (detailHeartImg) detailHeartImg.src = "/img/health/heartOff.png";
	    }

		if (likesCountList) {
	        likesCountList.dataset.count = currentLikes;
	        likesCountList.textContent = currentLikes;
	    }
	    if (detailLikesCount) {
	        detailLikesCount.dataset.count = currentLikes;
	        detailLikesCount.textContent = currentLikes;
	    }
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
	        const clickBtn = document.querySelector("#detail .clickBtn");
	        if (!clickBtn) {
	            console.error("data-fno가 있는 클릭 버튼을 찾을 수 없습니다.");
	            return;
	        }
	        const fno = clickBtn.getAttribute("data-fno");

	        const detailImage = document.querySelector("#detail .detail");
	        if (!detailImage) {
	            console.error("상세 이미지 요소를 찾을 수 없습니다.");
	            return;
	        }
	        const foodLat = parseFloat(detailImage.getAttribute("data-latitude"));
	        const foodLng = parseFloat(detailImage.getAttribute("data-longitude"));

	        console.log("음식점 fno, 위도, 경도:", fno, foodLat, foodLng);
	        if (!navigator.geolocation) {
	            alert("현재 위치를 가져올 수 없습니다. 브라우저에서 위치 권한을 허용해주세요.");
	            return;
	        }
	        navigator.geolocation.getCurrentPosition((position) => {
	            const userLat = position.coords.latitude;
	            const userLng = position.coords.longitude;
	            const url = `/bus/map?fno=${fno}&startLat=${userLat}&startLng=${userLng}&foodLat=${foodLat}&foodLng=${foodLng}`;
	            window.location.href = url;
	        }, (error) => {
	            console.error("현재 위치 가져오기 실패:", error);
	            alert("현재 위치를 가져올 수 없습니다.");
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
                }
                const comentHeader = document.querySelector(".comentHeader h3");
                if (comentHeader) {
                    const match = comentHeader.textContent.match(/\d+/);
                    if (match) {
                        const currentCount = parseInt(match[0], 10);
                        comentHeader.textContent = `댓글 ${currentCount + 1}개`;
                    }
                }
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
