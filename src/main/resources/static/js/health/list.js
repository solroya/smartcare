document.addEventListener("DOMContentLoaded", () => {
    window.loadDetail = async function (fno) {
        try {
            const response = await fetch(`/food/detail/${fno}`);
            if (response.ok) {
                const html = await response.text();
                const detailDiv = document.querySelector(".detailwrap");
                detailDiv.innerHTML = html;
                detailDiv.classList.add("visible");

                bindDetailHeartClick(fno);
            } else {
                console.error("상세 정보 로드 실패", response.status);
            }
        } catch (error) {
            console.error("상세 정보 요청 중 오류 발생", error);
        }
    };

    // 좋아요 토글
	window.toggleLike = async function (button) {
	    const fno = button.getAttribute("data-fno");
	    const isDetail = button.closest(".detail") !== null;

	    try {
	        const response = await fetch(`/food/like/${fno}`, { method: "POST" });
	        if (response.ok) {
	            const isLiked = await response.json();
	            const heartImage = button.querySelector(".heart img");
	            const likesCount = button.querySelector(".likes-count");

	            if (!likesCount) {
	                console.error("likesCount 요소를 찾을 수 없습니다.");
	                return;
	            }

	            const currentLikes = parseInt(likesCount.textContent, 10);

	            likesCount.textContent = isLiked
	                ? currentLikes + 1
	                : currentLikes - 1;

	            heartImage.src = isLiked
	                ? "/img/health/heartOff.png"
	                : "/img/health/heartOn.png"

	            // 상세페이지와 리스트 동기화
	            if (!isDetail) {
	                const detailHeartImg = document.querySelector(".detail .heart-img");
	                const detailLikesCount = document.querySelector(".detail .clickBtn b");
	                if (detailHeartImg && detailLikesCount) {
	                    detailLikesCount.textContent = likesCount.textContent;
	                    detailHeartImg.src = heartImage.src;
	                }
	            } else {
	                const listHeartImg = document.querySelector(
	                    `.foodList [data-fno="${fno}"] .heart img`
	                );
	                const listLikesCount = document.querySelector(
	                    `.foodList [data-fno="${fno}"] .likes-count`
	                );
	                if (listHeartImg && listLikesCount) {
	                    listLikesCount.textContent = likesCount.textContent;
	                    listHeartImg.src = heartImage.src;
	                }
	            }
	        } else {
	            console.error("좋아요 요청 실패", response.status);
	        }
	    } catch (error) {
	        console.error("좋아요 요청 중 오류 발생", error);
	    }
	};

    // 리스트 클릭 이벤트
    document.querySelector(".foodList").addEventListener("click", (event) => {
        if (event.target.closest(".like-btn")) {
            const button = event.target.closest(".like-btn");
            toggleLike(button);
            return; // 하트 클릭 시 상세페이지 열지 않음
        }

        const listItem = event.target.closest("li");
        const fno = listItem?.getAttribute("data-fno");
        if (fno) {
            loadDetail(fno);
        }
    });

    // 상세 하트 클릭 이벤트 바인딩
    function bindDetailHeartClick(fno) {
        const detailHeartImg = document.querySelector(".detail .heart-img");
        if (detailHeartImg) {
            detailHeartImg.addEventListener("click", () => {
                const detailButton = detailHeartImg.closest(".clickBtn");
                toggleLike(detailButton, fno, true);
            });
        }
    }
});
