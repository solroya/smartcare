document.addEventListener('DOMContentLoaded', function () {
    // 메인 메뉴 동작을 위한 요소들 선택
    const header = document.querySelector('.header');
    const menuItems = document.querySelectorAll('.menu-item');
    const subMenus = document.querySelectorAll('.subMenu');
    let hideTimeout;

    // 마우스가 메뉴 또는 서브메뉴 영역 내부에 있는지 확인하는 헬퍼 함수
    function isInsideMenuOrSubMenu(target) {
        return Array.from(menuItems).some(item => item.contains(target)) ||
            Array.from(subMenus).some(subMenu => subMenu.contains(target));
    }

    // 메인 메뉴 호버 이벤트 처리
    menuItems.forEach(item => {
        item.addEventListener('mouseenter', () => {
            clearTimeout(hideTimeout);
            header.classList.add('expanded');
        });

        item.addEventListener('mouseleave', (e) => {
            hideTimeout = setTimeout(() => {
                if (!isInsideMenuOrSubMenu(e.relatedTarget)) {
                    header.classList.remove('expanded');
                }
            }, 200); // 200ms 딜레이로 부드러운 전환 효과 제공
        });
    });

    // 서브메뉴 호버 이벤트 처리
    subMenus.forEach(subMenu => {
        subMenu.addEventListener('mouseenter', () => {
            clearTimeout(hideTimeout);
            header.classList.add('expanded');
        });

        subMenu.addEventListener('mouseleave', (e) => {
            hideTimeout = setTimeout(() => {
                if (!isInsideMenuOrSubMenu(e.relatedTarget)) {
                    header.classList.remove('expanded');
                }
            }, 200);
        });
    });

    // 햄버거 메뉴 관련 요소 선택
    const gnbBtn = document.querySelector('.gnb-btn');
    const expandedContent = document.querySelector('.expanded-content');

    // backdrop 요소 동적 생성 및 스타일 설정
    const backdrop = document.createElement('div');
    backdrop.className = 'menu-backdrop';
    backdrop.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        opacity: 0;
        visibility: hidden;
        transition: opacity 0.3s ease, visibility 0.3s ease;
        z-index: 999;
    `;
    document.body.appendChild(backdrop);

    // 메뉴 토글 함수 - 메뉴와 backdrop의 표시 상태를 함께 제어
    function toggleMenu(show) {
        gnbBtn.classList.toggle('active', show);
        expandedContent.classList.toggle('active', show);

        if (show) {
            backdrop.style.visibility = 'visible';
            backdrop.style.opacity = '1';
            document.body.style.overflow = 'hidden';
        } else {
            backdrop.style.opacity = '0';
            backdrop.style.visibility = 'hidden';
            document.body.style.overflow = '';
        }
    }

    // 햄버거 버튼 클릭 이벤트
    gnbBtn.addEventListener('click', function() {
        toggleMenu(!this.classList.contains('active'));
    });

    // backdrop 클릭 시 메뉴 닫기
    backdrop.addEventListener('click', () => toggleMenu(false));

    // 사이드 메뉴 항목 클릭 시 메뉴 닫기
    const menuLinks = document.querySelectorAll('.menu-list a');
    menuLinks.forEach(link => {
        link.addEventListener('click', () => toggleMenu(false));
    });

    // 사이드 메뉴 아이템 호버 효과
    const sideMenuItems = document.querySelectorAll('.menu-list li a');
    sideMenuItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(10px)';
        });

        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateX(0)';
        });
    });

    // ESC 키로 메뉴 닫기 기능 추가
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && expandedContent.classList.contains('active')) {
            toggleMenu(false);
        }
    });

    // 로그인 상태 확인 및 버튼 표시 설정
    fetch("/api/check-login-status")
        .then(response => {
            if (!response.ok) throw new Error("HTTP error " + response.status);
            return response.json();
        })
        .then(data => {
            const loginBtn = document.querySelector(".login-btn");
            const logoutBtn = document.querySelector(".logout-btn");
            const mypage = document.querySelector(".my-page");

            loginBtn.style.display = data.isLoggedIn ? "none" : "flex";
            logoutBtn.style.display = data.isLoggedIn ? "flex" : "none";
            mypage.style.display = data.isLoggedIn ? "flex" : "none";
        })
        .catch(error => console.error("로그인 상태 확인 실패:", error));
});

document.addEventListener('DOMContentLoaded', function() {
   const hamburger = document.querySelector('.hamburger');
   const expandedContent = document.querySelector('.expanded-content');

   hamburger.addEventListener('click', function() {
     // 햄버거 버튼에 active 클래스 토글 (X 모양으로 변환)
     hamburger.classList.toggle('active');
     // 확장 메뉴 열기/닫기 토글
     expandedContent.classList.toggle('active');
   });

   // 만약 확장 메뉴 내부에 별도의 닫기 버튼(.close-btn)이 있다면:
   const closeBtn = document.querySelector('.close-btn');
   if (closeBtn) {
     closeBtn.addEventListener('click', function() {
       hamburger.classList.remove('active');
       expandedContent.classList.remove('active');
     });
   }
 });