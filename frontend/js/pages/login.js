document.getElementById('signing-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;

    // Сбор данных формы
    const data = {
        email: form.email.value.trim(),
        password: form.password.value,
    };

    try {
        // Отправляем как form data для совместимости с Spring Security
        const formData = new FormData();
        formData.append('email', data.email);
        formData.append('password', data.password);
        formData.append('redirect', '/');

        const response = await fetch('/login', {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            credentials: 'same-origin',
            body: formData
        });

        if (response.ok) {
            Toast.success('Вход выполнен успешно! Переходим на главную страницу...');

            setTimeout(() => {
                window.location.href = '../index.html';
            }, 1500);
        } else {
            await ApiHelper.handleError(response);
        }

    } catch (error) {
        Toast.error('Ошибка соединения с сервером');
    }
});