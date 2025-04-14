insert into tariffs
values (11, 'Классика', 'Классический тариф с бесплатными входящими звонками'),
(12, 'Помесячный', 'Помесячный тариф с минутами для звонков');

insert into call_pricing (tariff_id, call_type, cost_per_min)
values (11, 1, 1.5), (11, 2, 2.5), (12, 1, 1.5), (11, 2, 2.5);

insert into params (param_name, param_desc, units)
values ('Минуты', 'Общие минуты для любых звонков', 'min');

insert into tariff_params (tariff_id, param_id, param_value)
values (12, 1, 50);

insert into tariff_intervals (tariff_id, interval, price)
values (12, 30, 100);
