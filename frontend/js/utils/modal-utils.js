// Общие утилиты модальных окон для единообразного поведения модальных окон во всем приложении
class ModalUtils {
    // Показать модальное окно с правильными ARIA атрибутами
    static showModal(modal, focusElement = null) {
        if (!modal) return;

        modal.classList.remove('hidden');
        modal.setAttribute('aria-hidden', 'false');

        if (focusElement) {
            focusElement.focus();
        }
    }

    // Скрыть модальное окно с правильными ARIA атрибутами
    static hideModal(modal) {
        if (!modal) return;

        modal.classList.add('hidden');
        modal.setAttribute('aria-hidden', 'true');
    }

    // Настройка закрытия модального окна при клике на фон
    static setupBackgroundClose(modal, hideCallback) {
        if (!modal) return;

        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                hideCallback();
            }
        });
    }

    // Настройка закрытия модального окна при нажатии Escape
    static setupEscapeClose(hideCallback) {
        const handleEscape = (e) => {
            if (e.key === 'Escape') {
                hideCallback();
            }
        };

        document.addEventListener('keydown', handleEscape);

        // Возвращаем функцию очистки
        return () => document.removeEventListener('keydown', handleEscape);
    }

    // Создать переиспользуемый контроллер модального окна
    static createModalController(modalId, options = {}) {
        const modal = document.getElementById(modalId);
        if (!modal) {
            console.warn(`Modal with ID '${modalId}' not found`);
            return { show: () => {}, hide: () => {} };
        }

        const {
            focusElement = null,
            onShow = null,
            onHide = null,
            closeOnEscape = true,
            closeOnBackground = true
        } = options;

        let escapeCleanup = null;

        const show = () => {
            this.showModal(modal, focusElement);

            if (closeOnEscape) {
                escapeCleanup = this.setupEscapeClose(hide);
            }

            if (onShow) {
                onShow();
            }
        };

        const hide = () => {
            this.hideModal(modal);

            if (escapeCleanup) {
                escapeCleanup();
                escapeCleanup = null;
            }

            if (onHide) {
                onHide();
            }
        };

        if (closeOnBackground) {
            this.setupBackgroundClose(modal, hide);
        }

        return { show, hide, modal };
    }
}

// Общие утилиты валидации форм
class FormValidationUtils {
    // Валидация числового ввода и ограничение только числами
    static validateNumericInput(input, options = {}) {
        const { min = 0, max = Infinity, allowDecimals = false } = options;

        // Удаление нечисловых символов
        const pattern = allowDecimals ? /[^\d.]/g : /\D/g;
        input.value = input.value.replace(pattern, '');

        const value = allowDecimals ? parseFloat(input.value) : parseInt(input.value);

        if (isNaN(value)) return false;

        return value >= min && value <= max;
    }

    // Включить/выключить кнопку на основе состояния валидации
    static toggleButton(button, isValid) {
        if (!button) return;

        button.disabled = !isValid;
    }

    // Валидация обязательных полей в форме
    static validateRequiredFields(container) {
        const requiredFields = container.querySelectorAll('[required]');

        return Array.from(requiredFields).every(field => {
            const value = field.value?.trim() || '';
            return value.length > 0;
        });
    }
}

// Делаем утилиты глобально доступными
window.ModalUtils = ModalUtils;
window.FormValidationUtils = FormValidationUtils;