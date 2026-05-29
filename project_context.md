# Контекст проекта (project_context.md)

## Обзор

Краткое описание сервиса аренды вещей — Spring Boot приложение, позволяющее пользователям арендовать предметы из
предопределённых категорий, а администраторам — полностью управлять системой.

## Архитектура

Система состоит из следующих компонентов:

- **Клиент** (веб или мобильный) → **API‑Gateway (NGINX)** → **Spring Boot сервис** (stateless) → **PostgreSQL**, *
  *Redis**, **S3/MinIO**.
- При бронировании открывается транзакция, выполняется `SELECT … FOR UPDATE` по выбранному предмету, проверяется
  доступность и сохраняется заказ с оптимистичной блокировкой (`@Version`).

## Структура проекта

``` 
src/
├─ main/
│   ├─ java/
│   │   └─ ru/dmsmirnov/rent/
│   │       ├─ Application.java
│   │       ├─ api/
│   │       │   └─ controller/
│   │       │       ├─ advice/              — ControllerAdvice (обработка ошибок)
│   │       │       └─ rest/                — REST-контроллеры (Auth, Item, Category)
│   │       ├─ domain/
│   │       │   ├─ enums/
│   │       │   ├─ exception/
│   │       │   └─ service/
│   │       └─ infrastructure/              — инфраструктура приложения
│   │           ├─ configuration/           — конфигурация (Spring, Security, БД)
│   │           └─ store/                   — слой хранения (БД)
│   │               ├─ repository/
│   │               └─ entity/
│   └─ resources/
│       ├─ application.yml
│       ├─ openapi.yaml          — OpenAPI-спецификация
│       └─ db/changelog/         — заготовки Liquibase
└─ test/
    ├─ java/ru/dmsmirnov/rent/api/  — интеграционные тесты контроллеров
    └─ resources/application.yml    — отключение БД для тестов
```

### Текущий прогресс (MVP)

| Шаг | Статус | Примечание |
|-----|--------|------------|
| 1. Инициализация Maven/Spring Boot | ✅ | Java 17, полный набор зависимостей проекта |
| 2. Структура пакетов | ✅ | `api`, `domain`, `infrastructure` |
| 3. Заглушки контроллеров + тесты | ✅ | `mvn test` — 4/4 passed |
| 4. Swagger / OpenAPI | ✅ | `/swagger-ui/index.html` |
| 5. Подготовка БД | ✅ | JPA, PostgreSQL, Liquibase в pom + placeholder‑ы |
| 6. Миграции Liquibase | ✅ | V1/V2 changelog, seed категорий |
| 7. Репозитории и сервисы | ✅ | entity + repository + service + H2 тесты |
| 8. Контроллеры с БД | ✅ | DTO, фильтрация items, 18/18 тестов |
| 9. Redis-кеш | ✅ | `@Cacheable`/`@CacheEvict` в `ItemService`, 20/20 тестов |
| 10. Бронирование | ✅ | `createOrder`, pessimistic lock, `OrderController`, 26/26 тестов |
| 11. Админ заказы | ✅ | `AdminOrderController`, stub `X-Admin-Role`, 32/32 тестов |
| 12. JWT / Security | ⏳ | следующий шаг |

## Технологический стек

| Слой                 | Технология                                |
|----------------------|-------------------------------------------|
| Язык                 | Java 17                                   |
| Фреймворк            | Spring Boot                               |
| Безопасность         | Spring Security, JWT, BCrypt              |
| База данных          | PostgreSQL, Hibernate, Flyway             |
| Кеш                  | Caffeine, Redis                           |
| Хранилище файлов     | Amazon S3 / MinIO                         |
| Документация API     | Springdoc OpenAPI 3 (Swagger)             |
| Ограничение запросов | Bucket4j / Resilience4j                   |
| Тесты                | JUnit 5, Mockito                          |
| CI/CD                | Maven, GitHub Actions (опционально)       |
| Мониторинг           | Spring Boot Actuator, Prometheus, Grafana |

## Стиль кода

- **Версия Java**: 17, совместимость source/target указана в Maven.
- **Форматирование**: отступы 4 пробела, длина строки ≤ 120 символов.
- **Именование**: классы PascalCase, интерфейсы PascalCase, методы/поля camelCase, константы UPPER_SNAKE.
- **Lombok**: опционально для геттеров/сеттеров.
- **REST‑контроллеры**: аннотация `@RestController`, использование DTO, валидация (`@Valid`, `@NotBlank` и т.д.).
- **Обработка исключений**: централизованно через `@ControllerAdvice`.
- **Логирование**: SLF4J с Logback, уровни логов согласно важности, пароли и токены не логировать.
- **Тестирование**: описательные имена тестов, паттерн arrange‑act‑assert.

## Руководство по безопасности

- JWT access‑token + refresh‑token, хранение в HttpOnly‑куки или заголовке Authorization.
- Роли: `ROLE_USER`, `ROLE_ADMIN`, `ROLE_MODERATOR`.
- CSRF защита отключена для stateless REST API.
- Валидация входных данных и экранирование вывода для защиты от XSS, Hibernate защищает от SQL‑инъекций.
- Ограничение запросов (rate limiting) per IP/user с помощью Bucket4j.

## Производительность и масштабируемость

- Сервис stateless, состояние при необходимости хранится в Redis.
- Кеширование часто запрашиваемых данных: Caffeine (внутрипроцессный) и Redis (распределённый).
- Индексы в БД по `user_id`, `item_id`, `status`, `start_date`.
- Оптимистичная блокировка (`@Version`) для сущности `RentalOrder` и пессимистичная блокировка при бронировании.

## Стратегия тестирования

- Юнит‑тесты сервисов и утилит с JUnit 5 и Mockito.
- Интеграционные тесты контроллеров и репозиториев с `@SpringBootTest`.
- Testcontainers для PostgreSQL и Redis в CI.

## CI/CD pipeline

- Maven `clean verify` собирает проект, запускает тесты, SpotBugs, Checkstyle.
- GitHub Actions собирает Docker‑образ, запускает тесты, публикует в реестр.

## Документация

**Вся документация проекта должна вестись на русском языке.**
- Спецификация OpenAPI 3 хранится в `src/main/resources/openapi.yaml`.
- Swagger UI доступен по пути `/swagger-ui/index.html`, OpenAPI JSON — `/v3/api-docs`.

## Развёртывание

- Dockerfile создаёт лёгкий образ на основе JRE.
- Переменные окружения для URL БД, хоста Redis, учётных данных S3, секрета JWT.
- NGINX как reverse proxy, отвечает за TLS‑терминацию.