// 유효성 상태 변수
let isIdValid = false;
let isPasswordValid = false;
let isPasswordConfirmed = false;
let isNameValid = false;
let isEmailValid = false;
let isGenderSelected = false;
let isBirthdateValid = false;
let isPhoneValid = false;
let isSmsVerified = false;

// 유효성 메시지 표시 함수
function showValidationMessage(elementId, message, isValid) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.style.color = isValid ? 'green' : 'red';
}

// 아이디 중복 확인
document.getElementById('checkIdBtn').addEventListener('click', function () {
    const memberId = document.getElementById('memberId').value.trim();

    if (!memberId || memberId.length < 6 || memberId.length > 12) {
        showValidationMessage('idValidationMessage', '아이디는 6~12자의 영문/숫자로 입력해주세요.', false);
        isIdValid = false;
        updateSubmitButtonState();
        return;
    }

    // 중복 확인 (예시로 fetch 사용)
    fetch(`/member/search-memberId?memberId=${memberId}`)
        .then(response => response.json())
        .then(data => {
            if (data.result) {
                showValidationMessage('idValidationMessage', '이미 사용 중인 아이디입니다.', false);
                isIdValid = false;
            } else {
                showValidationMessage('idValidationMessage', '사용 가능한 아이디입니다.', true);
                isIdValid = true;
            }
            updateSubmitButtonState();
        })
        .catch(() => {
            showValidationMessage('idValidationMessage', '아이디 확인 중 오류가 발생했습니다.', false);
            isIdValid = false;
            updateSubmitButtonState();
        });
});

// 비밀번호 유효성 검사
document.getElementById('memberPass').addEventListener('input', function () {
    const password = this.value.trim();
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/; // 영어+숫자, 최소 8자

    if (!passwordRegex.test(password)) {
        showValidationMessage('passwordValidationMessage', '비밀번호는 최소 8자 이상, 영어와 숫자를 포함해야 합니다.', false);
        isPasswordValid = false;
    } else {
        showValidationMessage('passwordValidationMessage', '유효한 비밀번호입니다.', true);
        isPasswordValid = true;
    }

    // 비밀번호가 변경되면 비밀번호 확인 상태를 초기화
    isPasswordConfirmed = false;
    document.getElementById('confirmMemberPass').value = ''; // 비밀번호 확인 필드 초기화
    showValidationMessage('passwordConfirmValidationMessage', '', false);

    updateSubmitButtonState();
});

// 비밀번호 확인
document.getElementById('confirmMemberPass').addEventListener('input', function () {
    const password = document.getElementById('memberPass').value.trim();
    const confirmPassword = this.value.trim();

    if (confirmPassword === '') {
        showValidationMessage('passwordConfirmValidationMessage', '', false);
        isPasswordConfirmed = false;
    } else if (password !== confirmPassword) {
        showValidationMessage('passwordConfirmValidationMessage', '비밀번호가 일치하지 않습니다.', false);
        isPasswordConfirmed = false;
    } else {
        showValidationMessage('passwordConfirmValidationMessage', '비밀번호가 일치합니다.', true);
        isPasswordConfirmed = true;
    }
    updateSubmitButtonState();
});

// 이름 유효성 검사
document.getElementById('memberName').addEventListener('input', function () {
    const name = this.value.trim();
    if (name.length < 2) {
        showValidationMessage('nameValidationMessage', '이름은 최소 2자 이상이어야 합니다.', false);
        isNameValid = false;
    } else {
        showValidationMessage('nameValidationMessage', '유효한 이름입니다.', true);
        isNameValid = true;
    }
    updateSubmitButtonState();
});

