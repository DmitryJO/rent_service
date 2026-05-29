# План разработки проекта Rent Service

## Общий подход
1. **Поэтапный** – каждый крупный блок реализуется полностью, покрывается тестами и проверяется вручную.  
2. **TDD** – пишем тесты **до** кода.  
3. **CI/CD** – после каждого коммита проверяется сборка, тесты и статический анализ.  
4. **Документация** – поддерживаем актуальный OpenAPI‑спецификацию и Swagger UI.  

## Порядок выполнения шагов

### 1. Инициализация проекта ✅
1.1. Создать Maven‑проект с `spring-boot-starter-parent`, Java 17.  
1.2. Добавить **минимальные** зависимости, необходимые для работы веб‑слоя и тестов:  
- `spring-boot-starter-web`  
- `spring-boot-starter-validation`  
- `spring-boot-starter-test` (JUnit 5, Mockito, Spring Test)  
- `springdoc-openapi-starter-webmvc-ui` (для будущей документации)  
- `lombok` (optional)  

> **Тест:** выполнить `mvn clean verify` – проект без кода должен собрать‑ся.  
> **Статус (2026‑05‑29):** выполнено. `pom.xml` восстановлен (JPA, Liquibase, OAuth2, OpenFeign и др.); добавлен `spring-boot-starter-web` для REST-слоя; `mvn clean verify` — **BUILD SUCCESS**.

### 2. Структура пакетов ✅
Создать базовые пакеты:

```
src/main/java/ru/dmsmirnov/rent/
    ├─ api/
    │   └─ controller/
    │       ├─ advice/                    (глобальная обработка ошибок)
    │       └─ rest/                      (REST-контроллеры)
    ├─ domain/
    │   ├─ enums/
    │   ├─ exception/
    │   └─ service/
    └─ infrastructure/
        ├─ configuration/                 (конфигурация Spring, Security, БД)
        └─ store/                         (слой хранения)
            ├─ repository/
            └─ entity/
```

> **Статус (2026‑05‑29):** выполнено. Пакеты созданы с `package-info.java`.

### 3. **Заглушки** контроллеров и их тесты ✅
3.1. **AuthController** – эндпоинты `POST /api/v1/auth/register` и `POST /api/v1/auth/login`.  
- Реализовать методы, возвращающие `ResponseEntity.ok().build()` (или простое сообщение).  

3.2. **ItemController** – эндпоинт `GET /api/v1/items`.  
- Возвращать пустой список `Collections.emptyList()`.

3.3. **CategoryController** – эндпоинт `GET /api/v1/categories`.  

3.4. Добавить `@RestControllerAdvice`‑класс `ControllerAdvice` в `api/controller/advice/` (пока без логики).  

3.5. **Тесты**:  
- Для каждого контроллера написать интеграционный тест с `@SpringBootTest` + `MockMvc`.  
- Проверять статус **200** и ожидаемое (пустое) тело.  

> **Тест:** `mvn test` – все тесты должны пройти, подтверждая, что контроллеры «живут».  
> **Статус (2026‑05‑29):** выполнено. 4 теста (`AuthControllerTest` ×2, `ItemControllerTest`, `CategoryControllerTest`) — все пройдены.

### 4. Swagger / OpenAPI (базовая настройка) ✅
4.1. Настроить **Springdoc OpenAPI** — `OpenApiConfiguration` в `infrastructure/configuration/`.  
4.2. Разрешить доступ к Swagger UI в `SecurityConfiguration` (без авторизации).  
4.3. Добавить `openapi.yaml` в `src/main/resources` — базовая спецификация текущих эндпоинтов.  
4.4. Аннотации `@Tag`, `@Operation` на REST-контроллерах.  

> **Пути:**  
> - Swagger UI: `/swagger-ui/index.html`  
> - OpenAPI JSON: `/v3/api-docs`  
> - Через NGINX (опционально): `{private-prefix-path}/swagger-ui` → проксируется на `/swagger-ui`  

> **Тест:** `OpenApiDocumentationTest` — api-docs и swagger-ui отвечают **200**.  
> **Статус (2026‑05‑29):** выполнено. `mvn test` — 6/6 passed.

