document.getElementById('signup-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;

    // Сбор данных формы
    const data = {
        username: form.username.value.trim(),
        email: form.email.value.trim(),
        password: form.password.value,
    };

    try {
        // Используем ApiHelper для отправки запроса
        await ApiHelper.post('/register', data, {
            showSuccessToast: true,
            successMessage: 'Регистрация успешна! Переходим на страницу входа...'
        });

        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1500);

    } catch (error) {
        // ApiHelper уже показал Toast с детальной ошибкой от сервера
    }
});