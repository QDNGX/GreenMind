-- ============================================================================
-- 02_initial_data.sql - Начальные данные для GreenMind
-- ============================================================================
-- Этот скрипт содержит обязательные начальные данные, необходимые для
-- корректной работы приложения после развертывания.
--
-- ВАЖНО: Пароли в этом файле предназначены только для начальной настройки!
-- После первого входа обязательно смените пароль администратора!
--
-- Использование:
-- psql -U <your_username> -d greenmind -f 02_initial_data.sql
-- ============================================================================

-- ============================================================================
-- Администратор системы по умолчанию
-- ============================================================================
-- Email: admin@greenmind.ru
-- Пароль: Admin123! (BCrypt хэшированный)
-- ВАЖНО: Измените пароль после первого входа!

INSERT INTO users (name, email, password_hash, role, created_at, updated_at)
VALUES (
    'Администратор',
    'admin@greenmind.ru',
    '$2a$10$YKcY7LMdJCE6GZHXy8L9Uu5JZKkXs3sVvDPxF4z5V9mFwD2kKxXHe', -- BCrypt хэш для "Admin123!"
    'ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- ============================================================================
-- Технический пользователь для поддержки
-- ============================================================================
-- Email: support@greenmind.ru
-- Пароль: Support2024! (BCrypt хэшированный)
INSERT INTO users (name, email, password_hash, role, created_at, updated_at)
VALUES (
    'Техническая поддержка',
    'support@greenmind.ru',
    '$2a$10$8nCzFJ0W5hKdQVqzW5FxZeJHvCKxR5TmVqR2XY3ZQwKHFx3mNxPKG', -- BCrypt хэш для "Support2024!"
    'ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- ============================================================================
-- Начальные категории продуктов (минимальный набор для демонстрации)
-- ============================================================================
-- Добавляем несколько базовых продуктов, чтобы магазин не был пустым
INSERT INTO products (name, price, quantity, image_path, created_at)
VALUES
    ('Приветственный набор для новичков', 1500.00, 10, '/frontend/img/products/welcome-kit.jpg', CURRENT_TIMESTAMP),
    ('Удобрение универсальное (1л)', 350.00, 50, '/frontend/img/products/fertilizer.jpg', CURRENT_TIMESTAMP),
    ('Горшок керамический (15см)', 750.00, 25, '/frontend/img/products/ceramic-pot.jpg', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- ============================================================================
-- Проверка корректности вставки данных
-- ============================================================================
DO $$
DECLARE
    admin_count INTEGER;
    product_count INTEGER;
BEGIN
    -- Проверяем наличие администраторов
    SELECT COUNT(*) INTO admin_count
    FROM users
    WHERE role = 'ADMIN';

    -- Проверяем наличие продуктов
    SELECT COUNT(*) INTO product_count
    FROM products;

    -- Выводим статистику
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Начальные данные успешно загружены:';
    RAISE NOTICE '- Администраторов создано: %', admin_count;
    RAISE NOTICE '- Продуктов добавлено: %', product_count;
    RAISE NOTICE '============================================';
    RAISE NOTICE '';
    RAISE NOTICE 'ВАЖНЫЕ ИНСТРУКЦИИ:';
    RAISE NOTICE '1. Измените пароль администратора после первого входа!';
    RAISE NOTICE '2. Для входа используйте:';
    RAISE NOTICE '   Email: admin@greenmind.ru';
    RAISE NOTICE '   Пароль: Admin123!';
    RAISE NOTICE '============================================';

    -- Проверка минимальных требований
    IF admin_count = 0 THEN
        RAISE WARNING 'Внимание: Не создано ни одного администратора!';
    END IF;
END $$;

-- ============================================================================
-- Дополнительная настройка безопасности
-- ============================================================================
-- Напоминание о необходимости создания резервной копии
COMMENT ON TABLE users IS 'ВНИМАНИЕ: Регулярно делайте резервные копии этой таблицы!';

-- Логирование загрузки начальных данных
DO $$
BEGIN
    -- Если в вашей БД есть таблица аудита, можно добавить запись о загрузке
    -- INSERT INTO audit_log (action, timestamp) VALUES ('Initial data loaded', CURRENT_TIMESTAMP);
    RAISE NOTICE 'Скрипт начальных данных выполнен: %', CURRENT_TIMESTAMP;
END $$;