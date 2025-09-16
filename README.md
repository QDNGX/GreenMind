# 🌿 GreenMind - E-Commerce Plant Store

<div align="center">
  
  [![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
  [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
  [![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
  [![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/yourusername/GreenMind/graphs/commit-activity)
  
  **Полнофункциональный интернет-магазин растений с административной панелью и системой управления корзиной**

</div>

## 📋 О проекте

GreenMind представляет собой современное веб-приложение для электронной коммерции, специализирующееся на продаже комнатных растений. Проект демонстрирует применение современных технологий разработки и архитектурных паттернов для создания масштабируемого и поддерживаемого приложения.

### ✨ Ключевые особенности

- **🛍️ Полный цикл покупки** - от просмотра каталога до оформления заказа
- **🔐 Безопасная аутентификация** - сессионная авторизация с BCrypt шифрованием
- **👨‍💼 Административная панель** - полный контроль над товарами и пользователями
- **🛒 Умная корзина** - автоматическое управление инвентарем с защитой от race conditions
- **📱 Адаптивный дизайн** - оптимизация для всех устройств
- **🎨 Современный UI/UX** - анимации, toast-уведомления, модальные окна

## 🏗️ Архитектура

### Технологический стек

#### Backend
- **Java 21** - современная версия с последними возможностями языка
- **Spring Boot 3.5.4** - основной фреймворк приложения
- **Spring Security** - аутентификация и авторизация
- **Spring Data JPA/Hibernate** - ORM для работы с базой данных
- **PostgreSQL** - основная база данных
- **Maven** - сборка и управление зависимостями

#### Frontend
- **Vanilla JavaScript (ES6+)** - без фреймворков для максимальной производительности
- **Модульная архитектура** - разделение на компоненты и утилиты
- **Responsive CSS** - адаптивная верстка
- **AJAX** - асинхронное взаимодействие с API



## 🚀 Быстрый старт

### Системные требования

- Java 21 или выше
- Maven 3.8+
- PostgreSQL 14+
- Node.js 20.13.1+ (для разработки frontend)

### Установка и запуск

1. **Клонирование репозитория**
```bash
git clone https://github.com/yourgithubusername/GreenMind.git
cd GreenMind
```

2. **Настройка базы данных**

Проект включает готовые SQL скрипты для инициализации базы данных, расположенные в директории `backend/database/`:

```bash
# Создание базы данных
psql -U postgres -c "CREATE DATABASE greenmind;"
psql -U postgres -c "CREATE USER ваш_пользователь WITH PASSWORD 'ваш_пароль';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE greenmind TO ваш_пользователь;"

# Применение SQL скриптов
cd backend/database
psql -U ваш_пользователь -d greenmind -f 01_schema.sql  # Структура БД
psql -U ваш_пользователь -d greenmind -f 02_initial_data.sql  # Обязательные данные
psql -U ваш_пользователь -d greenmind -f 03_sample_data.sql  # Демо-данные (опционально)
```

**Описание SQL скриптов:**
- `01_schema.sql` - создает все таблицы, индексы и триггеры
- `02_initial_data.sql` - добавляет администратора и минимальный набор продуктов
- `03_sample_data.sql` - загружает демонстрационные данные для разработки

**Учетные данные по умолчанию:**
- Администратор: `admin@greenmind.ru` / `Admin123!`
- Тестовые пользователи: `ivan@example.com`, `maria@example.com` / `Test123!`

⚠️ **Важно:** После первого входа обязательно смените пароль администратора!

3. **Запуск backend**
```bash
cd backend
mvn spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:8080`

### Конфигурация

Основные настройки находятся в `backend/src/main/resources/application.properties`:

```properties
# Импорт конфигурации базы данных
spring.config.import=classpath:application-db.properties

# API Configuration
spring.data.rest.base-path=/api

# Server Configuration
server.port=8080
```

Создайте файл `backend/src/main/resources/application-db.properties` с настройками подключения к базе данных:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/greenmind  # Ваш URL к базе данных
spring.datasource.username=ваш_пользователь  # Ваш логин к БД
spring.datasource.password=ваш_пароль  # Ваш пароль к БД
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Settings
spring.jpa.hibernate.ddl-auto=update
```

## 📂 Структура проекта

```
GreenMind/
├── backend/                      # Spring Boot приложение
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── ru/kolbasov_d_k/backend/
│   │   │   │       ├── config/         # Spring Security конфигурация
│   │   │   │       ├── controllers/    # REST API endpoints
│   │   │   │       ├── dto/           # Data Transfer Objects
│   │   │   │       ├── models/        # JPA сущности
│   │   │   │       ├── repositories/  # Data Access Layer
│   │   │   │       ├── services/      # Бизнес-логика
│   │   │   │       └── utils/         # Утилиты и обработка исключений
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-db.properties  # Конфигурация БД
│   │   │       └── static/            # Скомпилированный frontend
│   │   └── test/                 # Тесты
│   │       └── java/
│   ├── database/                 # SQL скрипты инициализации
│   │   ├── 01_schema.sql        # Структура базы данных
│   │   ├── 02_initial_data.sql  # Начальные данные
│   │   └── 03_sample_data.sql   # Демонстрационные данные
│   └── pom.xml
├── frontend/                     # Клиентская часть
│   ├── css/                     # Стили
│   ├── js/                      # JavaScript модули
│   │   ├── components/          # UI компоненты
│   │   ├── pages/              # Страничная логика
│   │   └── utils/              # Вспомогательные утилиты
│   └── *.html                  # HTML страницы
└── index.html                  # Главная страница
```

## 🔧 Разработка

### Backend разработка

```bash
# Запуск приложения
mvn spring-boot:run

# Сборка проекта
mvn clean package

# Генерация документации
mvn javadoc:javadoc
```

### База данных

Для разработки используется локальная PostgreSQL. Настройки подключения указываются в файле `application-db.properties`.

## 🔑 API Endpoints

### Публичные endpoints
- `GET /` - главная страница
- `POST /register` - регистрация пользователя
- `POST /login` - авторизация
- `GET /api/session` - проверка статуса аутентификации

### Защищенные endpoints
- `GET /api/user/profile` - профиль пользователя
- `GET /api/cart` - содержимое корзины
- `POST /api/cart` - добавление товара в корзину
- `PATCH /api/cart` - обновление количества товара
- `DELETE /api/cart/{productId}` - удаление товара из корзины

### Административные endpoints
- `GET /api/admin/users` - список всех пользователей
- `PATCH /api/admin/users/{id}/role` - изменение роли пользователя
- `DELETE /api/admin/users/{id}` - удаление пользователя
- `GET /api/admin/products` - список всех товаров
- `POST /api/admin/products` - создание товара
- `PUT /api/admin/products/{id}` - обновление товара
- `DELETE /api/admin/products/{id}` - удаление товара

## 🔒 Безопасность

### Реализованные механизмы защиты

- **BCrypt** - хеширование паролей
- **Spring Security** - контроль доступа на основе ролей (USER, ADMIN)
- **Session-based authentication** - безопасное управление сессиями
- **XSS Protection** - санитизация пользовательского ввода
- **Pessimistic Locking** - защита от race conditions при работе с корзиной
- **Input Validation** - валидация на уровне DTO с использованием Bean Validation

## 🚀 Развертывание

### Production сборка

```bash
# Полная сборка с frontend
mvn clean package

# Запуск JAR файла
java -jar backend/target/backend-0.0.1-SNAPSHOT.jar
```

### Docker (опционально)

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 🤝 Вклад в проект

Буду рад вашим предложениям по улучшению проекта! Пожалуйста, следуйте этим шагам:

1. Fork репозитория
2. Создайте feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit изменения (`git commit -m 'Add some AmazingFeature'`)
4. Push в branch (`git push origin feature/AmazingFeature`)
5. Откройте Pull Request

### Код-стайл

- Java: следуйте стандартным конвенциям Java и Spring Boot
- JavaScript: ES6+ синтаксис, модульная структура
- Комментарии: на русском языке для лучшего понимания командой

## 📝 Лицензия

Распространяется под лицензией MIT. Смотрите файл [LICENSE](LICENSE) для дополнительной информации.

## 👤 Автор

**Колбасов Данил**

Email: dkolbasov13@gmail.com

## 🙏 Благодарности

- [Spring Boot](https://spring.io/projects/spring-boot) - за отличный фреймворк
- [PostgreSQL](https://www.postgresql.org/) - за надежную базу данных
- Всем контрибьюторам open-source сообщества

---

<div align="center">
  Сделано с ❤️ для демонстрации навыков Full-Stack разработки
</div>
