# Tests locaux avec SonarCloud

Ce guide explique comment tester l'analyse SonarCloud en local avant de pusher vos changements.

---

## Prérequis

### Configuration

1. **Token SonarCloud**
   ```bash
   # Définir le token comme variable d'environnement
   export SONAR_TOKEN="votre-token-sonarcloud"
   ```

2. **Outils installés**
   - Java 17+
   - Maven 3.6+
   - Node.js 20+
   - npm 9+

---

## Backend - Tests locaux

### User Service

#### 1. Build et tests
```bash
cd backend/user-service

# Clean build avec tests
mvn clean test

# Vérifier la couverture
mvn jacoco:report
```

#### 2. Voir les rapports locaux
```bash
# Rapport HTML JaCoCo
xdg-open target/site/jacoco/index.html

# Rapport XML pour SonarCloud
cat target/site/jacoco/jacoco.xml
```

#### 3. Analyse SonarCloud locale
```bash
mvn sonar:sonar \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-user-service \
  -Dsonar.projectName="E-Commerce User Service" \
  -Dsonar.token=$SONAR_TOKEN \
  -Dsonar.java.binaries=target/classes \
  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
```

#### 4. Voir les résultats
```bash
# Ouvrir SonarCloud
xdg-open https://sonarcloud.io/project/overview?id=ecommerce-user-service
```

### Product Service

```bash
cd backend/product-service

# Build et test
mvn clean verify

# Analyse SonarCloud
mvn sonar:sonar \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-product-service \
  -Dsonar.token=$SONAR_TOKEN

# Voir résultats
xdg-open https://sonarcloud.io/project/overview?id=ecommerce-product-service
```

### Media Service

```bash
cd backend/media-service

# Build et test
mvn clean verify

# Analyse SonarCloud
mvn sonar:sonar \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-media-service \
  -Dsonar.token=$SONAR_TOKEN

# Voir résultats
xdg-open https://sonarcloud.io/project/overview?id=ecommerce-media-service
```

### Tous les services backend en une commande

```bash
#!/bin/bash
# Script: test-all-backend.sh

for service in user-service product-service media-service; do
    echo "================================"
    echo "Testing backend/${service}..."
    echo "================================"

    cd backend/${service}

    # Build et test
    mvn clean verify -B

    # Analyse SonarCloud
    mvn sonar:sonar -B \
      -Dsonar.host.url=https://sonarcloud.io \
      -Dsonar.organization=zone01-ecommerce \
      -Dsonar.projectKey=ecommerce-${service} \
      -Dsonar.token=$SONAR_TOKEN

    cd ../..

    echo ""
done

echo "✅ Tous les services backend analysés!"
```

---

## Frontend - Tests locaux

### 1. Installation des dépendances
```bash
cd frontend

# Installation propre
rm -rf node_modules package-lock.json
npm install
```

### 2. Linting
```bash
# Si ESLint configuré
npm run lint

# Fix automatique
npm run lint -- --fix
```

### 3. Tests avec couverture
```bash
# Tests avec ChromeHeadless (comme CI)
npm test -- \
  --no-watch \
  --no-progress \
  --browsers=ChromeHeadless \
  --code-coverage

# Voir le rapport de couverture
xdg-open coverage/frontend/index.html
```

### 4. Build de production
```bash
# Build comme en CI
npm run build -- --configuration production

# Vérifier la taille du build
du -sh dist/
```

### 5. Analyse SonarCloud

#### Option 1: Avec sonar-scanner (recommandé)

```bash
# Installer sonar-scanner si pas déjà fait
npm install -g sonar-scanner

# Lancer l'analyse
sonar-scanner \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-frontend \
  -Dsonar.projectName="E-Commerce Frontend" \
  -Dsonar.sources=src \
  -Dsonar.tests=src \
  -Dsonar.test.inclusions=**/*.spec.ts \
  -Dsonar.exclusions=**/*.spec.ts,**/node_modules/**,**/dist/**,**/coverage/** \
  -Dsonar.javascript.lcov.reportPaths=coverage/frontend/lcov.info \
  -Dsonar.token=$SONAR_TOKEN
```

#### Option 2: Avec Docker (si sonar-scanner non installé)

