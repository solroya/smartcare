document.addEventListener("DOMContentLoaded", () => {
	console.log("list.js DOMContentLoaded 실행됨!");

	const foodListContainer = document.querySelector(".foodList");
	if (!foodListContainer) return;

	foodListContainer.addEventListener("click", (event) => {
	    const likeBtn = event.target.closest(".like-btn");
	    if (likeBtn) {
	      event.preventDefault();
	      event.stopPropagation();
	      toggleLike(likeBtn);
	      return;
	    }
		const listItem = event.target.closest("li");
		if (listItem) {
		    const fno = listItem.getAttribute("data-fno");
		    const latitude = listItem.getAttribute("data-latitude");
		    const longitude = listItem.getAttribute("data-longitude");
		    if (fno && latitude && longitude) {
		        loadDetail(fno, parseFloat(latitude), parseFloat(longitude));
		    }
		}
    });
});

