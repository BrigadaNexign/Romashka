INSERT INTO tariffs VALUES
(11, 'Классика', 'Классический тариф с бесплатными входящими звонками'),
(12, 'Помесячный', 'Помесячный тариф с минутами для звонков');

INSERT INTO call_pricing (tariff_id, call_type, cost_per_min)
VALUES (11, 1, 1.5), (11, 2, 2.5), (12, 1, 1.5), (11, 2, 2.5);

INSERT INTO params (param_id, param_name, param_desc, units)
VALUES (1, 'Минуты', 'Общие минуты для любых звонков', 'min');

INSERT INTO tariff_params (tariff_id, param_id, param_value)
VALUES (12, 1, 50);

INSERT INTO tariff_intervals (tariff_id, interval, price)
VALUES (12, 30, 100);