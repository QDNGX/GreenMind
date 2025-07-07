(function () {
    const cartIcon = document.getElementById('cartIcon');
    const personIcon = document.getElementById('personIcon');
    const modal = document.getElementById('login-modal');
    const form = document.getElementById('login-form');
    const overlay = document.getElementById('overlay');
    const sideNav = document.getElementById('sideNav');
    const burgerBtn = document.getElementById('menuBtn');
    const errBox = document.getElementById('login-error');

    let isLoggedIn = false;

    document.addEventListener('DOMContentLoaded', async () => {
        try {
            const res = await fetch('/api/session', {
                method: 'HEAD',
                credentials: 'same-origin'
            });
            if (res.status === 204 && !res.redirected) {
                isLoggedIn = true;
            }
        } catch {

        }
    });

    function showLogin() {
        modal.classList.add('active');
    }

    function hideLogin() {
        modal.classList.remove('active');
    }

    function showError(msg) {
        errBox.textContent = msg;
        errBox.hidden = false;
    }

    function clearError() {
        errBox.hidden = true;
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

    function goToCart() {
        window.location.assign('/frontend/profile.html')
    }

    function goToProfile() {
        window.location.assign('/frontend/profile.html')
    }

    function protectedAction(target = 'profile') {
        closeSideNav(() => {
            if (!isLoggedIn) {
                showLogin();
                return;
            }
            target === 'cart' ? goToCart() : goToProfile();
        });
    }

    cartIcon?.addEventListener('click', () => protectedAction('cart'));
    personIcon?.addEventListener('click', () => protectedAction('profile'));

    document.addEventListener('click', (e) => {
        const link = e.target.closest('[data-action]');
        if (!link) return;

        e.preventDefault();
        if (link.dataset.action === 'cart') protectedAction('cart');
        if (link.dataset.action === 'profile') protectedAction('profile');
    });

    modal.addEventListener('click', (e) => {
        if (e.target === modal) hideLogin();
    });
    window.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            modal.classList.contains('active') ? hideLogin() : closeSideNav();
        }
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        clearError();

        try {
            const res = await fetch('/login', {
                method: 'POST',
                body: new FormData(form),
                credentials: 'same-origin'
            });
            if (res.redirected) {
                hideLogin();
                isLoggedIn = true;
                window.location.assign(res.url);
                return;
            }
            if (res.ok && res.headers.get('content-type')?.includes('application/json')) {
                const {redirect = '/frontend/profile.html'} = await res.json();
                hideLogin();
                isLoggedIn = true;
                window.location.assign(redirect);
                return;
            }

            showError(await res.text() || 'Неверный e-mail или пароль');
        } catch (err) {
            console.error(err);
            showError('Сервер временно недоступен. Попробуйте позже.');
        }
    });

})();
