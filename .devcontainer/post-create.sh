#!/bin/bash

echo "🚀 Instalando dependencias..."

echo "Otorgando permisos de ejecución a Maven Wrapper..."
chmod +x /workspace/backend/mvnw /workspace/backend/mvnw.cmd

echo "Iniciando servicio MongoDB..."
cd /workspace/.devcontainer && docker-compose up -d mongo
echo "Esperando a que MongoDB esté listo..."
sleep 10

echo "Instalando dependencias del backend..."
cd /workspace/backend && ./mvnw install -DskipTests

echo "Instalando dependencias del frontend..."
cd /workspace/frontend && npm install

if [ -f /workspace/.env ]; then
  echo "Cargando variables de entorno..."
  cd /workspace && set -a && source .env && set +a || { echo "❌ Error al cargar variables de entorno"; }
  echo "Rellenando base de datos con datos de prueba..."
  chmod +x /workspace/seed-db.sh
  cd /workspace && timeout 300 ./seed-db.sh || true
else
  echo "⚠️ Archivo .env no encontrado. No se cargarán variables de entorno ni se rellenará la base de datos con datos de prueba."
  echo "Para poder iniciar el proyecto, crea un archivo .env en la raíz del proyecto con las variables de entorno necesarias. Luego, ejecuta set -a && source .env && set +a para cargar las variables en el entorno actual."
fi

echo ""
echo "Herramientas de Java listas:"
java -version
mvn -version

echo "Herramientas de Node listas:"
node -v
npm -v

echo "✅ Entorno de desarrollo preparado!"
echo ""
echo "📝 Comandos disponibles:"
echo "  Backend:  cd backend && ./mvnw spring-boot:run"
echo "  Frontend: cd frontend && npm run dev"
