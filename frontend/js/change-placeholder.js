(function () {
    const input = document.getElementById("Hero-Search");
    const desktopText = 'What are you looking for?';
    const mobileText = 'Search plants';

    function updatePlaceholder(e) {
        if (e.matches) {
            input.placeholder = mobileText;
        } else {
            input.placeholder = desktopText;
        }
    }

    const mq = window.matchMedia('(max-width: 600px)');
    updatePlaceholder(mq);
    mq.addEventListener
        ? mq.addEventListener('change', updatePlaceholder)
        : mq.removeAttribute(updatePlaceholder);
})();