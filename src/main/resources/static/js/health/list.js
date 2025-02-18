/*
document.addEventListener("DOMContentLoaded", () => {
	console.log("list.js DOMContentLoaded 실행됨!");

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
				loadDetail(fno, parseFloat(latitude), parseFloat(longitude));
			}
		}
    });
});

*/