### 5. Подготовка к подключению базы данных ✅
5.1. Добавить в `pom.xml` зависимости, необходимые для работы с PostgreSQL и Liquibase:  
- `spring-boot-starter-data-jpa`  
- `postgresql`  
- `liquibase-core`  

5.2. В `application.yml` добавить placeholder‑ы для `spring.datasource.*` и `liquibase.*`.  

5.3. Создать `DatabaseConfiguration` в `infrastructure/configuration/` (заготовка под JPA).  

> **Переменные окружения:** `RENT_SERVICE_DB_JDBC_URL`, `RENT_SERVICE_DB_USERNAME`, `RENT_SERVICE_DB_PASSWORD`, `RENT_SERVICE_DB_SCHEMA`, `RENT_SERVICE_LIQUIBASE_ENABLED`.  

> **Тест:** собрать проект (`mvn verify`) – без реального подключения к БД.  
> **Статус (2026‑05‑29):** выполнено. Зависимости в `pom.xml`, placeholder‑ы в `application.yml`, `DatabaseConfigurationTest` — passed.

### 6. Моделирование БД и миграции (Liquibase) ✅
6.1. В `src/main/resources/db/changelog/` создать `master.xml`, включающий отдельные changelog‑файлы.  

6.2. **Changelog‑файлы** (по одному):
- `V1__create_tables.xml` – таблицы `users`, `categories`, `items`, `rental_orders` (с колонкой `version` для Optimistic Lock).  
- `V2__add_indexes.xml` – индексы `user_id`, `item_id`, `status`, `start_date`.  

6.3. Выполнить миграцию локально: `mvn liquibase:update`.  

> **Схема `rent_service`:**  
> - `users` — email, password_hash, role  
> - `categories` — name, description (+ seed: Стройка, Съёмка, Игровые приставки)  
> - `items` — category_id, name, description, condition, price_per_day, quantity, image_urls (JSONB)  
> - `rental_orders` — user_id, item_id, customer_full_name, customer_phone, start_date, end_date, status, **version**  

> **Запуск миграции:**  
> ```bash
> docker compose up postgres -d
> mvn liquibase:update
> ```  

> **Тест:** `mvn verify` — `LiquibaseChangelogValidationTest`; миграция на PostgreSQL — `liquibase:update`.  
> **Статус (2026‑05‑29):** выполнено. Changelog создан; `mvn verify` — passed.

### 7. Реализация репозиториев и сервисов (с БД) ✅
7.1. JPA‑репозитории в `infrastructure/store/repository`: `UserRepository`, `CategoryRepository`, `ItemRepository`, `RentalOrderRepository`.  

7.2. Сущности в `infrastructure/store/entity` с `@Entity`, `@Table`, `@Version`.  

7.3. Сервисы в `domain/service` (`CategoryService`, `ItemService`, `RentalOrderService`) с базовыми CRUD‑операциями.  

7.4. Unit‑тесты сервисов (использовать H2 в режиме `mem` для быстрых тестов).  

> **Тест:** `mvn test` – все репозитории и сервисы работают.  
> **Статус (2026‑05‑29):** выполнено. 4 entity, 4 repository, 3 service; 15/15 тестов passed.

### 8. Расширение контроллеров (работа с БД)
8.1. Обновить `ItemController` и `CategoryController` – использовать сервисы, возвращать реальные DTO.  

8.2. Добавить поиск/фильтрацию в `ItemController` (`GET /api/v1/items` с параметрами).  

> **Тест:** интеграционные тесты проверяют, что запросы к эндпоинтам возвращают данные из БД.  
> **Статус (2026‑05‑29):** выполнено. DTO (`CategoryResponse`, `ItemResponse`), мапперы, фильтрация items; 18/18 тестов passed.

### 9. Кеширование (Redis) – по мере необходимости
9.1. Добавить зависимость `spring-boot-starter-data-redis`.  
9.2. В `application.yml` добавить конфигурацию Redis (placeholder).  
9.3. В `ItemService` пометить методы `@Cacheable("items")`, `@CacheEvict` при изменениях.  

