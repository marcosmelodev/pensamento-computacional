# udfilasystem — Backend Java Spring Boot

Sistema de Gerenciamento de Filas com autenticacao em 2 fatores (TOTP/Microsoft Authenticator).

## Tecnologias

- **Java 21 LTS** + **Spring Boot 3.2**
- **Spring Security 6** (JWT + TOTP 2FA)
- **Spring Data JPA** / Hibernate
- **MySQL 8.0** com migrations Flyway
- **BCrypt** (fator 12) para hash de senhas
- **Google Authenticator / TOTP** (RFC 6238) — compativel com Microsoft Authenticator
- **ZXing** para geracao de QR Code
- **Bucket4j** para rate limiting

## Arquitetura Monolitica

```
src/
├── config/          # Configuracoes (Security, Scheduled Tasks)
├── controller/      # REST Controllers (Auth, Fila, Health)
├── dto/             # Data Transfer Objects (Records Java)
├── entity/          # Entidades JPA (Usuario, Fila, EntradaFila, RefreshToken)
├── exception/       # Exceptions customizadas + GlobalExceptionHandler
├── repository/      # Spring Data JPA Repositories
├── security/        # JWT Service, Filter, UserDetailsService
└── service/         # Logica de negocio (Auth, Fila, TOTP)
```

## Fluxo de Autenticacao

1. **Cadastro**: `POST /api/auth/cadastrar` → retorna QR Code Base64
2. **Ativar 2FA**: `POST /api/auth/totp/confirmar` → retorna JWT + Refresh Token
3. **Login Etapa 1**: `POST /api/auth/login/etapa1` → valida email/senha
4. **Login Etapa 2**: `POST /api/auth/login/etapa2` → valida TOTP → retorna JWT

## Configuracao (.env / application.yml)

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=udfilasystem
DB_USER=root
DB_PASSWORD=suasenha
JWT_SECRET=chave-secreta-256-bits-minimo
PORT=8080
```

## Como executar

```bash
mvn spring-boot:run
```

## Rodar testes

```bash
mvn test
```
