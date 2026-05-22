#!/bin/bash
# Script para ejecutar el Database Seeder

echo "🌱 Ejecutando Database Seeder..."
echo "================================"

cd "$(dirname "$0")/backend"

source ../.env

# Ejecutar con el profile 'seed' con timeout
timeout 180 ./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="--spring.profiles.active=dev,seed" \
  -DskipTests 2>&1 | while IFS= read -r line; do
  echo "$line"
  if echo "$line" | grep -q "seeding completado"; then
    pkill -f "spring-boot:run"
    break
  fi
done

# Asegurar que no quede ningún proceso residual
pkill -f "spring-boot:run" 2>/dev/null || true

echo ""
echo "✅ Seeder completado. La base de datos ha sido recreada con datos iniciales."
