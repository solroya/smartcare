document.addEventListener("DOMContentLoaded", () => {
	const memberIdInput = document.getElementById("memberId");
    const phoneInput = document.getElementById("memberPhoneNumber");
    const smsCodeInput = document.getElementById("smsCode");
    const sendSmsBtn = document.getElementById("sendSmsBtn");
    const verifySmsBtn = document.getElementById("verifySmsBtn");
    const updatePasswordBtn = document.getElementById("updatePasswordBtn");
    const idValidationMessage = document.getElementById("idValidationMessage");
    const phoneValidationMessage = document.getElementById("phoneValidationMessage");
    const smsValidationMessage = document.getElementById("smsValidationMessage");
    const passwordValidationMessage = document.getElementById("passwordValidationMessage");
    const passwordConfirmValidationMessage = document.getElementById("passwordConfirmValidationMessage");
    const remainingTimeDisplay = document.getElementById("remainingTime");
	const findPassModal = document.getElementById("findPassModal");
	const closeFindPassModal = document.getElementById("closeFindPassModal");

    let isPhoneValid = false;
    let isSmsVerified = false;
    let interval;
	
	memberIdInput.addEventListener("input", () => {
		const memberId = memberIdInput.value.trim();
		if (memberId.length >= 6 && memberId.length <= 12) {
			idValidationMessage.textContent = "유효한 아이디입니다.";
			idValidationMessage.style.color = "green";
		} else {
			idValidationMessage.textContent = "아이디는 6~12자의 영문/숫자이어야 합니다."
			idValidationMessage.style.color = "red";
		}
	});
	
	phoneInput.addEventListener("input", () => {
		const phone = phoneInput.value.replace(/\D/g, "");
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
	
	sendSmsBtn.addEventListener("click", () => {
		const memberId = memberIdInput.value.trim();
		const rawPhoneNumber = phoneInput.value.trim();
		const redisPhoneNumber = rawPhoneNumber.replace(/-/g, "");
		
		if (memberId.length < 6 || memberId.length > 12) {
			idValidationMessage.textContent = "유효하지 않은 아이디입니다."
			idValidationMessage.style.color = "red";
			return;
		}
		
		if (!isPhoneValid) {
			phoneValidationMessage.textContent = "유효하지 않은 전화번호입니다.";
			phoneValidationMessage.style.color = "red";
			return;
		}
		
		fetch("/member/find-password/send-code", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({ 
                memberId: memberId, 
                memberPhoneNumber: rawPhoneNumber
            }),
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.message) {
                    phoneValidationMessage.textContent = data.message;
                    phoneValidationMessage.style.color = "green";
                    startTimer(redisPhoneNumber);
                }
            })
            .catch(error => {
                console.error("인증 코드 발송 오류:", error);
                phoneValidationMessage.textContent = "인증 코드 발송 중 오류가 발생했습니다.";
                phoneValidationMessage.style.color = "red";
            });
	});
	
	function startTimer(redisPhoneNumber) {
        clearInterval(interval);
        interval = setInterval(() => {
            fetch("/sms/getTime", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ phone: redisPhoneNumber }),
            })
                .then((response) => response.json())
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
                    console.error("시간 조회 오류:", error);
                    clearInterval(interval);
                    remainingTimeDisplay.textContent = "시간 조회 중 오류가 발생했습니다.";
                    remainingTimeDisplay.style.color = "red";
                });
        }, 1000);
    }
	
	verifySmsBtn.addEventListener("click", () => {
		const rawPhoneNumber = phoneInput.value.trim();
	    const redisPhoneNumber = rawPhoneNumber.replace(/-/g, "");
	    const smsCode = smsCodeInput.value.trim();
		console.log("여기로 들어왔나? 그리고 들어온 전화번호는?", redisPhoneNumber);
	    if (!smsCode) {
	        smsValidationMessage.textContent = "인증코드를 입력해주세요.";
	        smsValidationMessage.style.color = "red";
	        return;
	    }
	
	    // 인증 코드 검증 요청
	    fetch("/member/find-password/verify-code", {
	        method: "POST",
	        headers: { "Content-Type": "application/json" },
	        body: JSON.stringify({ 
	            memberPhoneNumber: rawPhoneNumber, 
	            code: smsCode }),
	    })
	        .then((response) => response.json())
	        .then((data) => {
	            if (data.message === "인증 코드가 유효합니다.") {
	                smsValidationMessage.textContent = data.message;
	                smsValidationMessage.style.color = "green";
	                isSmsVerified = true;
					clearInterval(interval);
					remainingTimeDisplay.style.display = 'none';
					console.log("여기로 들어왔나?");
					openFindPassModal();
	            } else {
	                smsValidationMessage.textContent = data.message;
	                smsValidationMessage.style.color = "red";
					clearInterval(interval);
					remainingTimeDisplay.style.display = 'none';
	            }
	        })
	        .catch(error => {
	            console.error("인증 코드 검증 오류:", error);
	            smsValidationMessage.textContent = "인증 코드 검증 중 오류가 발생했습니다.";
	            smsValidationMessage.style.color = "red";
	        });
	});
	
	updatePasswordBtn.addEventListener("click", () => {
		const memberId = memberIdInput.value.trim();
		const newPassword = document.getElementById("memberPass").value.trim();
		const confirmNewPassword = document.getElementById("confirmMemberPass").value.trim();
		
		if (!isSmsVerified) {
			alret("인증이 완료되지 않았습니다.")
			return;
		}
		
		if (newPassword.length < 6) {
            passwordValidationMessage.textContent = "비밀번호는 최소 6자 이상이어야 합니다.";
            passwordValidationMessage.style.color = "red";
            return;
        }
        
        if (newPassword !== confirmNewPassword) {
            passwordConfirmValidationMessage.textContent = "비밀번호가 일치하지 않습니다.";
            passwordConfirmValidationMessage.style.color = "red";
            return;
        }
		
		fetch("/member/find-password/reset", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ 
                memberId: memberId, 
                newPassword: newPassword, 
                confirmNewPassword: confirmNewPassword 
            }),
        })
			.then((response) => response.json())
		    .then((data) => {
		        if (data.message === "비밀번호가 성공적으로 변경되었습니다.") {
		            alert(data.message);
		            window.location.href = "/member/login";
		        } else {
		            alert(data.message);
		        }
		    })
		    .catch(error => {
		        console.error("비밀번호 변경 오류:", error);
		        alert("비밀번호 변경 중 오류가 발생했습니다.");
		    });
		
	});
	
	function openFindPassModal() {
        findPassModal.style.display = "flex";
    }
    
    closeFindPassModal.addEventListener("click", () => {
        findPassModal.style.display = "none";
    });
    
    window.addEventListener("click", (event) => {
        if (event.target === findPassModal) {
            findPassModal.style.display = "none";
        }
    });
	
});