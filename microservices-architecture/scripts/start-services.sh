#!/bin/bash

echo "🚀 Iniciando Arquitetura de Microsserviços - DIO Challenge"
echo "=================================================="

# Verificar se Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Por favor, inicie o Docker primeiro."
    exit 1
fi

# Iniciar RabbitMQ
echo "📡 Iniciando RabbitMQ..."
docker-compose up -d

# Aguardar RabbitMQ estar pronto
echo "⏳ Aguardando RabbitMQ inicializar..."
sleep 10

# Verificar se RabbitMQ está rodando
if curl -f http://localhost:15672 > /dev/null 2>&1; then
    echo "✅ RabbitMQ está rodando em http://localhost:15672"
else
    echo "❌ Erro ao iniciar RabbitMQ"
    exit 1
fi

# Compilar e iniciar Warehouse Service
echo "🏭 Iniciando Warehouse Service..."
cd warehouse-service
mvn clean compile > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Warehouse Service compilado com sucesso"
    mvn spring-boot:run > ../logs/warehouse.log 2>&1 &
    WAREHOUSE_PID=$!
    echo "🏭 Warehouse Service iniciado (PID: $WAREHOUSE_PID)"
else
    echo "❌ Erro ao compilar Warehouse Service"
    exit 1
fi

cd ..

# Aguardar Warehouse Service estar pronto
echo "⏳ Aguardando Warehouse Service inicializar..."
sleep 15

# Verificar se Warehouse está rodando
if curl -f http://localhost:8081/api/products > /dev/null 2>&1; then
    echo "✅ Warehouse Service está rodando em http://localhost:8081"
else
    echo "❌ Erro ao iniciar Warehouse Service"
    kill $WAREHOUSE_PID 2>/dev/null
    exit 1
fi

# Compilar e iniciar Storefront Service
echo "🏪 Iniciando Storefront Service..."
cd storefront-service
mvn clean compile > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Storefront Service compilado com sucesso"
    mvn spring-boot:run > ../logs/storefront.log 2>&1 &
    STOREFRONT_PID=$!
    echo "🏪 Storefront Service iniciado (PID: $STOREFRONT_PID)"
else
    echo "❌ Erro ao compilar Storefront Service"
    kill $WAREHOUSE_PID 2>/dev/null
    exit 1
fi

cd ..

# Aguardar Storefront Service estar pronto
echo "⏳ Aguardando Storefront Service inicializar..."
sleep 15

# Verificar se Storefront está rodando
if curl -f http://localhost:8080/api/storefront/products > /dev/null 2>&1; then
    echo "✅ Storefront Service está rodando em http://localhost:8080"
else
    echo "❌ Erro ao iniciar Storefront Service"
    kill $WAREHOUSE_PID $STOREFRONT_PID 2>/dev/null
    exit 1
fi

# Criar diretório de logs se não existir
mkdir -p logs

# Salvar PIDs para facilitar o stop
echo $WAREHOUSE_PID > logs/warehouse.pid
echo $STOREFRONT_PID > logs/storefront.pid

echo ""
echo "🎉 Todos os serviços estão rodando com sucesso!"
echo "=================================================="
echo "📊 URLs dos Serviços:"
echo "   • Warehouse API: http://localhost:8081/api/products"
echo "   • Storefront API: http://localhost:8080/api/orders"
echo "   • RabbitMQ Management: http://localhost:15672 (guest/guest)"
echo "   • H2 Console Warehouse: http://localhost:8081/h2-console"
echo "   • H2 Console Storefront: http://localhost:8080/h2-console"
echo ""
echo "📝 Logs dos Serviços:"
echo "   • Warehouse: tail -f logs/warehouse.log"
echo "   • Storefront: tail -f logs/storefront.log"
echo ""
echo "🛑 Para parar os serviços: ./scripts/stop-services.sh"
echo "=================================================="
