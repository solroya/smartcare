document.addEventListener('DOMContentLoaded', function () {
    const header = document.querySelector('.header');
    const menuItems = document.querySelectorAll('.menu-item');
    const subMenus = document.querySelectorAll('.subMenu');

    let hideTimeout;

    function isInsideMenuOrSubMenu(target) {
        return Array.from(menuItems).some(item => item.contains(target)) ||
               Array.from(subMenus).some(subMenu => subMenu.contains(target));
    }

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

    document.addEventListener('click', function (e) {
        if (!header.contains(e.target)) {
            clearTimeout(hideTimeout);
            header.classList.remove('expanded');
        }
    });
});

document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/check-login-status")
        .then((response) => {
            if (!response.ok) {
                throw new Error("HTTP error " + response.status);
            }
            return response.json();
        })
        .then((data) => {
            console.log("로그인 상태:", data);
            const isLoggedIn = data.isLoggedIn;
            const loginBtn = document.querySelector(".login-btn");
            const logoutBtn = document.querySelector(".logout-btn");
            const mypage = document.querySelector(".my-page");

            console.log("mypage 요소:", mypage);

            if (isLoggedIn) {
                loginBtn.style.display = "none";
                logoutBtn.style.display = "flex";
                mypage.style.display = "flex";
            } else {
                loginBtn.style.display = "flex";
                logoutBtn.style.display = "none";
                mypage.style.display = "none";
            }
        })
        .catch((error) => console.error("로그인 상태 확인 실패:", error));
});

document.addEventListener('DOMContentLoaded', function() {
    const gnbBtn = document.querySelector('.gnb-btn');
    const expandedContent = document.querySelector('.expanded-content');

    gnbBtn.addEventListener('click', function() {
        this.classList.toggle('active');
        expandedContent.classList.toggle('active');

        // 햄버거 메뉴가 열렸을 때 body 스크롤 방지
        if (expandedContent.classList.contains('active')) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = '';
        }
    });

    // 메뉴 항목 클릭시 햄버거 메뉴 닫기
    const menuLinks = document.querySelectorAll('.menu-list a');
    menuLinks.forEach(link => {
        link.addEventListener('click', () => {
            gnbBtn.classList.remove('active');
            expandedContent.classList.remove('active');
            document.body.style.overflow = '';
        });
    });

    // 화면 외부 클릭시 메뉴 닫기
    document.addEventListener('click', function(e) {
        if (!expandedContent.contains(e.target) &&
            !gnbBtn.contains(e.target) &&
            expandedContent.classList.contains('active')) {
            gnbBtn.classList.remove('active');
            expandedContent.classList.remove('active');
            document.body.style.overflow = '';
        }
    });
});