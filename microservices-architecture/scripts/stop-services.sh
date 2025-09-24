#!/bin/bash

echo "🛑 Parando Arquitetura de Microsserviços - DIO Challenge"
echo "=================================================="

# Parar Warehouse Service
if [ -f logs/warehouse.pid ]; then
    WAREHOUSE_PID=$(cat logs/warehouse.pid)
    if kill -0 $WAREHOUSE_PID 2>/dev/null; then
        echo "🏭 Parando Warehouse Service (PID: $WAREHOUSE_PID)..."
        kill $WAREHOUSE_PID
        echo "✅ Warehouse Service parado"
    else
        echo "⚠️  Warehouse Service já estava parado"
    fi
    rm -f logs/warehouse.pid
fi

# Parar Storefront Service
if [ -f logs/storefront.pid ]; then
    STOREFRONT_PID=$(cat logs/storefront.pid)
    if kill -0 $STOREFRONT_PID 2>/dev/null; then
        echo "🏪 Parando Storefront Service (PID: $STOREFRONT_PID)..."
        kill $STOREFRONT_PID
        echo "✅ Storefront Service parado"
    else
        echo "⚠️  Storefront Service já estava parado"
    fi
    rm -f logs/storefront.pid
fi

# Parar RabbitMQ
echo "📡 Parando RabbitMQ..."
docker-compose down
echo "✅ RabbitMQ parado"

# Limpar logs antigos (opcional)
if [ "$1" = "--clean-logs" ]; then
    echo "🧹 Limpando logs..."
    rm -rf logs/*.log
    echo "✅ Logs limpos"
fi

echo ""
echo "🎉 Todos os serviços foram parados com sucesso!"
echo "=================================================="
