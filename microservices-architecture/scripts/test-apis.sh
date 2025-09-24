#!/bin/bash

echo "🧪 Testando APIs - Arquitetura de Microsserviços"
echo "=================================================="

BASE_WAREHOUSE="http://localhost:8081"
BASE_STOREFRONT="http://localhost:8080"

# Função para testar endpoint
test_endpoint() {
    local method=$1
    local url=$2
    local data=$3
    local description=$4
    
    echo "🔍 Testando: $description"
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method -H "Content-Type: application/json" -d "$data" "$url")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$url")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo "✅ Sucesso ($http_code)"
        if [ ${#body} -gt 100 ]; then
            echo "   Resposta: $(echo "$body" | head -c 100)..."
        else
            echo "   Resposta: $body"
        fi
    else
        echo "❌ Erro ($http_code)"
        echo "   Resposta: $body"
    fi
    echo ""
}

# Testar Warehouse Service
echo "🏭 TESTANDO WAREHOUSE SERVICE"
echo "----------------------------------------"

test_endpoint "GET" "$BASE_WAREHOUSE/api/products" "" "Listar todos os produtos"

test_endpoint "GET" "$BASE_WAREHOUSE/api/products/available" "" "Listar produtos disponíveis"

test_endpoint "POST" "$BASE_WAREHOUSE/api/products" '{
    "name": "Produto Teste API",
    "description": "Produto criado via teste automatizado",
    "price": 99.99,
    "stockQuantity": 100
}' "Criar novo produto"

test_endpoint "GET" "$BASE_WAREHOUSE/api/products/search?name=Smartphone" "" "Buscar produtos por nome"

test_endpoint "GET" "$BASE_WAREHOUSE/api/products/1/stock-check?quantity=5" "" "Verificar disponibilidade de estoque"

# Testar Storefront Service
echo "🏪 TESTANDO STOREFRONT SERVICE"
echo "----------------------------------------"

test_endpoint "GET" "$BASE_STOREFRONT/api/storefront/products" "" "Catálogo de produtos (via Storefront)"

test_endpoint "GET" "$BASE_STOREFRONT/api/storefront/products/available" "" "Produtos disponíveis (via Storefront)"

test_endpoint "POST" "$BASE_STOREFRONT/api/orders" '{
    "customerName": "Cliente Teste",
    "customerEmail": "teste@email.com",
    "items": [
        {
            "productId": 1,
            "quantity": 2
        }
    ]
}' "Criar novo pedido"

test_endpoint "GET" "$BASE_STOREFRONT/api/orders" "" "Listar todos os pedidos"

test_endpoint "GET" "$BASE_STOREFRONT/api/orders/status/PENDING" "" "Listar pedidos pendentes"

# Testar atualização de estoque
echo "📦 TESTANDO ATUALIZAÇÃO DE ESTOQUE"
echo "----------------------------------------"

test_endpoint "PUT" "$BASE_WAREHOUSE/api/products/stock" '{
    "productId": 1,
    "quantity": 5,
    "operation": "SUBTRACT"
}' "Reduzir estoque (simular venda)"

test_endpoint "PUT" "$BASE_WAREHOUSE/api/products/stock" '{
    "productId": 1,
    "quantity": 10,
    "operation": "ADD"
}' "Aumentar estoque (simular reposição)"

echo "🎉 Testes concluídos!"
echo "=================================================="
echo "💡 Dicas:"
echo "   • Verifique os logs para ver as mensagens RabbitMQ"
echo "   • Acesse http://localhost:15672 para ver as filas"
echo "   • Use Postman ou Insomnia para testes mais detalhados"
echo "=================================================="
