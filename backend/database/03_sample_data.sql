-- ============================================================================
-- 03_sample_data.sql - Демонстрационные данные для GreenMind
-- ============================================================================
-- Этот скрипт содержит тестовые данные для разработки и демонстрации
-- функциональности приложения. НЕ используйте эти данные в production!
--
-- Использование:
-- psql -U <your_username> -d greenmind -f 03_sample_data.sql
-- ============================================================================

BEGIN TRANSACTION;

-- ============================================================================
-- Тестовые пользователи
-- ============================================================================
-- Все пароли: Test123! (BCrypt хэшированный)
INSERT INTO users (name, email, password_hash, birth_date, role, created_at, updated_at)
VALUES
    ('Иванов Иван', 'ivan@example.com', '$2a$10$2kLfN3.YJcCr8zE6GZfnOuPPqtL6TqXwFkP9qYzV3nYK1XqZJKX1S', '1990-05-15', 'USER', CURRENT_TIMESTAMP - INTERVAL '6 months', CURRENT_TIMESTAMP),
    ('Петрова Мария', 'maria@example.com', '$2a$10$2kLfN3.YJcCr8zE6GZfnOuPPqtL6TqXwFkP9qYzV3nYK1XqZJKX1S', '1985-08-20', 'USER', CURRENT_TIMESTAMP - INTERVAL '4 months', CURRENT_TIMESTAMP),
    ('Сидоров Алексей', 'alexey@example.com', '$2a$10$2kLfN3.YJcCr8zE6GZfnOuPPqtL6TqXwFkP9qYzV3nYK1XqZJKX1S', '1995-03-10', 'USER', CURRENT_TIMESTAMP - INTERVAL '3 months', CURRENT_TIMESTAMP),
    ('Козлова Елена', 'elena@example.com', '$2a$10$2kLfN3.YJcCr8zE6GZfnOuPPqtL6TqXwFkP9qYzV3nYK1XqZJKX1S', '1992-11-25', 'USER', CURRENT_TIMESTAMP - INTERVAL '2 months', CURRENT_TIMESTAMP),
    ('Новиков Дмитрий', 'dmitry@example.com', '$2a$10$2kLfN3.YJcCr8zE6GZfnOuPPqtL6TqXwFkP9qYzV3nYK1XqZJKX1S', '1988-07-08', 'USER', CURRENT_TIMESTAMP - INTERVAL '1 month', CURRENT_TIMESTAMP),
    ('Морозова Анна', 'anna@example.com', '$2a$10$2kLfN3.YJcCr8zE6GZfnOuPPqtL6TqXwFkP9qYzV3nYK1XqZJKX1S', '1993-09-30', 'USER', CURRENT_TIMESTAMP - INTERVAL '2 weeks', CURRENT_TIMESTAMP),
    ('Волков Сергей', 'sergey@example.com', '$2a$10$2kLfN3.YJcCr8zE6GZfnOuPPqtL6TqXwFkP9qYzV3nYK1XqZJKX1S', '1991-12-18', 'USER', CURRENT_TIMESTAMP - INTERVAL '1 week', CURRENT_TIMESTAMP),
    ('Зайцева Ольга', 'olga@example.com', '$2a$10$2kLfN3.YJcCr8zE6GZfnOuPPqtL6TqXwFkP9qYzV3nYK1XqZJKX1S', '1994-04-22', 'USER', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- ============================================================================
-- Каталог продуктов (растений)
-- ============================================================================

-- Комнатные растения - популярные
INSERT INTO products (name, price, quantity, image_path, created_at)
VALUES
    ('Фикус Бенджамина', 1200.00, 15, '/frontend/img/index/main/Best-Selling/Frame_7.svg', CURRENT_TIMESTAMP - INTERVAL '3 months'),
    ('Монстера деликатесная', 2500.00, 8, '/frontend/img/index/main/Best-Selling/Frame_8.svg', CURRENT_TIMESTAMP - INTERVAL '3 months'),
    ('Сансевиерия трёхполосная', 800.00, 20, '/frontend/img/index/main/Best-Selling/Frame_9.svg', CURRENT_TIMESTAMP - INTERVAL '3 months'),
    ('Драцена окаймлённая', 1500.00, 12, '/frontend/img/products/dracaena.jpg', CURRENT_TIMESTAMP - INTERVAL '2 months'),
    ('Замиокулькас', 1800.00, 10, '/frontend/img/products/zamioculcas.jpg', CURRENT_TIMESTAMP - INTERVAL '2 months'),

    -- Суккуленты
    ('Алоэ вера', 600.00, 25, '/frontend/img/index/main/Categories/Frame_36.svg', CURRENT_TIMESTAMP - INTERVAL '2 months'),
    ('Эхеверия элегантная', 450.00, 30, '/frontend/img/index/main/Categories/Frame_37.svg', CURRENT_TIMESTAMP - INTERVAL '2 months'),
    ('Крассула овальная (Денежное дерево)', 750.00, 18, '/frontend/img/index/main/Categories/Frame_38.svg', CURRENT_TIMESTAMP - INTERVAL '1 month'),
    ('Хавортия полосатая', 500.00, 22, '/frontend/img/products/haworthia.jpg', CURRENT_TIMESTAMP - INTERVAL '1 month'),
    ('Седум Моргана', 550.00, 20, '/frontend/img/products/sedum.jpg', CURRENT_TIMESTAMP - INTERVAL '1 month'),

    -- Цветущие растения
    ('Орхидея Фаленопсис', 2200.00, 10, '/frontend/img/products/orchid.jpg', CURRENT_TIMESTAMP - INTERVAL '3 weeks'),
    ('Антуриум Андре', 1900.00, 7, '/frontend/img/products/anthurium.jpg', CURRENT_TIMESTAMP - INTERVAL '3 weeks'),
    ('Спатифиллум', 900.00, 15, '/frontend/img/products/spathiphyllum.jpg', CURRENT_TIMESTAMP - INTERVAL '2 weeks'),
    ('Калатея Макоя', 1600.00, 9, '/frontend/img/products/calathea.jpg', CURRENT_TIMESTAMP - INTERVAL '2 weeks'),
    ('Бегония королевская', 1100.00, 13, '/frontend/img/products/begonia.jpg', CURRENT_TIMESTAMP - INTERVAL '1 week'),

    -- Пальмы
    ('Хамедорея изящная', 2800.00, 6, '/frontend/img/products/chamaedorea.jpg', CURRENT_TIMESTAMP - INTERVAL '1 week'),
    ('Ховея Форстера', 3500.00, 4, '/frontend/img/products/howea.jpg', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    ('Финиковая пальма', 3200.00, 5, '/frontend/img/products/phoenix.jpg', CURRENT_TIMESTAMP - INTERVAL '3 days'),

    -- Редкие и экзотические
    ('Филодендрон Розовая принцесса', 4500.00, 3, '/frontend/img/products/philodendron-pink.jpg', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    ('Алоказия Полли', 2100.00, 7, '/frontend/img/products/alocasia.jpg', CURRENT_TIMESTAMP - INTERVAL '1 day'),

    -- Аксессуары и уход
    ('Набор для пересадки растений', 850.00, 40, '/frontend/img/products/repotting-kit.jpg', CURRENT_TIMESTAMP),
    ('Автополив для растений (3 шт)', 1200.00, 25, '/frontend/img/products/auto-watering.jpg', CURRENT_TIMESTAMP),
    ('Кашпо керамическое "Минимализм" 20см', 1500.00, 15, '/frontend/img/products/ceramic-planter.jpg', CURRENT_TIMESTAMP),
    ('Грунт универсальный (10л)', 450.00, 50, '/frontend/img/products/soil.jpg', CURRENT_TIMESTAMP),
    ('Опрыскиватель медный 0.5л', 1800.00, 10, '/frontend/img/products/sprayer.jpg', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- ============================================================================
-- Примеры товаров в корзинах пользователей
-- ============================================================================

-- Получаем ID пользователей и продуктов для создания связей
DO $$
DECLARE
    ivan_id INTEGER;
    maria_id INTEGER;
    alexey_id INTEGER;
    elena_id INTEGER;

    ficus_id INTEGER;
    monstera_id INTEGER;
    aloe_id INTEGER;
    orchid_id INTEGER;
    soil_id INTEGER;
BEGIN
    -- Получаем ID пользователей
    SELECT id INTO ivan_id FROM users WHERE email = 'ivan@example.com';
    SELECT id INTO maria_id FROM users WHERE email = 'maria@example.com';
    SELECT id INTO alexey_id FROM users WHERE email = 'alexey@example.com';
    SELECT id INTO elena_id FROM users WHERE email = 'elena@example.com';

    -- Получаем ID продуктов
    SELECT id INTO ficus_id FROM products WHERE name = 'Фикус Бенджамина';
    SELECT id INTO monstera_id FROM products WHERE name = 'Монстера деликатесная';
    SELECT id INTO aloe_id FROM products WHERE name = 'Алоэ вера';
    SELECT id INTO orchid_id FROM products WHERE name = 'Орхидея Фаленопсис';
    SELECT id INTO soil_id FROM products WHERE name = 'Грунт универсальный (10л)';

    -- Добавляем товары в корзины только если пользователи и продукты существуют
    IF ivan_id IS NOT NULL AND ficus_id IS NOT NULL THEN
        INSERT INTO user_products (user_id, product_id, quantity)
        VALUES (ivan_id, ficus_id, 1)
        ON CONFLICT DO NOTHING;
    END IF;

    IF ivan_id IS NOT NULL AND soil_id IS NOT NULL THEN
        INSERT INTO user_products (user_id, product_id, quantity)
        VALUES (ivan_id, soil_id, 2)
        ON CONFLICT DO NOTHING;
    END IF;

    IF maria_id IS NOT NULL AND monstera_id IS NOT NULL THEN
        INSERT INTO user_products (user_id, product_id, quantity)
        VALUES (maria_id, monstera_id, 1)
        ON CONFLICT DO NOTHING;
    END IF;

    IF alexey_id IS NOT NULL AND aloe_id IS NOT NULL THEN
        INSERT INTO user_products (user_id, product_id, quantity)
        VALUES (alexey_id, aloe_id, 3)
        ON CONFLICT DO NOTHING;
    END IF;

    IF elena_id IS NOT NULL AND orchid_id IS NOT NULL THEN
        INSERT INTO user_products (user_id, product_id, quantity)
        VALUES (elena_id, orchid_id, 1)
        ON CONFLICT DO NOTHING;
    END IF;
END $$;

-- ============================================================================
-- Статистика загруженных данных
-- ============================================================================
DO $$
DECLARE
    user_count INTEGER;
    product_count INTEGER;
    cart_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO user_count FROM users WHERE role = 'USER';
    SELECT COUNT(*) INTO product_count FROM products;
    SELECT COUNT(*) INTO cart_count FROM user_products;

    RAISE NOTICE '================================================';
    RAISE NOTICE 'Демонстрационные данные успешно загружены:';
    RAISE NOTICE '- Пользователей создано: %', user_count;
    RAISE NOTICE '- Продуктов добавлено: %', product_count;
    RAISE NOTICE '- Товаров в корзинах: %', cart_count;
    RAISE NOTICE '================================================';
    RAISE NOTICE '';
    RAISE NOTICE 'Тестовые учётные записи:';
    RAISE NOTICE '- Email: ivan@example.com';
    RAISE NOTICE '- Email: maria@example.com';
    RAISE NOTICE '- Пароль для всех: Test123!';
    RAISE NOTICE '================================================';
    RAISE NOTICE '';
    RAISE NOTICE 'ВНИМАНИЕ: Эти данные только для разработки!';
    RAISE NOTICE 'НЕ используйте в production окружении!';
    RAISE NOTICE '================================================';
END $$;

COMMIT;

-- ============================================================================
-- Обновление статистики для оптимизации запросов
-- ============================================================================
ANALYZE users;
ANALYZE products;
ANALYZE user_products;