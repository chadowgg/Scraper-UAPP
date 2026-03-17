## API
### Запустити скрапінг
POST http://localhost:8080/api/scraper/start?url=https://site.com
### Зупинити скрапінг
POST http://localhost:8080/api/scraper/stop

## Конфігурація (application.properties)
### Дирикторія збереження фото
storage.path=./compressed-images
### Кількість виділених потоків
threads.pool=10

### Налаштування підключення до бази даних PostgreSQL
### URL підключення до PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/scraper
### Ім'я користувача БД
spring.datasource.username=postgres
### Пароль БД
spring.datasource.password=123456