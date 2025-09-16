const ApiHelper = {
    async handleResponse(response, options = {}) {
        const { showSuccessToast = false, successMessage = 'Операция выполнена успешно' } = options;

        // Успешный ответ
        if (response.ok) {
            if (showSuccessToast) {
                Toast.success(successMessage);
            }

            // Для 204 No Content возвращаем null (нет тела ответа)
            if (response.status === 204) {
                return null;
            }

            // Для остальных успешных ответов парсим JSON
            const data = await response.json();
            return data;
        }

        // Обработка ошибок
        await this.handleError(response);
        throw new Error(`HTTP ${response.status}`);
    },
    async handleError(response) {
        try {
            const contentType = response.headers.get('content-type');
            
            // Проверяем, что ответ в JSON формате
            if (contentType && contentType.includes('application/json')) {
                const errorData = await response.json();
                
                // Все ошибки теперь в формате {"error": "message"}
                if (errorData.error && typeof errorData.error === 'string') {
                    Toast.error(errorData.error);
                    return;
                }
            }
            
            // Для text/plain ответов
            if (contentType && contentType.includes('text/plain')) {
                const textError = await response.text();
                if (textError) {
                    Toast.error(textError);
                    return;
                }
            }
            
            // Fallback для не-JSON ответов или неожиданных форматов
            const textError = await response.text();
            if (textError) {
                Toast.error(textError);
            } else {
                Toast.error(this.getDefaultErrorMessage(response.status));
            }
            
        } catch (parseError) {
            // Если не удалось распарсить ответ, показываем общую ошибку
            Toast.error(this.getDefaultErrorMessage(response.status));
        }
    },
    getDefaultErrorMessage(status) {
        const errorMessages = {
            400: 'Некорректные данные запроса',
            401: 'Необходима авторизация',
            403: 'Доступ запрещен',
            404: 'Ресурс не найден',
            409: 'Конфликт данных',
            422: 'Ошибка валидации данных',
            500: 'Внутренняя ошибка сервера',
            502: 'Сервер недоступен',
            503: 'Сервис временно недоступен'
        };
        
        return errorMessages[status] || `Ошибка сервера (${status})`;
    },

    async post(url, data, options = {}) {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest',
                ...options.headers
            },
            credentials: 'same-origin',
            body: JSON.stringify(data),
            ...options
        });
        
        return this.handleResponse(response, options);
    },
    async patch(url, data, options = {}) {
        const response = await fetch(url, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest',
                ...options.headers
            },
            credentials: 'same-origin',
            body: JSON.stringify(data),
            ...options
        });

        return this.handleResponse(response, options);
    },

    async put(url, data, options = {}) {
        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest',
                ...options.headers
            },
            credentials: 'same-origin',
            body: JSON.stringify(data),
            ...options
        });

        return this.handleResponse(response, options);
    },

    async get(url, options = {}) {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                ...options.headers
            },
            credentials: 'same-origin',
            ...options
        });
        
        return this.handleResponse(response, options);
    },
    async delete(url, options = {}) {
        const response = await fetch(url, {
            method: 'DELETE',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                ...options.headers
            },
            credentials: 'same-origin',
            ...options
        });
        
        return this.handleResponse(response, options);
    }
};