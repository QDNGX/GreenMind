(() => {
    const btn   = document.getElementById('menuBtn');
    const nav   = document.getElementById('sideNav');
    const ovl   = document.getElementById('overlay');
    const focusable = 'a, button';
    let previouslyFocused;

    btn.addEventListener('click', toggleMenu);
    ovl.addEventListener('click', closeMenu);
    window.addEventListener('keydown', e => e.key === 'Escape' && closeMenu());

    nav.addEventListener('click', e => {
        if (e.target.tagName === 'A') closeMenu();
    });

    function toggleMenu(){
        nav.classList.toggle('show');
        ovl.classList.toggle('show');
        btn.classList.toggle('open');

        const open = nav.classList.contains('show');
        btn.setAttribute('aria-expanded', open);
        nav.setAttribute('aria-hidden', !open);

        if (open){
            previouslyFocused = document.activeElement;
            nav.querySelector(focusable).focus();
            document.body.style.overflow = 'hidden';
            trapFocus(nav);
        }else{
            untrapFocus(nav);
            previouslyFocused?.focus();
            document.body.style.overflow = '';
        }
    }
    function closeMenu(){ if (nav.classList.contains('show')) toggleMenu(); }

    function trapFocus(container){
        const foci = [...container.querySelectorAll(focusable)];
        const first = foci[0], last = foci[foci.length - 1];
        container.addEventListener('keydown', handler);
        function handler(e){
            if (e.key !== 'Tab') return;
            if (e.shiftKey && e.target === first){ e.preventDefault(); last.focus(); }
            if (!e.shiftKey && e.target === last){ e.preventDefault(); first.focus(); }
        }
        container._trap = handler;
    }
    function untrapFocus(container){
        container.removeEventListener('keydown', container._trap);
    }
})();
