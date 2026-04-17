# API Examples

## Login
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"admin","password":"Admin@123"}'

## Create customer
curl -X POST http://localhost:8080/api/customers -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d '{"firstName":"Ahmed","lastName":"Ibrahim","email":"ahmed@example.com","nationalId":"ID-123"}'

## Create account
curl -X POST http://localhost:8080/api/accounts -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d '{"customerId":1,"accountTypeCode":"CHK","currencyCode":"USD"}'
