# Romashka
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

### Регистрация пользователя
```
POST /api/auth/signup
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
POST /api/auth/signin
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

## Эндпоинты управления тарифами

### Создание нового тарифа
```
POST /api/manager/tariffs
```
Тело запроса (CreateTariffRequest):
```json
{
  "name": "String",
  "description": "String",
  "intervalDays": 30,
  "price": 300.0,
  "callPrices": [
    {
      "callType": 1,
      "pricePerMinute": 1.5
    }
  ],
  "params": [
    {
      "name": "String",
      "description": "String",
      "value": 100.0,
      "units": "minutes"
    }
  ]
}
```
Валидация:
- Название: обязательно, не пустое
- Интервал дней: обязательно, положительное число
- Цена: обязательно, положительное число
- Цены звонков: для каждого типа обязательно указание цены
