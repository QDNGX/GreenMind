document.addEventListener('DOMContentLoaded', () => {
    const imgBlock = document.querySelector('.hero-search__images');

    const minWidth = 768;
    const slideStart = 1100;
    const fadeStart = 1024;

    function updateImagePos() {
        const w = window.innerWidth;
        if (w >= slideStart) {
            imgBlock.style.transform = 'translateX(0)';
            imgBlock.style.opacity = '1';
            return;
        }
        if (w <= minWidth) {
            imgBlock.style.transform = 'translateX(100%)';
            imgBlock.style.opacity = '0';
            return;
        }
        const slideProgress = (slideStart - w) / (slideStart - minWidth);
        const shift = slideProgress * 100;
        let alpha = 1;
        if (w < fadeStart) {
            alpha = 0;
        }

        imgBlock.style.transform = `translateX(${shift}%)`;
        imgBlock.style.opacity = alpha;
    }

    updateImagePos();
    window.addEventListener('resize', updateImagePos);
});

