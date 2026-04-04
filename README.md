# ☀️ Gestão de Estoque — Setor de Energia Solar

Sistema de gerenciamento de estoque desenvolvido para uma empresa do setor de energia solar. A aplicação controla entradas e saídas de produtos, calculando o estoque atual em tempo real por meio de movimentações registradas no banco de dados.

---

## 🚀 Tecnologias Utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java | Linguagem principal |
| Spring Boot | Framework da aplicação |
| Spring Security + JWT | Autenticação e autorização |
| PostgreSQL | Banco de dados relacional |
| Docker | Containerização da aplicação e do banco |

---

## 📦 Funcionalidades

- **Autenticação e Autorização** com Spring Security e tokens JWT
- **Gestão de Produtos** — cadastro e consulta de itens do estoque
- **Movimentações de Estoque** — registro de entradas e saídas
- **Cálculo Automático do Estoque** — o saldo é calculado via queries, somando todas as entradas e subtraindo todas as saídas registradas
- **Histórico de Movimentações** — rastreabilidade completa de cada operação

---

## 🏗️ Arquitetura

```
┌─────────────────────────────┐
│        Cliente / API        │
└────────────┬────────────────┘
             │ HTTP (JWT)
┌────────────▼────────────────┐
│   Container: Aplicação      │
│   Spring Boot               │
│   Porta: 8080               │
└────────────┬────────────────┘
             │
┌────────────▼────────────────┐
│   Container: Banco de Dados │
│   PostgreSQL                │
│   Porta: 5432               │
└─────────────────────────────┘
```

### Lógica de Cálculo do Estoque

O saldo de cada produto é calculado dinamicamente por query, sem armazenar um campo de saldo fixo:

```sql
SELECT
    produto_id,
    SUM(CASE WHEN tipo = 'ENTRADA' THEN quantidade ELSE 0 END) -
    SUM(CASE WHEN tipo = 'SAIDA'   THEN quantidade ELSE 0 END) AS estoque_atual
FROM movimentacoes
GROUP BY produto_id;
```

Isso garante consistência e rastreabilidade total — cada movimentação é um registro imutável.

---

## 🔐 Autenticação

A API utiliza autenticação via **JWT (JSON Web Token)**.

### Fluxo

1. Faça login enviando suas credenciais para o endpoint de autenticação
2. Receba o token JWT na resposta
3. Inclua o token no header de todas as requisições protegidas

```http
Authorization: Bearer <seu_token_jwt>
```

---

## 📡 Endpoints Principais

### Autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/auth/login` | Realiza login e retorna o token JWT |
| POST | `/auth/register` | Cadastra novo usuário |

### Produtos

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/produtos` | Lista todos os produtos |
| GET | `/produtos/{id}` | Busca produto por ID |
| POST | `/produtos` | Cadastra novo produto |
| PUT | `/produtos/{id}` | Atualiza produto |
| DELETE | `/produtos/{id}` | Remove produto |

### Movimentações

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/movimentacoes` | Lista todas as movimentações |
| POST | `/movimentacoes/entrada` | Registra entrada de estoque |
| POST | `/movimentacoes/saida` | Registra saída de estoque |

### Estoque

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/estoque` | Consulta saldo atual de todos os produtos |
| GET | `/estoque/{produtoId}` | Consulta saldo atual de um produto específico |

> Os endpoints acima são exemplos baseados na descrição do projeto. Ajuste conforme os endpoints reais da sua aplicação.

---

## 🗂️ Estrutura do Projeto

```
src/
└── main/
    └── java/
        └── com/empresa/estoque/
            ├── config/          # Configurações de segurança e JWT
            ├── controller/      # Controllers REST
            ├── dto/             # Data Transfer Objects
            ├── model/           # Entidades JPA
            ├── repository/      # Repositórios Spring Data
            ├── service/         # Regras de negócio
            └── security/        # Filtros e utilitários JWT
```

---

## 🐳 Docker Compose

O projeto utiliza dois containers:

- **app** — imagem da aplicação Spring Boot
- **db** — imagem oficial do PostgreSQL

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      - SPRING_DATASOURCE_URL=${DB_URL}
      - SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}

  db:
    image: postgres:15
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: estoque
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
```

---

## 👨‍💻 Autor

Desenvolvido por **Victor Macalin**

---

## 📄 Licença

Este projeto é de uso privado e foi desenvolvido para uso interno empresarial.
