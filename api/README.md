## Проект по миграции расчета цен между городами 

После запуска апи доступно тут

http://localhost:8080/swagger-ui/index.html

## DB
Используется postgis.
Из функционала - пока только поиск по радиусу от заданной точки.

pgAdmin
`docker run -p 5050:80 -e "PGADMIN_DEFAULT_EMAIL=user@domain.com" -e "PGADMIN_DEFAULT_PASSWORD=SuperSecret" -d dpage/pgadmin4`