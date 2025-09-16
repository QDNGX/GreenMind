-- ============================================================================
-- 01_schema.sql - Структура базы данных GreenMind
-- ============================================================================
-- Этот скрипт создает все необходимые таблицы и структуры для приложения
-- интернет-магазина растений GreenMind.
--
-- Требования:
-- - PostgreSQL 12+
-- - База данных должна быть создана заранее
--
-- Использование:
-- psql -U <your_username> -d greenmind -f 01_schema.sql
-- ============================================================================

-- Удаление существующих объектов (для пересоздания схемы)
-- ВНИМАНИЕ: Это удалит все данные! Используйте с осторожностью!
DROP TABLE IF EXISTS user_products CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TYPE IF EXISTS user_role CASCADE;

-- ============================================================================
-- Создание пользовательских типов
-- ============================================================================

-- Тип для ролей пользователей
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

-- ============================================================================
-- Таблица: users
-- Описание: Хранит информацию о пользователях системы
-- ============================================================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    birth_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    role user_role NOT NULL DEFAULT 'USER'
);

-- Уникальный индекс для email (используется при аутентификации)
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- Индекс для роли (для фильтрации пользователей по ролям)
CREATE INDEX idx_users_role ON users(role);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE users IS 'Таблица пользователей системы';
COMMENT ON COLUMN users.id IS 'Уникальный идентификатор пользователя';
COMMENT ON COLUMN users.name IS 'Отображаемое имя пользователя';
COMMENT ON COLUMN users.password_hash IS 'BCrypt хэш пароля';
COMMENT ON COLUMN users.email IS 'Email для аутентификации';
COMMENT ON COLUMN users.birth_date IS 'Дата рождения пользователя';
COMMENT ON COLUMN users.created_at IS 'Дата и время регистрации';
COMMENT ON COLUMN users.updated_at IS 'Дата и время последнего обновления профиля';
COMMENT ON COLUMN users.role IS 'Роль пользователя в системе (USER или ADMIN)';

-- ============================================================================
-- Таблица: products
-- Описание: Каталог продуктов (растений)
-- ============================================================================
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    image_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0)
);

-- Индекс для поиска по имени продукта
CREATE INDEX idx_products_name ON products(name);

-- Индекс для фильтрации по цене
CREATE INDEX idx_products_price ON products(price);

-- Индекс для товаров в наличии
CREATE INDEX idx_products_quantity ON products(quantity) WHERE quantity > 0;

-- Комментарии к таблице и колонкам
COMMENT ON TABLE products IS 'Каталог продуктов (растений)';
COMMENT ON COLUMN products.id IS 'Уникальный идентификатор продукта';
COMMENT ON COLUMN products.name IS 'Название продукта';
COMMENT ON COLUMN products.price IS 'Цена продукта в рублях';
COMMENT ON COLUMN products.image_path IS 'Относительный путь к изображению продукта';
COMMENT ON COLUMN products.created_at IS 'Дата и время добавления продукта в каталог';
COMMENT ON COLUMN products.quantity IS 'Количество единиц товара на складе';

-- ============================================================================
-- Таблица: user_products
-- Описание: Связующая таблица для корзины покупок (many-to-many с атрибутами)
-- ============================================================================
CREATE TABLE user_products (
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
    PRIMARY KEY (user_id, product_id)
);

-- Индекс для быстрого поиска товаров конкретного пользователя
CREATE INDEX idx_user_products_user_id ON user_products(user_id);

-- Индекс для анализа популярности товаров
CREATE INDEX idx_user_products_product_id ON user_products(product_id);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE user_products IS 'Корзина покупок пользователей';
COMMENT ON COLUMN user_products.user_id IS 'ID пользователя';
COMMENT ON COLUMN user_products.product_id IS 'ID продукта';
COMMENT ON COLUMN user_products.quantity IS 'Количество единиц товара в корзине';

-- ============================================================================
-- Триггер для автоматического обновления updated_at в таблице users
-- ============================================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- Проверка корректности создания схемы
-- ============================================================================
DO $$
DECLARE
    table_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO table_count
    FROM information_schema.tables
    WHERE table_schema = 'public'
    AND table_type = 'BASE TABLE'
    AND table_name IN ('users', 'products', 'user_products');

    IF table_count = 3 THEN
        RAISE NOTICE 'Схема базы данных успешно создана!';
        RAISE NOTICE 'Созданы таблицы: users, products, user_products';
    ELSE
        RAISE EXCEPTION 'Ошибка создания схемы. Ожидалось 3 таблицы, создано %', table_count;
    END IF;
END $$;