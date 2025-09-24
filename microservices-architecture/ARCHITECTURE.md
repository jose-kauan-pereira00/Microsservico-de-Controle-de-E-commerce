# Documentação da Arquitetura

## Visão Geral

Este documento detalha a arquitetura técnica do sistema de microsserviços, incluindo decisões de design, padrões utilizados e justificativas técnicas.

## Decisões Arquiteturais

### 1. Separação de Responsabilidades

#### Warehouse Service
- **Responsabilidade**: Gerenciamento de produtos e controle de estoque
- **Justificativa**: Centralizar a lógica de inventário em um único serviço garante consistência e facilita a manutenção
- **Domínio**: Produtos, Estoque, Alertas de Estoque Baixo

#### Storefront Service  
- **Responsabilidade**: Interface de vendas e gerenciamento de pedidos
- **Justificativa**: Separar a lógica de vendas permite escalabilidade independente e diferentes estratégias de negócio
- **Domínio**: Pedidos, Clientes, Catálogo (proxy)

### 2. Padrões de Comunicação

#### Comunicação Síncrona (HTTP/REST)
- **Quando usar**: Operações que requerem resposta imediata
- **Exemplos**: 
  - Consulta de produtos
  - Verificação de estoque
  - Validação de dados

#### Comunicação Assíncrona (RabbitMQ)
- **Quando usar**: Eventos que não requerem resposta imediata
- **Exemplos**:
  - Notificações de estoque baixo
  - Eventos de pedidos criados
  - Atualizações de status

### 3. Padrões de Design Implementados

#### Repository Pattern
\`\`\`java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findAvailableProducts();
}
\`\`\`

#### Service Layer Pattern
\`\`\`java
@Service
@Transactional
public class ProductService {
    // Lógica de negócio isolada
}
\`\`\`

#### DTO Pattern
\`\`\`java
public class ProductDTO {
    // Transferência de dados entre camadas
}
\`\`\`

#### Message Publisher/Consumer Pattern
\`\`\`java
@Service
public class MessagePublisher {
    public void publishStockUpdate(StockUpdateMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
\`\`\`

## Fluxos de Dados

### Fluxo de Criação de Pedido

\`\`\`mermaid
sequenceDiagram
    participant C as Cliente
    participant S as Storefront
    participant W as Warehouse
    participant R as RabbitMQ
    
    C->>S: POST /api/orders
    S->>W: GET /api/products/{id}
    W>S: Product details
    S->>W: GET /api/products/{id}/stock-check
    W>S: Stock availability
    S->>W: PUT /api/products/stock (subtract)
    W>S: Updated stock
    W->>R: Publish stock update event
    S->>S: Save order
    S->>R: Publish order created event
    S>C: Order confirmation
\`\`\`

### Fluxo de Atualização de Estoque

\`\`\`mermaid
sequenceDiagram
    participant A as Admin
    participant W as Warehouse
    participant R as RabbitMQ
    participant S as Storefront
    
    A->>W: PUT /api/products/stock
    W->>W: Update stock quantity
    W->>R: Publish stock update event
    W->>R: Publish low stock alert (if needed)
    R->>S: Consume stock update
    R->>S: Consume low stock alert
    S->>S: Update local cache/notifications
\`\`\`

## Configurações de Mensageria

### Exchanges e Filas

\`\`\`java
// Warehouse Exchange
public static final String WAREHOUSE_EXCHANGE = "warehouse.exchange";
public static final String STOCK_UPDATE_QUEUE = "stock.update.queue";
public static final String LOW_STOCK_ALERT_QUEUE = "low.stock.alert.queue";

// Order Exchange  
public static final String ORDER_EXCHANGE = "order.exchange";
public static final String ORDER_CREATED_QUEUE = "order.created.queue";
public static final String ORDER_STATUS_UPDATE_QUEUE = "order.status.update.queue";
\`\`\`

### Routing Keys
- `stock.update` - Atualizações de estoque
- `stock.low` - Alertas de estoque baixo
- `order.created` - Pedidos criados
- `order.status.update` - Mudanças de status

## Tratamento de Erros

### Estratégias Implementadas

1. **Validação de Entrada**
   \`\`\`java
   @Valid @RequestBody ProductDTO productDTO
   \`\`\`

2. **Tratamento de Exceções de Negócio**
   \`\`\`java
   if (!stockAvailable) {
       throw new RuntimeException("Insufficient stock");
   }
   \`\`\`

3. **Rollback de Transações**
   \`\`\`java
   @Transactional
   public Order createOrder(OrderDTO orderDTO) {
       // Se falhar, rollback automático
   }
   \`\`\`

## Considerações de Performance

### Otimizações Implementadas

1. **Lazy Loading**: Relacionamentos JPA configurados como LAZY
2. **Connection Pooling**: Pool de conexões do HikariCP
3. **Message Batching**: RabbitMQ configurado para batch processing
4. **Caching**: Potencial para cache de produtos frequentemente acessados

### Métricas Importantes

- **Latência**: Tempo de resposta das APIs
- **Throughput**: Número de pedidos processados por segundo  
- **Disponibilidade**: Uptime dos serviços
- **Utilização de Recursos**: CPU, Memória, Conexões DB

## Segurança

### Implementações Atuais
- Validação de entrada com Bean Validation
- CORS configurado para desenvolvimento
- Transações para consistência de dados

### Melhorias Futuras
- Autenticação JWT
- Autorização baseada em roles
- Rate limiting
- Criptografia de dados sensíveis

## Monitoramento e Observabilidade

### Logs Estruturados
\`\`\`java
logging.level.com.dio.challenge=DEBUG
\`\`\`

### Health Checks
- Spring Boot Actuator endpoints
- Database connectivity checks
- RabbitMQ connectivity checks

### Métricas Customizadas
- Número de produtos criados
- Pedidos por status
- Alertas de estoque baixo

## Escalabilidade

### Estratégias de Escala

1. **Horizontal Scaling**
   - Múltiplas instâncias de cada serviço
   - Load balancer (nginx/HAProxy)

2. **Database Scaling**
   - Read replicas para consultas
   - Sharding por categoria de produto

3. **Message Queue Scaling**
   - Cluster RabbitMQ
   - Particionamento de filas

### Pontos de Atenção
- Estado compartilhado (database)
- Sincronização entre instâncias
- Configuração de sessões

## Testes

### Estratégia de Testes

1. **Testes Unitários**
   - Services e Repositories
   - Validações de negócio

2. **Testes de Integração**
   - APIs REST
   - Mensageria RabbitMQ
   - Persistência de dados

3. **Testes End-to-End**
   - Fluxos completos de negócio
   - Comunicação entre serviços

## Deployment

### Containerização
\`\`\`dockerfile
FROM openjdk:17-jdk-slim
COPY target/warehouse-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
\`\`\`

### Orquestração
- Docker Compose para desenvolvimento
- Kubernetes para produção
- Helm charts para configuração

## Conclusão

A arquitetura implementada segue as melhores práticas de microsserviços, proporcionando:

- **Baixo Acoplamento**: Serviços independentes
- **Alta Coesão**: Responsabilidades bem definidas  
- **Escalabilidade**: Capacidade de crescer horizontalmente
- **Resiliência**: Tolerância a falhas
- **Manutenibilidade**: Código organizado e documentado

Esta base sólida permite evoluções futuras e adaptações às necessidades do negócio.
