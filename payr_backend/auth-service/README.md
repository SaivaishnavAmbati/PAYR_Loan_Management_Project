# cartigo-auth-service (OTP + Password + JWT)

## Requirements
- Java 17
- MySQL running
- Gmail App Password (for SMTP)

## Configure (recommended via environment variables)
- DB_PASSWORD
- MAIL_USERNAME
- MAIL_APP_PASSWORD
- JWT_SECRET

Example (Windows PowerShell):
```powershell
$env:DB_PASSWORD="your_db_password"
$env:MAIL_USERNAME="yourgmail@gmail.com"
$env:MAIL_APP_PASSWORD="xxxx xxxx xxxx xxxx"
$env:JWT_SECRET="a-very-long-secret-at-least-32-chars"
mvn spring-boot:run
```

## APIs
- POST /auth/register/send-otp
- POST /auth/register/verify-otp  -> returns JWT
- POST /auth/login/send-otp
- POST /auth/login/verify-otp     -> returns JWT
- POST /auth/login/password       -> returns JWT
- GET  /users                     -> JWT required

Swagger:
- /swagger-ui.html
