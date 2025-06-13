(function () {
    const cartIcon = document.getElementById('cartIcon');
    const personIcon = document.getElementById('personIcon');
    const modal = document.getElementById('login-modal');
    const form = document.getElementById('login-form');
    const overlay = document.getElementById('overlay');
    const sideNav = document.getElementById('sideNav');
    const burgerBtn = document.getElementById('menuBtn');

    let isLoggedIn = false;

    function showLogin() {
        modal.classList.add('active');
    }

    function hideLogin() {
        modal.classList.remove('active');
    }

    function closeSideNav(afterClose) {
        if (!sideNav || !sideNav.classList.contains('show')) {
            afterClose?.();
            return;
        }

        const onEnd = (e) => {
            if (e.propertyName !== 'transform') return;
            sideNav.removeEventListener('transitionend', onEnd);
            afterClose?.();
        };

        sideNav.addEventListener('transitionend', onEnd);
        sideNav.classList.remove('show');
        overlay?.classList.remove('show');

        if (burgerBtn) {
            burgerBtn.classList.remove('open');
            burgerBtn.setAttribute('aria-expanded', 'false');
        }
        document.body.style.overflow = '';
    }

    function protectedAction() {
        closeSideNav(() => {
            if (!isLoggedIn) showLogin();
        });
    }

    cartIcon?.addEventListener('click', protectedAction);
    personIcon?.addEventListener('click', protectedAction);

    document.addEventListener('click', (e) => {
        const link = e.target.closest('[data-action]');
        if (!link) return;
        const act = link.dataset.action;
        if (act === 'profile' || act === 'cart') {
            e.preventDefault();
            protectedAction();
        }
    });

    modal.addEventListener('click', (e) => {
        if (e.target === modal) hideLogin();
    });

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const data = Object.fromEntries(new FormData(form));
        alert(`Спасибо, ${data.email}!`);
        isLoggedIn = true;
        hideLogin();
    });

    window.addEventListener('keydown', (e) => {
        if (e.key !== 'Escape') return;
        modal.classList.contains('active') ? hideLogin() : closeSideNav();
    });
})();
