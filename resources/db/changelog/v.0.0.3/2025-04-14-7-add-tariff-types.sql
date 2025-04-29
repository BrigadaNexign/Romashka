-- Создаем таблицу типов тарифов
CREATE TABLE "type" (
    "type_id" INTEGER NOT NULL PRIMARY KEY,
    "name" VARCHAR(50) NOT NULL UNIQUE,
    "description" TEXT
);

-- Добавляем стандартные типы
INSERT INTO "type" ("type_id", "name", "description") VALUES
(1, 'interval', 'Интервальный тариф'),
(2, 'per_minute', 'Поминутный тариф'),
(3, 'combined', 'Комбинированный тариф');

-- Добавляем столбец type_id в tariffs с внешним ключом
ALTER TABLE "tariffs" ADD COLUMN "type_id" INTEGER REFERENCES "type"("type_id");

-- Обновляем существующие тарифы (на основе данных из tariff_intervals и call_pricing)
UPDATE "tariffs" SET "type_id" = 1
WHERE "tariff_id" IN (SELECT DISTINCT "tariff_id" FROM "tariff_intervals");

UPDATE "tariffs" SET "type_id" = 2
WHERE "tariff_id" IN (
    SELECT DISTINCT "tariff_id" FROM "call_pricing"
    EXCEPT
    SELECT DISTINCT "tariff_id" FROM "tariff_intervals"
);

UPDATE "tariffs" SET "type_id" = 3
WHERE "tariff_id" IN (
    SELECT DISTINCT c."tariff_id"
    FROM "call_pricing" c
    JOIN "tariff_intervals" t ON c."tariff_id" = t."tariff_id"
);