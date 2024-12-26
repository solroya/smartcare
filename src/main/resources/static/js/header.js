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
            }, 150);
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
            }, 150);
        });
    });

    document.addEventListener('click', function (e) {
        if (!header.contains(e.target)) {
            clearTimeout(hideTimeout);
            header.classList.remove('expanded');
        }
    });
});
