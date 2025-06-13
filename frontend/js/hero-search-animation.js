document.addEventListener('DOMContentLoaded', () => {
    const navItems = document.querySelectorAll('.What-sayAbout__nav-item');
    const slider = document.querySelector('.What-sayAbout__cards');
    const card = slider.querySelector('.What-sayAbout__card');

    const getStep = () => {
        const gap = parseInt(getComputedStyle(slider).gap) || 0;
        return card.getBoundingClientRect().width + gap;
    };
    navItems.forEach((dot, idx) => {
        dot.addEventListener('click', () => {
            if (dot.classList.contains('active')) return;

            navItems.forEach(item => item.classList.remove('active'));
            dot.classList.add('active');

            const offset = idx * getStep();
            slider.style.transform = `translateX(-${offset}px)`;
        });
    });
    window.addEventListener('resize', () => {
        const activeIndex = [...navItems].findIndex(i => i.classList.contains('active'));
        slider.style.transform = `translateX(-${activeIndex * getStep()}px)`;
    });

});