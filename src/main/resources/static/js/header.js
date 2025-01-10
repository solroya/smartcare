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