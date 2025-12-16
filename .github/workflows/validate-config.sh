#!/bin/bash

###############################################################################
# Script de validation de la configuration SonarCloud
# Usage: ./validate-config.sh
###############################################################################

set -e

# Couleurs pour l'output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Compteurs
ERRORS=0
WARNINGS=0
SUCCESS=0

readonly STATUS_OK="OK"
readonly STATUS_WARNING="WARNING"
readonly STATUS_ERROR="ERROR"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Validation Configuration SonarCloud${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

###############################################################################
# Fonction de vérification
###############################################################################
check() {
    local name="$1"
    local result="$2"

    if [[ "$result" == "$STATUS_OK" ]]; then
        echo -e "${GREEN}✅ ${name}${NC}"
        ((SUCCESS++))
    elif [[ "$result" == "$STATUS_WARNING" ]]; then
        echo -e "${YELLOW}⚠️  ${name}${NC}"
        ((WARNINGS++))
    else
        echo -e "${RED}❌ ${name}${NC}"
        ((ERRORS++))
    fi

    return 0
}

###############################################################################
# 1. Vérification de la structure du projet
###############################################################################
echo -e "${BLUE}📁 Vérification de la structure du projet${NC}"
echo ""

# Backend services
if [[ -d "backend/user-service" ]]; then
    check "backend/user-service existe" "$STATUS_OK"
else
    check "backend/user-service existe" "$STATUS_ERROR"
fi

if [[ -d "backend/product-service" ]]; then
    check "backend/product-service existe" "$STATUS_OK"
else
    check "backend/product-service existe" "$STATUS_ERROR"
fi

if [[ -d "backend/media-service" ]]; then
    check "backend/media-service existe" "$STATUS_OK"
else
    check "backend/media-service existe" "$STATUS_ERROR"
fi

# Frontend
if [[ -d "frontend" ]]; then
    check "frontend existe" "$STATUS_OK"
else
    check "frontend existe" "$STATUS_ERROR"
fi

echo ""

###############################################################################
# 2. Vérification des fichiers de configuration
###############################################################################
echo -e "${BLUE}⚙️  Vérification des fichiers de configuration${NC}"
echo ""

# POM files
for service in user-service product-service media-service; do
    if [[ -f "backend/${service}/pom.xml" ]]; then
        # Vérifier présence de JaCoCo
        if grep -q "jacoco-maven-plugin" "backend/${service}/pom.xml"; then
            check "backend/${service}/pom.xml contient JaCoCo" "$STATUS_OK"
        else
            check "backend/${service}/pom.xml contient JaCoCo" "$STATUS_WARNING"
        fi
    else
        check "backend/${service}/pom.xml existe" "$STATUS_ERROR"
    fi
done

# Package.json
if [[ -f "frontend/package.json" ]]; then
    check "frontend/package.json existe" "$STATUS_OK"

    # Vérifier présence des scripts nécessaires
    if grep -q '"test"' "frontend/package.json"; then
        check "frontend/package.json contient script test" "$STATUS_OK"
    else
        check "frontend/package.json contient script test" "$STATUS_ERROR"
    fi

    if grep -q '"build"' "frontend/package.json"; then
        check "frontend/package.json contient script build" "$STATUS_OK"
    else
        check "frontend/package.json contient script build" "$STATUS_ERROR"
    fi
else
    check "frontend/package.json existe" "$STATUS_ERROR"
fi

echo ""

###############################################################################
# 3. Vérification des workflows GitHub Actions
###############################################################################
echo -e "${BLUE}🔄 Vérification des workflows GitHub Actions${NC}"
echo ""

if [[ -f ".github/workflows/sonarqube-backend.yml" ]]; then
    check "sonarqube-backend.yml existe" "$STATUS_OK"
else
    check "sonarqube-backend.yml existe" "$STATUS_ERROR"
fi

if [[ -f ".github/workflows/sonarqube-frontend.yml" ]]; then
    check "sonarqube-frontend.yml existe" "$STATUS_OK"
else
    check "sonarqube-frontend.yml existe" "$STATUS_ERROR"
fi

if [[ -f ".github/workflows/sonarqube-full.yml" ]]; then
    check "sonarqube-full.yml existe" "$STATUS_OK"
else
    check "sonarqube-full.yml existe" "$STATUS_ERROR"
fi

echo ""

###############################################################################
# 4. Vérification des Project Keys dans les workflows
###############################################################################
echo -e "${BLUE}🔑 Vérification des Project Keys${NC}"
echo ""

declare -A expected_keys=(
    ["user-service"]="ecommerce-user-service"
    ["product-service"]="ecommerce-product-service"
    ["media-service"]="ecommerce-media-service"
    ["frontend"]="ecommerce-frontend"
)

if [[ -f ".github/workflows/sonarqube-backend.yml" ]]; then
    for service in "${!expected_keys[@]}"; do
        if [[ "$service" != "frontend" ]]; then
            key="${expected_keys[$service]}"
            if grep -q "project-key: ${key}" ".github/workflows/sonarqube-backend.yml"; then
                check "Project Key ${key} configuré" "$STATUS_OK"
            else
                check "Project Key ${key} configuré" "$STATUS_ERROR"
            fi
        fi
    done
fi

if [[ -f ".github/workflows/sonarqube-frontend.yml" ]]; then
    if grep -q "projectKey=ecommerce-frontend" ".github/workflows/sonarqube-frontend.yml"; then
        check "Project Key ecommerce-frontend configuré" "$STATUS_OK"
    else
        check "Project Key ecommerce-frontend configuré" "$STATUS_ERROR"
    fi
fi

echo ""

###############################################################################
# 5. Vérification de l'organisation SonarCloud
###############################################################################
echo -e "${BLUE}🏢 Vérification de l'organisation SonarCloud${NC}"
echo ""

if grep -qr "zone01-ecommerce" .github/workflows/; then
    check "Organisation zone01-ecommerce configurée" "$STATUS_OK"
else
    check "Organisation zone01-ecommerce configurée" "$STATUS_ERROR"
fi

echo ""

###############################################################################
# 6. Vérification des secrets (simulation)
###############################################################################
echo -e "${BLUE}🔐 Vérification des secrets requis${NC}"
echo ""

echo -e "${YELLOW}⚠️  Les secrets suivants doivent être configurés dans GitHub:${NC}"
echo "   - SONAR_TOKEN (requis)"
echo "   - GITHUB_TOKEN (fourni automatiquement par GitHub)"
echo ""
echo -e "${BLUE}Pour vérifier:${NC}"
echo "   1. Aller sur GitHub → Settings → Secrets and variables → Actions"
echo "   2. Vérifier que SONAR_TOKEN existe"
echo ""

if command -v gh &> /dev/null; then
    echo -e "${BLUE}Tentative de vérification via GitHub CLI...${NC}"
    if gh secret list &> /dev/null; then
        if gh secret list | grep -q "SONAR_TOKEN"; then
            check "Secret SONAR_TOKEN existe (via gh cli)" "$STATUS_OK"
        else
            check "Secret SONAR_TOKEN existe (via gh cli)" "$STATUS_ERROR"
        fi
    else
        echo -e "${YELLOW}⚠️  Impossible de vérifier les secrets (authentification requise)${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  GitHub CLI (gh) non installé - impossible de vérifier automatiquement${NC}"
fi

echo ""

###############################################################################
# 7. Vérification des outils nécessaires
###############################################################################
echo -e "${BLUE}🛠️  Vérification des outils nécessaires${NC}"
echo ""

# Java
if command -v java &> /dev/null; then
    version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [[ "$version" == "17" || "$version" -ge 17 ]]; then
        check "Java 17+ installé (version: $version)" "$STATUS_OK"
    else
        check "Java 17+ installé (version trouvée: $version)" "$STATUS_WARNING"
    fi
else
    check "Java installé" "$STATUS_WARNING"
fi

# Maven
if command -v mvn &> /dev/null; then
    mvn_version=$(mvn -version | head -n 1 | awk '{print $3}')
    check "Maven installé (version: $mvn_version)" "$STATUS_OK"
else
    check "Maven installé" "$STATUS_WARNING"
fi

# Node.js
if command -v node &> /dev/null; then
    node_version=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
    if [[ "$node_version" -ge 20 ]]; then
        check "Node.js 20+ installé (version: $(node -v))" "$STATUS_OK"
    else
        check "Node.js 20+ installé (version trouvée: $(node -v))" "$STATUS_WARNING"
    fi
else
    check "Node.js installé" "$STATUS_WARNING"
fi

# npm
if command -v npm &> /dev/null; then
    npm_version=$(npm -v)
    check "npm installé (version: $npm_version)" "$STATUS_OK"
else
    check "npm installé" "$STATUS_WARNING"
fi

echo ""

###############################################################################
# 8. Tests de build (optionnel)
###############################################################################
echo -e "${BLUE}🔨 Tests de build (optionnel)${NC}"
echo ""

read -p "Voulez-vous tester les builds localement? (y/N) " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${BLUE}Testing backend builds...${NC}"
    for service in user-service product-service media-service; do
        echo -e "${YELLOW}Testing backend/${service}...${NC}"
        if cd "backend/${service}" && mvn clean compile -q && cd ../..; then
            check "backend/${service} compile" "$STATUS_OK"
        else
            check "backend/${service} compile" "$STATUS_ERROR"
        fi
    done

    echo ""
    echo -e "${BLUE}Testing frontend build...${NC}"
    if cd frontend && npm install --silent && npm run build && cd ..; then
        check "frontend build" "$STATUS_OK"
    else
        check "frontend build" "$STATUS_ERROR"
    fi
else
    echo -e "${YELLOW}⚠️  Tests de build ignorés${NC}"
fi

echo ""

###############################################################################
# 9. Résumé
###############################################################################
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Résumé de la validation${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e "${GREEN}✅ Succès:    ${SUCCESS}${NC}"
echo -e "${YELLOW}⚠️  Avertissements: ${WARNINGS}${NC}"
echo -e "${RED}❌ Erreurs:   ${ERRORS}${NC}"

echo ""

if [[ $ERRORS -eq 0 && $WARNINGS -eq 0 ]]; then
    echo -e "${GREEN}🎉 Configuration parfaite! Tous les checks sont passés.${NC}"
    exit 0
elif [[ $ERRORS -eq 0 ]]; then
    echo -e "${YELLOW}⚠️  Configuration OK avec quelques avertissements.${NC}"
    echo -e "${YELLOW}   Les avertissements n'empêchent pas le fonctionnement.${NC}"
    exit 0
else
    echo -e "${RED}❌ Configuration incomplète. Veuillez corriger les erreurs.${NC}"
    exit 1
fi
