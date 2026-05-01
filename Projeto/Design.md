# Design do Sistema — udfilasystem

## Visão Geral

Aplicação monolítica para gerenciamento de filas com autenticação segura em 2 fatores. O sistema é dividido em duas entregas: o backend em Java Spring Boot e o frontend em React + Tailwind CSS.

---

## Estrutura em Camadas (Backend)

```
Cliente (Browser)
      │
      ▼
[ Controller ]        — Recebe requisições HTTP, valida entrada, retorna resposta
      │
      ▼
[ Service ]           — Regras de negócio, orquestração de operações
      │
      ▼
[ Repository ]        — Acesso ao banco de dados via JPA/Hibernate
      │
      ▼
[ MySQL 8 ]           — Persistência relacional com migrations Flyway
```

---

## Módulos Principais

| Módulo        | Responsabilidade                                               |
|---------------|----------------------------------------------------------------|
| `AuthService` | Cadastro, login em 2 etapas, emissão e renovação de tokens    |
| `TotpService` | Geração de segredo, verificação de código, geração de QR Code |
| `FilaService` | Criação de filas, entrada/saída, controle de posições         |
| `JwtService`  | Emissão, validação e extração de dados do token JWT           |

---

## Fluxo de Autenticação

```
Cadastro:
  [Formulário] → POST /api/auth/cadastrar
              → Gera hash BCrypt + segredo TOTP
              → Salva usuário no banco
              → Retorna QR Code Base64
              → Usuário escaneia com Microsoft Authenticator
              → POST /api/auth/totp/confirmar (código TOTP)
              → Emite JWT + Refresh Token

Login:
  [Etapa 1] → POST /api/auth/login/etapa1 (email + senha)
            → Valida credenciais
  [Etapa 2] → POST /api/auth/login/etapa2 (email + código TOTP)
            → Valida código (janela 30s, RFC 6238)
            → Emite JWT (1h) + Refresh Token (24h)
```

---

## Banco de Dados

| Tabela           | Propósito                                          |
|------------------|----------------------------------------------------|
| `usuarios`       | Dados cadastrais, hash da senha e segredo TOTP     |
| `refresh_tokens` | Tokens de renovação com controle de revogação      |
| `filas`          | Filas de atendimento com capacidade máxima         |
| `entradas_fila`  | Posições dos usuários com status e timestamps      |
| `login_attempts` | Auditoria de tentativas de acesso                  |

---

## Segurança

- **Senhas:** BCrypt fator 12 — resistente a força bruta
- **Tokens:** JWT assinado (HS256) + Refresh Token UUID no banco
- **2FA:** TOTP RFC 6238, janela de 30 segundos
- **API:** Stateless, sem sessão no servidor, protegida por filtro JWT
- **Erros:** Respostas padronizadas RFC 9457 sem leak de detalhes internos

---

## Frontend

| Página       | Descrição                                               |
|--------------|---------------------------------------------------------|
| `/`          | Entrada do sistema — botões Entrar e Cadastrar          |
| `/login`     | Etapa 1 (email/senha) → Etapa 2 (código TOTP)          |
| `/cadastrar` | Formulário → QR Code para configurar o autenticador     |
| `/dashboard` | Área autenticada pós-login                              |

---

## Decisões de Design

- **Monolítico:** Escolha deliberada para simplicidade operacional. A separação em camadas permite extrair serviços futuramente se necessário.
- **Flyway:** Versionamento explícito do esquema do banco — rastreável e reversível.
- **Records Java:** DTOs implementados como `record` — imutáveis, concisos e seguros.
- **Problem Details (RFC 9457):** Padrão de resposta de erro adotado pelo Spring Boot 3 — interoperável e sem exposição de internos.
