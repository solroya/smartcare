/**
 * 
 */

document.addEventListener('DOMContentLoaded', function () {
    const menuItems = document.querySelectorAll('.menu-item');

    menuItems.forEach(item => {
        item.addEventListener('mouseenter', () => {
            item.classList.add('show');
        });

        item.addEventListener('mouseleave', () => {
            item.classList.remove('show');
        });
    });
});