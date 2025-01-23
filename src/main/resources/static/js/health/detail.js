document.addEventListener("DOMContentLoaded", () => {
    const detailHeartClickHandler = async () => {
        const detailElement = document.querySelector(".detail");
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