```bash
docker run \
  --rm \
  -e SONAR_TOKEN=$SONAR_TOKEN \
  -v "$(pwd):/usr/src" \
  sonarsource/sonar-scanner-cli \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-frontend \
  -Dsonar.sources=src \
  -Dsonar.javascript.lcov.reportPaths=coverage/frontend/lcov.info
```

### 6. Voir les résultats
```bash
xdg-open https://sonarcloud.io/project/overview?id=ecommerce-frontend
```

---

## Script de test complet

### Créer le script

```bash
#!/bin/bash
# Script: test-full-project.sh
# Description: Teste tous les composants localement avant push

set -e

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}Tests locaux - Projet E-Commerce${NC}"
echo -e "${BLUE}================================${NC}"
echo ""

# Vérifier le token
if [ -z "$SONAR_TOKEN" ]; then
    echo -e "${RED}❌ SONAR_TOKEN non défini${NC}"
    echo "Export le token: export SONAR_TOKEN=your-token"
    exit 1
fi

# Backend
echo -e "${BLUE}📦 Backend Services${NC}"
for service in user-service product-service media-service; do
    echo -e "${YELLOW}Testing ${service}...${NC}"
    cd backend/${service}

    # Build et test
    mvn clean verify -B -q

    # SonarCloud
    mvn sonar:sonar -B -q \
      -Dsonar.host.url=https://sonarcloud.io \
      -Dsonar.organization=zone01-ecommerce \
      -Dsonar.projectKey=ecommerce-${service} \
      -Dsonar.token=$SONAR_TOKEN

    echo -e "${GREEN}✅ ${service} OK${NC}"
    cd ../..
done

echo ""

# Frontend
echo -e "${BLUE}🎨 Frontend${NC}"
cd frontend

echo -e "${YELLOW}Installing dependencies...${NC}"
npm ci --silent

echo -e "${YELLOW}Running tests...${NC}"
npm test -- --no-watch --browsers=ChromeHeadless --code-coverage

echo -e "${YELLOW}Building...${NC}"
npm run build -- --configuration production

echo -e "${YELLOW}SonarCloud analysis...${NC}"
sonar-scanner \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-frontend \
  -Dsonar.sources=src \
  -Dsonar.javascript.lcov.reportPaths=coverage/frontend/lcov.info \
  -Dsonar.token=$SONAR_TOKEN

echo -e "${GREEN}✅ Frontend OK${NC}"
cd ..

echo ""
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}✅ Tous les tests sont passés!${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo "Voir les résultats:"
echo "- User Service: https://sonarcloud.io/project/overview?id=ecommerce-user-service"
echo "- Product Service: https://sonarcloud.io/project/overview?id=ecommerce-product-service"
echo "- Media Service: https://sonarcloud.io/project/overview?id=ecommerce-media-service"
echo "- Frontend: https://sonarcloud.io/project/overview?id=ecommerce-frontend"
```

### Utiliser le script

```bash
# Rendre exécutable
chmod +x test-full-project.sh

# Définir le token
export SONAR_TOKEN="votre-token"

# Lancer
./test-full-project.sh
```

---

## Tests spécifiques

### Tester uniquement la couverture

#### Backend
```bash
cd backend/user-service
mvn clean test jacoco:report
xdg-open target/site/jacoco/index.html
```

#### Frontend
```bash
cd frontend
npm test -- --no-watch --code-coverage
xdg-open coverage/frontend/index.html
```

### Tester uniquement les Quality Gates

```bash
# Backend
cd backend/user-service
mvn sonar:sonar \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-user-service \
  -Dsonar.token=$SONAR_TOKEN \
  -Dsonar.qualitygate.wait=true

# Frontend
cd frontend
sonar-scanner \
  -Dsonar.qualitygate.wait=true \
  -Dsonar.token=$SONAR_TOKEN
```

### Tester uniquement le build

#### Backend
```bash
# Build rapide sans tests
cd backend/user-service
mvn clean compile -DskipTests

# Build complet avec tests
mvn clean verify
```

#### Frontend
```bash
# Build development
cd frontend
npm run build

# Build production
npm run build -- --configuration production
```