> **Тест:** изменить запись и убедиться, что кэш сбрасывается (можно через `CacheManager` в тесте).  
> **Статус (2026‑05‑29):** выполнено. Redis + `CacheConfiguration`, `@Cacheable`/`@CacheEvict` в `ItemService`; тесты на `simple` cache, 20/20 passed.

### 10. Бизнес‑логика бронирования
10.1. DTO `CreateOrderRequest`.  
10.2. `RentalOrderService.createOrder`:
- `@Transactional`
- Пессимистическая блокировка `SELECT … FOR UPDATE` по `Item`.
- Проверка конфликтов дат.
- Уменьшение `quantity`, сохранение `RentalOrder` с `@Version`.  

10.3. `OrderController` (`POST /api/v1/orders`).  

> **Тест:** параллельные запросы на один предмет – один succeeds, остальные получают `InsufficientAvailabilityException`.  
> **Статус (2026‑05‑29):** выполнено. `createOrder` с `FOR UPDATE`, проверка пересечений дат, `OrderController`, 26/26 тестов passed.

### 11. Администрирование заказов
11.1. `AdminOrderController` (`/api/v1/admin/orders/**`).  
11.2. Сервисы для изменения статуса, отмены и возврата количества.  

> **Тест:** проверка доступа только пользователям с ролью ADMIN (пока заглушка, но уже прописаны проверки).  
> **Статус (2026‑05‑29):** выполнено. `AdminOrderController`, `updateStatus`/`cancelOrder`, `@PreAuthorize` + заглушка `X-Admin-Role`, 32/32 тестов passed.

### 12. Подключение безопасности (JWT, Spring Security)
12.1. Добавить зависимости `spring-boot-starter-security`, `jjwt-api`, `jjwt-impl`, `jjwt-jackson`.  
12.2. Реализовать `JwtTokenProvider`, `SecurityConfig`, `JwtAuthenticationFilter`.  
12.3. `AuthController` теперь генерирует JWT‑токены.  
12.4. Защищаем все эндпоинты, кроме `/api/v1/auth/**` и публичных `GET /items`, `GET /categories`.  

> **Тест:** регистрировать, логинить, проверять 401/403 при недоступе.

### 12. Хранение изображений (S3/MinIO) – опционально
12.1. Добавить зависимость `aws-java-sdk-s3` или `io.minio:minio`.  
12.2. `StorageService` + эндпоинт загрузки изображения в `ItemAdminController`.  

### 13. Отзывы и рейтинг – после MVP
13.1. Таблица `reviews`, сервис и контроллер.  

### 14. Уведомления (email/SMS) – базовая реализация
14.1. `spring-boot-starter-mail`, `NotificationService`.  

### 15. Rate Limiting (Bucket4j)
15.1. Добавить конфигурацию в `SecurityConfig`.  

### 16. Мониторинг и метрики
16.1. Actuator + Prometheus (`micrometer-registry-prometheus`).  

### 17. OpenAPI документация (расширение)
17.1. Поддерживать актуальность `openapi.yaml` по мере добавления эндпоинтов.  
17.2. Добавлять `@Parameter`, схемы DTO, примеры запросов/ответов.  

> **Примечание:** базовая настройка выполнена на **шаге 4**.  

### 18. CI/CD (GitHub Actions) – опционально
18.1. Workflow: `mvn clean verify`, сборка Docker‑образа, (по желанию) деплой.  

### 19. Финальная проверка и релиз
- Полный набор интеграционных тестов (`mvn verify`).  
- Проверка безопасности, кеширования, rate‑limiting.  
- Тег версии, подготовка Docker‑образа.  

---

## Как будет проверяться каждый шаг
1. **Команды** – агент запускает нужные Maven/LIQUIBASE/Gradle‑команды.  
2. **Результаты** – присылает вывод тестов, логов, скриншоты при необходимости.  
3. **Подтверждение** – после успешного прохода агент переходит к следующему пункту.  

*Этот план сохранён в файле **DEV_PLAN.md** и служит чек‑листом для пошаговой реализации проекта.*