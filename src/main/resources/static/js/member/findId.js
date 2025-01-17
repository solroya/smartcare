document.addEventListener("DOMContentLoaded", () => {
	const phoneInput = document.getElementById("memberPhoneNumber");
	const smsCodeInput = document.getElementById("smsCode");
	const sendSmsBtn = document.getElementById("sendSmsBtn");
	const verifySmsBtn = document.getElementById("verifySmsBtn");
	const phoneValidationMessage = document.getElementById("phoneValidationMessage");
	const smsValidationMessage = document.getElementById("smsValidationMessage");
	const foundMemberIdsBox = document.getElementById("foundMemberIds");
	const findIdModal = document.getElementById("findIdModal");
	const closeFindIdModal = document.getElementById("closeFindIdModal");
	const remainingTimeDisplay = document.getElementById("remainingTime"); // 남은 시간 표시
	
	let isPhoneValid = false;
	let interval;
	
	phoneInput.addEventListener("input", () => {
	    const phone = phoneInput.value.replace(/\D/g, ""); // 숫자만 남기기
	    if (phone.length <= 3) {
	        phoneInput.value = phone;
	    } else if (phone.length <= 7) {
	        phoneInput.value = `${phone.slice(0, 3)}-${phone.slice(3)}`;
	    } else {
	        phoneInput.value = `${phone.slice(0, 3)}-${phone.slice(3, 7)}-${phone.slice(7, 11)}`;
	    }

	    const phoneRegex = /^010-\d{3,4}-\d{4}$/;
	    if (phoneRegex.test(phoneInput.value)) {
	        phoneValidationMessage.textContent = "유효한 휴대전화 번호입니다.";
	        phoneValidationMessage.style.color = "green";
	        isPhoneValid = true;
	    } else {
	        phoneValidationMessage.textContent = "휴대전화 번호는 010-1234-5678 형식이어야 합니다.";
	        phoneValidationMessage.style.color = "red";
	        isPhoneValid = false;
	    }
	});
	
	// 인증 코드 발송
	sendSmsBtn.addEventListener("click", () => {
		if (!isPhoneValid) {
			phoneValidationMessage.textContent = "올바른 휴대전화 번호를 입력해주세요.";
			phoneValidationMessage.style.color = "red";
			return;
		}
		
		const redisPhoneNumber = phoneInput.value.replace(/-/g, "");
		fetch("/sms/send", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify({ phone: redisPhoneNumber }),
		})
			.then((response) => response.json())
			.then(() => {
				phoneValidationMessage.textContent = "인증 코드가 전송되었습니다.";
				phoneValidationMessage.style.color = "green";
				startTimer(redisPhoneNumber); // 타이머 시작
			})
			.catch((error) => {
				console.error("인증 코드 발송 오류:", error);
				phoneValidationMessage.textContent = "인증 코드 발송 중 오류가 발생했습니다.";
				phoneValidationMessage.style.color = "red";
			});
	});
	
	// 타이머 시작
	function startTimer(redisPhoneNumber) {
		clearInterval(interval); // 기존 타이머 초기화
		interval = setInterval(() => {
			fetch("/sms/getTime", {
				method : "POST",
				headers : {
					"Content-Type" : "application/json",
				},
				body: JSON.stringify({ phone: redisPhoneNumber }),
			})
				.then((response) => {
					if (!response.ok) {
						throw new Error(`HTTP error! status: ${response.status}`);
					}
					
					return response.json();
				})
				.then((ttl) => {
					if (ttl <= 0) {
						clearInterval(interval);
						remainingTimeDisplay.textContent = "시간이 만료되었습니다.";
						remainingTimeDisplay.style.color = "red";
					} else {
						const minutes = Math.floor(ttl / 60);
						const seconds = ttl % 60;
						remainingTimeDisplay.textContent = `${minutes}:${seconds}`;
						remainingTimeDisplay.style.color = "green";
					}
				})
				.catch((error) => {
					console.error("시간 조회 오류 :", error);
					clearInterval(interval);
					remainingTimeDisplay.textContent = "시간 조회 중 오류가 발생했습니다.";
					remainingTimeDisplay.style.color = "red";
				});
		}, 1000);
	}
	
	// 인증 코드 확인 및 아이디 찾기
	verifySmsBtn.addEventListener("click", () => {
	    const formattedPhoneNumber = phoneInput.value;
	    const smsCode = smsCodeInput.value.trim();

	    if (!smsCode) {
	        smsValidationMessage.textContent = "인증코드를 입력해주세요.";
	        smsValidationMessage.style.color = "red";
	        return;
	    }

	    // 인증 코드 검증 및 아이디 찾기 통합 요청
	    fetch("/member/find-id", {
	        method: "POST",
	        headers: { "Content-Type": "application/json" },
	        body: JSON.stringify({ phone: formattedPhoneNumber, code: smsCode }),
	    })
	        .then((response) => {
	            if (!response.ok) {
	                return response.json().then(err => { throw err; });
	            }
	            return response.json();
	        })
	        .then((data) => {
	            if (data.memberIds) {
	                foundMemberIdsBox.textContent = `${data.memberIds.join(", ")}`;
	                foundMemberIdsBox.style.color = "#333";
					clearInterval(interval);
					remainingTimeDisplay.style.display = 'none';
	                openFindIdModal();
	            } else {
	                foundMemberIdsBox.textContent = "해당 번호로 등록된 계정이 없습니다.";
	                foundMemberIdsBox.style.color = "red";
					clearInterval(interval);
	                remainingTimeDisplay.style.display = 'none';
	            }
	        })
	        .catch((error) => {
	            console.error("아이디 찾기 오류:", error);
	            smsValidationMessage.textContent = "인증 코드가 유효하지 않습니다.";
	            smsValidationMessage.style.color = "red";
				clearInterval(interval);
	            remainingTimeDisplay.style.display = 'none';
	        });
	});
	
	// 모달창 열기
	function openFindIdModal() {
		findIdModal.style.display = "flex";
	}
	
	// 모달창 닫기
	closeFindIdModal.addEventListener("click", () => {
		findIdModal.style.display = "none";
	});
	
	// 모달 바깥 클릭 시 닫기
	window.addEventListener("click", (event) => {
		if (event.target === findIdModal) {
			findIdModal.style.display = "none";
		}
	});
	
});