// 이메일 중복 확인
document.getElementById('checkEmailBtn').addEventListener('click', function () {
    const email = document.getElementById('memberEmail').value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(email)) {
        showValidationMessage('emailValidationMessage', '유효한 이메일 주소를 입력해주세요.', false);
        isEmailValid = false;
        updateSubmitButtonState();
        return;
    }
	
	// 카카오 이메일 도메인 검사
	if (email.toLowerCase().endsWith('@kakao.com')) {
	        showValidationMessage('emailValidationMessage', '카카오는 소셜로그인을 이용해주세요.', false);
	        openKakaoModal();
	        return;
	    }
	
    // 이메일 중복 확인
    fetch(`/member/search-memberEmail?memberEmail=${email}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답이 올바르지 않습니다.');
            }
            return response.json(); // JSON 응답 처리
        })
        .then(data => {
            if (data.result) {
                showValidationMessage('emailValidationMessage', '이미 사용 중인 이메일입니다.', false);
                isEmailValid = false;
            } else {
                showValidationMessage('emailValidationMessage', '사용 가능한 이메일입니다.', true);
                isEmailValid = true;
            }
            updateSubmitButtonState();
        })
        .catch(error => {
            console.error('에러 발생:', error);
            showValidationMessage('emailValidationMessage', '이메일 확인 중 오류가 발생했습니다.', false);
            isEmailValid = false;
            updateSubmitButtonState();
        });
		
});

// 모달 열기
function openKakaoModal() {
	document.getElementById('kakaoLoginModal').style.display = 'flex';
	
	const birthdayInput = document.getElementById("memberBirthday");
	birthdayInput.disabled = true;
}

// 모달 닫기
document.getElementById('closeKakaoModal').addEventListener('click', function () {
	document.getElementById('kakaoLoginModal').style.display = 'none';
	
	const birthdayInput = document.getElementById("memberBirthday");
	birthdayInput.disabled = false;
})

// 카카오 로그인 버튼 클릭 시
document.getElementById('kakaoLoginBtn').addEventListener('click', function () {
	
    const kakaoAuthUrl = `/oauth2/authorization/kakao`;
    window.location.href = kakaoAuthUrl;
});

// 메세지 표시 함수
function showValidationMessage(elementId, message, isValid) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = message;
        element.style.color = isValid ? 'green' : 'red';
    }
}

function updateSubmitButtonState() {
    const submitButton = document.getElementById('submitButton'); // 제출 버튼 ID 확인
    if (submitButton) {
    }
}

// 성별 확인
const genderRadios = document.querySelectorAll('input[name="gender"]');
genderRadios.forEach(radio => {
    radio.addEventListener('change', function () {
        isGenderSelected = [...genderRadios].some(radio => radio.checked);
        updateSubmitButtonState();
    });
});

// 생년월일 확인
document.addEventListener("DOMContentLoaded", () => {
    const birthdayInput = document.getElementById("memberBirthday"); // 생년월일 입력 필드
    const messageElement = document.getElementById("birthdayValidationMessage"); // 검증 메시지 출력 요소

    // 생년월일 검증 이벤트 추가
    birthdayInput.addEventListener("change", function () {
        const selectedDate = birthdayInput.value; // 입력된 생년월일 값
        validateBirthday(selectedDate); // 생년월일 검증 함수 호출
    });

    // 생년월일 검증 함수
    function validateBirthday(selectedDate) {
        const inputDate = new Date(selectedDate);
        const today = new Date();
        const minDate = new Date(today.getFullYear() - 120, today.getMonth(), today.getDate()); // 120년 전
        const maxDate = new Date(today.getFullYear() - 14, today.getMonth(), today.getDate()); // 14년 전

        // 유효성 검증
        if (inputDate > today) {
            messageElement.textContent = "생년월일은 미래 날짜일 수 없습니다.";
            messageElement.style.color = "red";
        } else if (inputDate < minDate) {
            messageElement.textContent = "생년월일이 너무 오래되었습니다. (최대 120년 전)";
            messageElement.style.color = "red";
        } else if (inputDate > maxDate) {
            messageElement.textContent = "서비스 이용을 위해 만 14세 이상이어야 합니다.";
            messageElement.style.color = "red";
        } else {
            messageElement.textContent = "유효한 생년월일입니다.";
            messageElement.style.color = "green";
        }
    }
});

// 휴대폰 인증 및 인증코드
document.addEventListener("DOMContentLoaded", () => {
    const phoneInput = document.getElementById("memberPhoneNumber"); // 휴대전화 입력 필드
    const phoneValidationMessage = document.getElementById("phoneValidationMessage"); // 휴대전화 검증 메시지
    const smsCodeInput = document.getElementById("smsCode"); // 인증 코드 입력 필드
    const smsValidationMessage = document.getElementById("smsValidationMessage"); // 인증 코드 검증 메시지
    const sendSmsBtn = document.getElementById("sendSmsBtn"); // 인증 코드 발송 버튼
    const verifySmsBtn = document.getElementById("verifySmsBtn"); // 인증 코드 확인 버튼
	const remainingTimeDisplay = document.getElementById("remainingTime"); // 남은 시간 표시

    let isPhoneValid = false; // 휴대전화 유효성 상태
    let isSmsVerified = false; // SMS 인증 상태
	let interval;

    // 휴대전화 번호 입력 시 자동 포맷팅 및 검증
    phoneInput.addEventListener("input", function () {
        let phone = this.value.replace(/\D/g, ""); // 숫자만 남기기
        if (phone.length <= 3) {
            this.value = phone; // 3자리 이하
        } else if (phone.length <= 7) {
            this.value = `${phone.slice(0, 3)}-${phone.slice(3)}`; // 4~7자리
        } else {
            this.value = `${phone.slice(0, 3)}-${phone.slice(3, 7)}-${phone.slice(7, 11)}`; // 8자리 이상
        }

        // 전화번호 유효성 검사
        const phoneRegex = /^010-\d{3,4}-\d{4}$/;
        if (phoneRegex.test(this.value)) {
            phoneValidationMessage.textContent = "유효한 휴대전화 번호입니다.";
            phoneValidationMessage.style.color = "green";
            isPhoneValid = true;
        } else {
            phoneValidationMessage.textContent = "휴대전화 번호는 010-1234-5678 형식이어야 합니다.";
            phoneValidationMessage.style.color = "red";
            isPhoneValid = false;
        }

    });

    // 인증 코드 발송 버튼 클릭 이벤트
	sendSmsBtn.addEventListener("click", function () {
	    if (!isPhoneValid) {
	        phoneValidationMessage.textContent = "올바른 휴대전화 번호를 입력해주세요.";
	        phoneValidationMessage.style.color = "red";
	        return;
	    }

	    const phoneNumber = phoneInput.value.replace(/-/g, ""); // "-" 제거한 휴대전화 번호

	    // 인증 코드 발송 요청
	    fetch("/sms/send", {
	        method: "POST",
	        headers: {
	            "Content-Type": "application/json",
	        },
	        body: JSON.stringify({ phone: phoneNumber }),
	    })
	    	.then((response) => response.json())
			.then((data) => {
				phoneValidationMessage.textContent = "인증 코드가 전송되었습니다.";
				phoneValidationMessage.style.color = "green";
				startTimer(phoneNumber); // 타이머 시작
			})    
	        .catch((error) => {
	            console.error("인증 코드 발송 오류:", error);
	            phoneValidationMessage.textContent = "인증 코드 발송 중 오류가 발생했습니다.";
	            phoneValidationMessage.style.color = "red";
	        });
	});
	
	// 타이머 시작
	function startTimer(phone) {
		clearInterval(interval); // 기존 타이머 초기화
		interval = setInterval(() => {
			fetch("/sms/getTime", {
				method : "POST",
				headers : {
					"Content-Type" : "application/json",
				},
				body: JSON.stringify({ phone: phone }),
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
	
    // SMS 인증 코드 확인 버튼 클릭 이벤트
	verifySmsBtn.addEventListener("click", function () {
	    const smsCode = smsCodeInput.value.trim();
	    const phoneNumber = phoneInput.value.replace(/-/g, ""); // "-" 제거한 휴대전화 번호

	    if (!smsCode) {
	        smsValidationMessage.textContent = "인증코드를 입력해주세요.";
	        smsValidationMessage.style.color = "red";
	        isSmsVerified = false;
	        return;
	    }

	    // 인증 코드 확인 요청
	    fetch("/sms/verify", {
	        method: "POST",
	        headers: {
	            "Content-Type": "application/json",
	        },
	        body: JSON.stringify({ code: smsCode, phone: phoneNumber }),
	    })
	        .then((response) => response.text()) // JSON 대신 단순 텍스트로 응답 처리
	        .then((text) => {
	            console.log("응답 본문:", text); // 디버깅용 콘솔 출력
	            if (text.includes("인증 성공")) {
					clearInterval(interval);
	                smsValidationMessage.textContent = "인증에 성공했습니다.";
	                smsValidationMessage.style.color = "green";
	                isSmsVerified = true;
					$('#remainingTime').css('display', 'none');
	            } else {
					clearInterval(interval);
	                smsValidationMessage.textContent = "인증에 실패하셨습니다."; // 서버 응답 그대로 표시
	                smsValidationMessage.style.color = "red";
	                isSmsVerified = false;
	            }
	        })
	        .catch((error) => {
				clearInterval(interval);
	            console.error("인증 코드 확인 오류:", error);
	            smsValidationMessage.textContent = "인증 확인 중 오류가 발생했습니다.";
	            smsValidationMessage.style.color = "red";
	            isSmsVerified = false;
	        });
	});
});