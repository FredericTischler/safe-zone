#!/bin/bash

# Script pour lancer tous les tests SonarQube
# E-Commerce Safe Zone Project

SONAR_TOKEN="sqa_960d50ef3186bd23ec19235a75ed359b696ac324"
PROJECT_ROOT=$(pwd)

echo "=========================================="
echo "  Lancement des tests SonarQube"
echo "=========================================="
echo ""

# Fonction pour afficher les messages
print_section() {
    echo ""
    echo "=========================================="
    echo "  $1"
    echo "=========================================="
    echo ""
}

# Fonction pour gérer les erreurs
handle_error() {
    echo "❌ Erreur lors de l'exécution de : $1"
    echo "Voulez-vous continuer avec les autres services ? (y/n)"
    read -r response
    if [[ "$response" != "y" ]]; then
        exit 1
    fi
}

# Test 1: User Service
print_section "1/4 - Test User Service"
cd "$PROJECT_ROOT/backend/user-service" || handle_error "cd user-service"
mvn clean verify sonar:sonar -Dsonar.token="$SONAR_TOKEN" || handle_error "User Service"
cd "$PROJECT_ROOT"

# Test 2: Product Service
print_section "2/4 - Test Product Service"
cd "$PROJECT_ROOT/backend/product-service" || handle_error "cd product-service"
mvn clean verify sonar:sonar -Dsonar.token="$SONAR_TOKEN" || handle_error "Product Service"
cd "$PROJECT_ROOT"

# Test 3: Media Service
print_section "3/4 - Test Media Service"
cd "$PROJECT_ROOT/backend/media-service" || handle_error "cd media-service"
mvn clean verify sonar:sonar -Dsonar.token="$SONAR_TOKEN" || handle_error "Media Service"
cd "$PROJECT_ROOT"

# Test 4: Frontend
print_section "4/4 - Test Frontend (Angular)"
cd "$PROJECT_ROOT/frontend" || handle_error "cd frontend"
npm test -- --watch=false --code-coverage || handle_error "Frontend tests"
npx sonar-scanner -Dsonar.token="$SONAR_TOKEN" || handle_error "Frontend SonarQube"
cd "$PROJECT_ROOT"

# Résumé
print_section "Terminé !"
echo "✅ Tous les tests ont été exécutés"
echo ""
echo "📊 Dashboards SonarQube disponibles sur :"
echo "   - User Service:    http://localhost:9000/dashboard?id=ecommerce-user-service"
echo "   - Product Service: http://localhost:9000/dashboard?id=ecommerce-product-service"
echo "   - Media Service:   http://localhost:9000/dashboard?id=ecommerce-media-service"
echo "   - Frontend:        http://localhost:9000/dashboard?id=ecommerce-frontend"
echo ""