---

## Reproduire les conditions CI

### Backend (comme GitHub Actions)

```bash
cd backend/user-service

# Exactement comme CI
mvn clean verify -B \
  -DskipTests=false \
  -Dmaven.test.failure.ignore=false

# Analyse SonarCloud
mvn sonar:sonar -B \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-user-service \
  -Dsonar.java.binaries=target/classes \
  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
  -Dsonar.token=$SONAR_TOKEN
```

### Frontend (comme GitHub Actions)

```bash
cd frontend

# Installation comme CI
npm ci --prefer-offline --no-audit

# Tests comme CI
npm test -- \
  --no-watch \
  --no-progress \
  --browsers=ChromeHeadless \
  --code-coverage

# Build comme CI
npm run build -- --configuration production

# Analyse comme CI
sonar-scanner \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-frontend \
  -Dsonar.sources=src \
  -Dsonar.tests=src \
  -Dsonar.test.inclusions=**/*.spec.ts \
  -Dsonar.exclusions=**/*.spec.ts,**/node_modules/**,**/dist/** \
  -Dsonar.javascript.lcov.reportPaths=coverage/frontend/lcov.info \
  -Dsonar.token=$SONAR_TOKEN
```

---

## Commandes utiles

### Voir les métriques localement

#### Backend
```bash
# Générer les rapports
cd backend/user-service
mvn clean verify site

# Voir les rapports
xdg-open target/site/index.html
```

#### Frontend
```bash
# Générer les rapports
cd frontend
npm run test -- --no-watch --code-coverage

# Voir les rapports
xdg-open coverage/frontend/index.html
```

### Nettoyer les builds

```bash
# Backend - tous les services
for service in user-service product-service media-service; do
    cd backend/${service}
    mvn clean
    cd ../..
done

# Frontend
cd frontend
rm -rf dist/ coverage/ node_modules/
```

### Vérifier les versions

```bash
# Java
java -version

# Maven
mvn -version

# Node.js
node -v

# npm
npm -v

# sonar-scanner
sonar-scanner --version
```

---

## Dépannage

### Erreur: "Failed to execute goal org.sonarsource.scanner.maven:sonar-maven-plugin"

**Solution:**
```bash
# Vérifier le token
echo $SONAR_TOKEN

# Vérifier la connexion à SonarCloud
curl -u $SONAR_TOKEN: https://sonarcloud.io/api/authentication/validate
```

### Erreur: "Coverage report not found"

**Backend:**
```bash
# Générer le rapport JaCoCo
mvn jacoco:report

# Vérifier qu'il existe
ls -la target/site/jacoco/jacoco.xml
```

**Frontend:**
```bash
# Générer le rapport de couverture
npm test -- --no-watch --code-coverage

# Vérifier qu'il existe
ls -la coverage/frontend/lcov.info
```

### Tests échouent avec ChromeHeadless

```bash
# Installer Chrome si manquant
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install google-chrome-stable

# Vérifier l'installation
which google-chrome-stable
```

### Maven télécharge toutes les dépendances

```bash
# Utiliser le cache local
mvn clean verify -o  # Offline mode

# Forcer la mise à jour du cache
mvn clean install -U
```

---

## Résumé des commandes rapides

### Test rapide backend
```bash
cd backend/user-service && mvn clean test
```

### Test rapide frontend
```bash
cd frontend && npm test -- --no-watch
```

### Analyse rapide backend
```bash
cd backend/user-service && mvn clean verify sonar:sonar -Dsonar.token=$SONAR_TOKEN
```

### Analyse rapide frontend
```bash
cd frontend && npm test -- --no-watch --code-coverage && sonar-scanner -Dsonar.token=$SONAR_TOKEN
```

---

## Liens utiles

- [SonarCloud](https://sonarcloud.io/organizations/zone01-ecommerce)
- [Maven SonarCloud Plugin](https://docs.sonarcloud.io/advanced-setup/ci-based-analysis/sonarscanner-for-maven/)
- [SonarScanner for JavaScript](https://docs.sonarcloud.io/advanced-setup/languages/javascript/)

---

**Dernière mise à jour:** 2025-12-15
**Version:** 1.0