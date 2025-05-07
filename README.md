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
