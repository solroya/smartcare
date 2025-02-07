document.addEventListener('DOMContentLoaded', function () {
    // 헤더 메뉴 관련 기능
    const header = document.querySelector('.header');
    const menuItems = document.querySelectorAll('.menu-item');
    const subMenus = document.querySelectorAll('.subMenu');
    let hideTimeout;

    function isInsideMenuOrSubMenu(target) {
        return Array.from(menuItems).some(item => item.contains(target)) ||
            Array.from(subMenus).some(subMenu => subMenu.contains(target));
    }

    // 메뉴 호버 이벤트 처리
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
            }, 200);
        });
    });

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

    // 햄버거 메뉴 관련 기능
    const gnbBtn = document.querySelector('.gnb-btn');
    const expandedContent = document.querySelector('.expanded-content');

    // backdrop 요소 동적 생성
    const backdrop = document.createElement('div');
    backdrop.className = 'menu-backdrop';
    document.body.appendChild(backdrop);

    function toggleMenu(show) {
        gnbBtn.classList.toggle('active', show);
        expandedContent.classList.toggle('active', show);
        backdrop.classList.toggle('active', show);
        document.body.style.overflow = show ? 'hidden' : '';
    }

    // 햄버거 버튼 클릭 이벤트
    gnbBtn.addEventListener('click', function() {
        toggleMenu(!this.classList.contains('active'));
    });

    // backdrop 클릭 시 메뉴 닫기
    backdrop.addEventListener('click', () => toggleMenu(false));

    // 메뉴 항목 클릭 시 메뉴 닫기
    const menuLinks = document.querySelectorAll('.menu-list a');
    menuLinks.forEach(link => {
        link.addEventListener('click', () => toggleMenu(false));
    });

    // 메뉴 아이템 호버 효과
    const sideMenuItems = document.querySelectorAll('.menu-list li a');
    sideMenuItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(10px)';
        });

        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateX(0)';
        });
    });

    // 로그인 상태 확인
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