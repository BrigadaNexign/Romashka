# Romashka
## Autotest
Чтобы работать с автотестами, необходимо распаковать архива Autotest.7z в корень проекта
## Запуск 1.ApiTests
```bash
pytest .\{Name_of_file.py}
```
## Запуск 2.CDRvaidateBRT
```bash
pytest -s .\{Name_of_file.py}
```
## Запуск 3.e2eProcess
```bash
pytest .\{Name_of_file.py}
```
## Запуск 4.GenerateCDR
```bash
.\{Name_of_file.py}
```
## Работа с Allure report
### Запуск автотеста с создание файлов отчета 
```bash
pytest .\{Name_of_file.py} --alluredir allure-results
```
### Просмотр отчета
```bash
allure serve allure-results
```
## CDR

Описание API:
POST /api/cdr/generate/year/2
POST /api/cdr/generate/month/6
POST /api/cdr/generate/week/3
POST /api/cdr/generate/custom?start=2023-01-01T00:00&end=2023-06-01T00:00

Для отправки данных в BRT был выбран формат CSV, так как в настоящих ATC используется именно он

Для запуска в Docker

```
docker-compose build
```

```
docker-compose up -d
```

Для остановки

```
docker-compose down
```

## CRM

## Эндпоинты аутентификации

#### Возвращают JWT-токен

### Регистрация пользователя
```
POST /api/auth/sign-up
```
Тело запроса (SignUpRequest):
```json
{
  "username": "String",
  "email": "user@example.com",
  "msisdn": "79992224466",
  "password": "String",
  "role": "MANAGER|SUBSCRIBER"
}
```
Валидация:
- Имя пользователя: обязательно, не пустое
- Email: обязательно, валидный формат (5-255 символов)
- Номер телефона: обязательно, российский формат (^[7-8]\d{10}$)
- Пароль: максимум 255 символов
- Роль: обязательно (MANAGER или SUBSCRIBER)

### Авторизация
```
POST /api/auth/sign-in
```
Тело запроса (SignInRequest):
```json
{
  "username": "String",
  "password": "String"
}
```
Валидация:
- Имя пользователя: обязательно, не пустое
- Пароль: обязательно, 8-255 символов

## Base URL
```http://localhost:8083```

## Authentication
Все запросы требуют JWT токена с ролю MANAGER / SUBSCRIBER в заголовке:
```
Authorization: Bearer <token>
```

## Manager Endpoints

### 1. Управление абонентами

#### Создать абонента
```
POST /api/manager/subscriber/create
```
Тело запроса:
```json
{
  "name": "string",
  "msisdn": "79991234567",
  "tariffId": 11,
  "balance": 100.0,
  "minutes": 100,
  "paymentDay": "2025-01-01"
}
```
Обязательные поля:
- `name` - имя абонента
- `msisdn` - номер телефона (11 цифр, начинается с 7 или 8)
- `tariffId` - ID тарифного плана

Опциональные поля:
- `balance` - начальный баланс (по умолчанию: 100.0)
- `minutes` - доступные минуты (по умолчанию: 0)
- `paymentDay` - дата следующего платежа (по умолчанию: текущая дата + 1 месяц)

#### Пополнить баланс
```
POST /api/manager/subscriber/{msisdn}/balance/top-up
```
Тело запроса:
```json
{
  "amount": 100.0
}
```

#### Сменить тариф
```
POST /api/manager/subscriber/{msisdn}/tariff/change-tariff
```
Тело запроса:
```json
{
  "tariffId": 12
}
```

#### Получить информацию об абоненте
```
GET /api/manager/subscriber/{msisdn}
```

### 2. Управление тарифами

#### Получить детали тарифа
```
GET /api/manager/tariffs/{tariffId}
```

#### Получить все тарифы
```
GET /api/manager/tariffs?sortBy=name
```

## Subscriber Endpoints

### Пополнить баланс
```
POST /api/user/subscriber/balance/top-up
```
Тело запроса:
```json
{
  "msisdn": "79991234567",
  "amount": 100.0
}
```

## Ответы на ошибки

| Код | Описание |
|-----|----------|
| 400 | Невалидные входные данные |
| 403 | Доступ запрещен |
| 404 | Ресурс не найден |
| 500 | Внутренняя ошибка сервера |
