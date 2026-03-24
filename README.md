# GreenMind — интернет-магазин растений

<div align="center">

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Supported-2496ED.svg)](https://www.docker.com/)

Полнофункциональный интернет-магазин комнатных растений с административной панелью, управлением корзиной и ролевой моделью доступа.

</div>

## О проекте

GreenMind — веб-приложение для электронной коммерции на Spring Boot. Проект охватывает полный цикл покупки: каталог товаров, корзина с защитой от race conditions (pessimistic locking), регистрация и аутентификация, административная панель для управления товарами и пользователями.

### Возможности

- **Каталог и корзина** — просмотр товаров, добавление в корзину, управление количеством
- **Аутентификация** — сессионная авторизация, BCrypt-хеширование паролей, CSRF-защита
- **Административная панель** — CRUD товаров, управление пользователями и ролями
- **Адаптивный интерфейс** — Vanilla JS (ES6+), модульная архитектура, toast-уведомления

## Технологический стек

| Слой | Технологии |
|------|-----------|
| **Backend** | Java 21, Spring Boot 3.5.4, Spring Security, Spring Data JPA / Hibernate |
| **Frontend** | Vanilla JavaScript (ES6+), модульный CSS |
| **База данных** | PostgreSQL 16 |
| **Инфраструктура** | Docker, Docker Compose, Maven |
| **Тестирование** | JUnit 5, Mockito, AssertJ |

## Быстрый старт

### Вариант 1: Docker Compose 
Самый быстрый способ — поднять всё одной командой. Docker Compose запустит PostgreSQL и приложение, автоматически применит SQL-скрипты и настроит подключение.

```bash
git clone git@github.com:QDNGX/GreenMind.git
cd GreenMind
docker-compose up --build
```

Приложение будет доступно по адресу: `http://localhost:8080`

> БД инициализируется автоматически из скриптов в `backend/database/`. Данные сохраняются в Docker volume `pgdata`.

### Вариант 2: Локальная установка

**Требования:** Java 21+, Maven 3.8+, PostgreSQL 14+

1. **Настройка базы данных**

```bash
psql -U postgres -c "CREATE DATABASE greenmind;"
psql -U postgres -c "CREATE USER your_user WITH PASSWORD 'your_password';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE greenmind TO your_user;"

cd backend/database
psql -U your_user -d greenmind -f 01_schema.sql
psql -U your_user -d greenmind -f 02_initial_data.sql
psql -U your_user -d greenmind -f 03_sample_data.sql  # опционально
```

2. **Конфигурация подключения**

Создайте файл `backend/src/main/resources/application-db.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/greenmind
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
```

3. **Запуск**

```bash
cd backend
mvn spring-boot:run
```

### Учётные данные по умолчанию

| Роль | Email | Пароль |
|------|-------|--------|
| Администратор | `admin@greenmind.ru` | `Admin123!` |
| Тестовый пользователь | `ivan@example.com` | `Test123!` |
| Тестовый пользователь | `maria@example.com` | `Test123!` |

## Архитектура

### Структура проекта

```
GreenMind/
├── backend/
│   ├── src/
│   │   ├── main/java/ru/kolbasov_d_k/backend/
│   │   │   ├── config/          # Spring Security, CSRF, профили
│   │   │   ├── controllers/     # REST API контроллеры
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── models/          # JPA-сущности
│   │   │   ├── repositories/    # Data Access Layer
│   │   │   ├── services/        # Бизнес-логика
│   │   │   └── utils/           # Утилиты, обработка исключений
│   │   ├── main/resources/
│   │   │   ├── application.properties
│   │   │   ├── application-dev.properties
│   │   │   └── application-docker.properties
│   │   └── test/                # Unit-тесты (JUnit 5 + Mockito)
│   ├── database/                # SQL-скрипты инициализации
│   └── pom.xml
├── frontend/
│   ├── css/
│   ├── js/
│   │   ├── components/          # UI-компоненты
│   │   ├── pages/               # Страничная логика
│   │   └── utils/               # API-хелперы, утилиты
│   └── *.html
├── Dockerfile                   # Multi-stage build
├── docker-compose.yml           # PostgreSQL + приложение
└── index.html
```

### Ключевые архитектурные решения

- **DTO-паттерн** — разделение моделей API и JPA-сущностей; чувствительные данные (password hash) не попадают в ответы
- **Centralized Exception Handling** — `GlobalExceptionHandler` обрабатывает 14+ типов исключений, единообразные JSON-ответы
- **Pessimistic Locking** — `@Lock(PESSIMISTIC_WRITE)` на `ProductRepository.findByIdForUpdate()` предотвращает overselling при конкурентных запросах
- **JOIN FETCH** — предотвращение N+1 проблемы при загрузке связанных сущностей
- **JPA Auditing** — автоматическое управление `createdAt` / `updatedAt` через `@CreatedDate` / `@LastModifiedDate`
- **Spring Profiles** — разделение конфигурации на `dev`, `docker` и production

## API

### Публичные

| Метод | Endpoint | Описание |
|-------|----------|----------|
| `POST` | `/register` | Регистрация пользователя |
| `POST` | `/login` | Аутентификация |
| `GET` | `/api/session` | Статус сессии |

### Пользовательские (требуют аутентификации)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| `GET` | `/api/user/profile` | Профиль пользователя |
| `GET` | `/api/cart` | Содержимое корзины |
| `POST` | `/api/cart` | Добавление товара в корзину |
| `PATCH` | `/api/cart` | Обновление количества |
| `DELETE` | `/api/cart/{productId}` | Удаление из корзины |

### Административные (роль ADMIN)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| `GET` | `/api/admin/users` | Список пользователей |
| `PATCH` | `/api/admin/users/{id}/role` | Изменение роли |
| `DELETE` | `/api/admin/users/{id}` | Удаление пользователя |
| `GET` | `/api/admin/products` | Список товаров |
| `POST` | `/api/admin/products` | Создание товара |
| `PUT` | `/api/admin/products/{id}` | Обновление товара |
| `DELETE` | `/api/admin/products/{id}` | Удаление товара |

## Безопасность

- **BCrypt** — хеширование паролей
- **Spring Security** — ролевая модель доступа (USER / ADMIN)
- **CSRF Protection** — cookie-based токены (`CookieCsrfTokenRepository`) + заголовок `X-XSRF-TOKEN`
- **Session-based Authentication** — управление сессиями на стороне сервера
- **Bean Validation** — валидация входных данных на уровне DTO (`@NotBlank`, `@Email`, `@Size`, `@Pattern`)
- **Pessimistic Locking** — защита от race conditions при операциях с корзиной

## Автор

**Колбасов Данил** — dkolbasov13@gmail.com

## Лицензия

MIT. См. файл [LICENSE](LICENSE).
