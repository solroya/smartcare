// sidebar.js
function initSidebar() {
    const menuToggle = document.querySelector('.mobile-menu-toggle');
    const sidebar = document.querySelector('.sidebar');

    let backdrop = document.querySelector('.sidebar-backdrop');
    if (!backdrop) {
        backdrop = document.createElement('div');
        backdrop.className = 'sidebar-backdrop';
        document.body.appendChild(backdrop);
    }

    menuToggle?.addEventListener('click', function() {
        sidebar.classList.toggle('active');
        backdrop.classList.toggle('active');
    });

    backdrop?.addEventListener('click', function() {
        sidebar.classList.remove('active');
        backdrop.classList.remove('active');
    });
}

// DOM이 로드되면 초기화
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initSidebar);
} else {
    initSidebar();
}