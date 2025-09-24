# Arquitetura de Microsserviços - Desafio DIO

Este projeto implementa uma arquitetura de microsserviços em Java utilizando Spring Boot, demonstrando comunicação síncrona via HTTP e assíncrona via RabbitMQ entre os serviços **Warehouse** (Armazém) e **Storefront** (Vitrine).

## 🏗️ Arquitetura do Sistema

\`\`\`
┌─────────────────┐    HTTP/REST    ┌─────────────────┐
│                 │◄───────────────►│                 │
│  Storefront     │                 │   Warehouse     │
│  Service        │                 │   Service       │
│  (Port 8080)    │                 │   (Port 8081)   │
│                 │                 │                 │
└─────────┬───────┘                 └─────────┬───────┘
          │                                   │
          │              RabbitMQ             │
          │         ┌─────────────────┐       │
          └────────►│                 │◄──────┘
                    │   Message       │
                    │   Broker        │
                    │   (Port 5672)   │
                    └─────────────────┘
\`\`\`

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring AMQP (RabbitMQ)**
- **Spring WebFlux (WebClient)**
- **H2 Database** (em memória)
- **Maven**
- **Docker & Docker Compose**

## 📋 Funcionalidades

### Warehouse Service (Armazém)
- ✅ Gerenciamento completo de produtos (CRUD)
- ✅ Controle de estoque com validações
- ✅ Alertas de estoque baixo
- ✅ API REST para consultas de produtos
- ✅ Publicação de eventos via RabbitMQ

### Storefront Service (Vitrine)
- ✅ Catálogo de produtos (consumindo Warehouse)
- ✅ Gerenciamento de pedidos
- ✅ Validação de estoque antes da compra
- ✅ Atualização automática de estoque
- ✅ Sistema de status de pedidos

### Comunicação Entre Serviços
- ✅ **Síncrona**: HTTP/REST para consultas em tempo real
- ✅ **Assíncrona**: RabbitMQ para eventos e notificações

## 🛠️ Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.6+
- Docker e Docker Compose

### 1. Iniciar RabbitMQ
\`\`\`bash
docker-compose up -d
\`\`\`

### 2. Executar Warehouse Service
\`\`\`bash
cd warehouse-service
mvn spring-boot:run
\`\`\`

### 3. Executar Storefront Service
\`\`\`bash
cd storefront-service
mvn spring-boot:run
\`\`\`

## 📡 Endpoints da API

### Warehouse Service (http://localhost:8081)

#### Produtos
- `GET /api/products` - Listar todos os produtos
- `GET /api/products/{id}` - Buscar produto por ID
- `GET /api/products/available` - Listar produtos disponíveis
- `GET /api/products/search?name={name}` - Buscar produtos por nome
- `GET /api/products/low-stock?threshold={n}` - Produtos com estoque baixo
- `POST /api/products` - Criar novo produto
- `PUT /api/products/{id}` - Atualizar produto
- `DELETE /api/products/{id}` - Remover produto

#### Estoque
- `PUT /api/products/stock` - Atualizar estoque
- `GET /api/products/{id}/stock-check?quantity={n}` - Verificar disponibilidade

### Storefront Service (http://localhost:8080)

#### Produtos (Proxy para Warehouse)
- `GET /api/storefront/products` - Catálogo de produtos
- `GET /api/storefront/products/{id}` - Detalhes do produto
- `GET /api/storefront/products/available` - Produtos disponíveis
- `GET /api/storefront/products/search?name={name}` - Buscar produtos

#### Pedidos
- `GET /api/orders` - Listar todos os pedidos
- `GET /api/orders/{id}` - Buscar pedido por ID
- `GET /api/orders/customer/{email}` - Pedidos por cliente
- `GET /api/orders/status/{status}` - Pedidos por status
- `POST /api/orders` - Criar novo pedido
- `PUT /api/orders/{id}/status?status={status}` - Atualizar status
- `PUT /api/orders/{id}/cancel` - Cancelar pedido

## 📊 Exemplos de Uso

### Criar um Produto (Warehouse)
\`\`\`bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Smartphone Samsung Galaxy S24",
    "description": "Último modelo com câmera de 200MP",
    "price": 1299.99,
    "stockQuantity": 50
  }'
\`\`\`

### Criar um Pedido (Storefront)
\`\`\`bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "João Silva",
    "customerEmail": "joao@email.com",
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ]
  }'
\`\`\`

## 🔄 Fluxo de Mensageria

### Eventos Publicados

#### Warehouse Service
- **stock.update**: Quando o estoque é atualizado
- **stock.low**: Quando o estoque fica abaixo do limite

#### Storefront Service  
- **order.created**: Quando um pedido é criado
- **order.status.update**: Quando o status do pedido muda

### Filas RabbitMQ
- `stock.update.queue`
- `low.stock.alert.queue`
- `order.created.queue`
- `order.status.update.queue`

## 🗄️ Modelo de Dados

### Warehouse Service
\`\`\`sql
-- Produtos
CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
\`\`\`

### Storefront Service
\`\`\`sql
-- Pedidos
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Itens do Pedido
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL
);
\`\`\`

## 🔧 Configurações

### application.properties (Warehouse)
```properties
server.port=8081
spring.application.name=warehouse-service
spring.datasource.url=jdbc:h2:mem:warehouse
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
