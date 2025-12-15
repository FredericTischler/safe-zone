# Guide d'Installation et Configuration SonarQube

> **Note sur Docker Compose** : Ce guide utilise la commande `docker compose` (v2, intégré à Docker). Si vous utilisez l'ancienne version standalone, remplacez `docker compose` par `docker-compose` dans toutes les commandes. Les scripts fournis détectent automatiquement la version disponible.

## Table des matières
1. [Architecture](#architecture)
2. [Prérequis](#prérequis)
3. [Installation](#installation)
4. [Accès à SonarQube](#accès-à-sonarqube)
5. [Configuration des projets](#configuration-des-projets)
6. [Analyse du code](#analyse-du-code)
7. [Troubleshooting](#troubleshooting)

---

## Architecture

### Vue d'ensemble

```
┌─────────────────────────────────────────────────────────────┐
│                  Infrastructure E-Commerce                   │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  User        │  │  Product     │  │  Media       │      │
│  │  Service     │  │  Service     │  │  Service     │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                 │                │
│         └─────────────────┴─────────────────┘                │
│                          │                                   │
│              ┌───────────┴───────────┐                       │
│              │                       │                       │
│         ┌────▼─────┐           ┌────▼─────┐                │
│         │ MongoDB  │           │  Kafka   │                │
│         │ (Projet) │           │          │                │
│         └──────────┘           └──────────┘                │
│                                                               │
├─────────────────────────────────────────────────────────────┤
│              Infrastructure SonarQube (Séparée)              │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│         ┌──────────────┐           ┌──────────────┐         │
│         │  SonarQube   │◄──────────┤ PostgreSQL   │         │
│         │  :9000       │           │ (SonarQube)  │         │
│         └──────────────┘           └──────────────┘         │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

### Points importants

- **MongoDB** : Base de données du projet e-commerce (reste inchangée)
- **PostgreSQL** : Base de données **dédiée uniquement à SonarQube** (contrainte technique de SonarQube)
- **Réseau** : Tous les services partagent le réseau Docker `ecommerce-network`
- **Modularité** : SonarQube peut être démarré/arrêté indépendamment de l'application principale

---

## Prérequis

### Système

- **Docker** : version 20.10 ou supérieure
- **Docker Compose** : version 2.0 ou supérieure
- **Mémoire RAM** : Minimum 4 GB disponibles (2 GB pour SonarQube seul)
- **Espace disque** : Minimum 5 GB libres

### Vérification des prérequis

```bash
# Vérifier Docker
docker --version

# Vérifier Docker Compose (v2 intégré)
docker compose version

# OU pour la version standalone (v1)
docker-compose --version

# Vérifier la mémoire disponible
free -h
```

### Configuration système Linux

SonarQube utilise Elasticsearch qui nécessite une configuration spécifique :

```bash
# Augmenter les limites système (temporaire)
sudo sysctl -w vm.max_map_count=262144
sudo sysctl -w fs.file-max=65536

# Pour rendre permanent, ajoutez au fichier /etc/sysctl.conf :
vm.max_map_count=262144
fs.file-max=65536

# Puis appliquez
sudo sysctl -p
```

---

## Installation

### 1. Configuration des variables d'environnement

```bash
# Copier le fichier d'exemple
cp .env.example .env

# (Optionnel) Modifier les valeurs par défaut
nano .env
```

### 2. Démarrer l'infrastructure complète

#### Option A : Démarrer tout (Application + SonarQube)

```bash
# Démarrer MongoDB, Kafka et tous les services d'abord
docker compose up -d

# Puis démarrer SonarQube
docker compose -f docker-compose.yml -f docker-compose.sonarqube.yml up -d
```

#### Option B : Démarrer uniquement SonarQube (si l'app est déjà en cours)

```bash
# Si le réseau ecommerce-network existe déjà
docker compose -f docker-compose.sonarqube.yml up -d
```

#### Utiliser les scripts fournis (recommandé)

```bash
# Linux/Mac
./scripts/start-sonarqube.sh

# Windows
.\scripts\start-sonarqube.bat
```

### 3. Vérifier le démarrage

```bash
# Voir les logs SonarQube
docker logs -f ecommerce-sonarqube

# Vérifier le statut des conteneurs
docker ps | grep sonarqube
```

Le démarrage initial peut prendre 2-3 minutes. SonarQube est prêt quand vous voyez :
```
SonarQube is operational
```

---

## Accès à SonarQube

### Interface Web

**URL** : http://localhost:9000

### Credentials par défaut

- **Username** : `admin`
- **Password** : `admin`

### Première connexion

1. Accédez à http://localhost:9000
2. Connectez-vous avec `admin` / `admin`
3. **IMPORTANT** : SonarQube vous demandera de changer le mot de passe
4. Choisissez un nouveau mot de passe sécurisé

### Génération d'un token d'authentification

Pour les analyses automatiques (CI/CD) :

1. Cliquez sur votre avatar en haut à droite
2. **My Account** > **Security**
3. **Generate Token**
   - Name : `ecommerce-analysis`
   - Type : `Global Analysis Token`
   - Expiration : Choisissez selon vos besoins
4. **Copiez le token** (vous ne pourrez plus le voir après)
5. Ajoutez-le à votre fichier `.env` :
   ```bash
   SONAR_TOKEN=votre_token_ici
   ```

---

## Configuration des projets

### Créer les projets dans SonarQube

#### 1. Via l'interface web

1. Cliquez sur **Create Project** > **Manually**
2. Pour chaque microservice, créez un projet :

**User Service :**
- Project key : `ecommerce-user-service`
- Display name : `E-Commerce User Service`

**Product Service :**
- Project key : `ecommerce-product-service`
- Display name : `E-Commerce Product Service`

**Media Service :**
- Project key : `ecommerce-media-service`
- Display name : `E-Commerce Media Service`

**Frontend :**
- Project key : `ecommerce-frontend`
- Display name : `E-Commerce Frontend`

#### 2. Configurer Maven pour les projets Java

Ajoutez cette configuration à chaque `pom.xml` des microservices :

```xml
<properties>
    <!-- Autres propriétés... -->
    <sonar.host.url>http://localhost:9000</sonar.host.url>
    <sonar.projectKey>ecommerce-user-service</sonar.projectKey>
    <sonar.projectName>E-Commerce User Service</sonar.projectName>
    <sonar.java.binaries>target/classes</sonar.java.binaries>
    <sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
</properties>

<build>
    <plugins>
        <!-- Plugin JaCoCo pour la couverture de code -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- Plugin SonarQube -->
        <plugin>
            <groupId>org.sonarsource.scanner.maven</groupId>
            <artifactId>sonar-maven-plugin</artifactId>
            <version>4.0.0.4121</version>
        </plugin>
    </plugins>
</build>
```

**Note :** Modifiez `sonar.projectKey` selon le service.

---

## Analyse du code

### Backend Java (Maven)

#### Analyse locale

```bash
# Depuis le répertoire d'un microservice
cd backend/user-service

# Lancer les tests et l'analyse
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=ecommerce-user-service \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=VOTRE_TOKEN
```

#### Analyser tous les microservices

```bash
# Script pour analyser tous les services backend
for service in user-service product-service media-service; do
  echo "Analysing $service..."
  cd backend/$service
  mvn clean verify sonar:sonar \
    -Dsonar.projectKey=ecommerce-$service \
    -Dsonar.host.url=http://localhost:9000 \
    -Dsonar.token=$SONAR_TOKEN
  cd ../..
done
```

### Frontend Angular

#### Installation du scanner

```bash
cd frontend
npm install --save-dev sonarqube-scanner
```

#### Configuration

Créez le fichier `sonar-project.properties` dans `/frontend` :

```properties
sonar.projectKey=ecommerce-frontend
sonar.projectName=E-Commerce Frontend
sonar.projectVersion=1.0

# Chemins sources
sonar.sources=src
sonar.tests=src
sonar.test.inclusions=**/*.spec.ts

# Exclusions
sonar.exclusions=**/node_modules/**,**/*.spec.ts,**/test/**,**/dist/**

# TypeScript
sonar.typescript.lcov.reportPaths=coverage/lcov.info

# Configuration
sonar.sourceEncoding=UTF-8
```

#### Lancer l'analyse

```bash
cd frontend

# Générer le coverage
npm run test -- --code-coverage --watch=false --browsers=ChromeHeadless

# Lancer l'analyse SonarQube
npx sonar-scanner \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=VOTRE_TOKEN
```

### Intégration CI/CD

Pour automatiser les analyses avec GitHub Actions, Jenkins, ou GitLab CI, consultez :
- [GitHub Actions](https://docs.sonarqube.org/latest/analysis/github-integration/)
- [Jenkins](https://docs.sonarqube.org/latest/analysis/jenkins/)
- [GitLab CI](https://docs.sonarqube.org/latest/analysis/gitlab-integration/)

---

## Commandes utiles

### Gestion des conteneurs

```bash
# Démarrer SonarQube
docker compose -f docker-compose.sonarqube.yml up -d

# Arrêter SonarQube
docker compose -f docker-compose.sonarqube.yml down

# Redémarrer SonarQube
docker compose -f docker-compose.sonarqube.yml restart

# Voir les logs
docker logs -f ecommerce-sonarqube
docker logs -f ecommerce-sonarqube-postgres

# Voir l'utilisation des ressources
docker stats ecommerce-sonarqube
```

### Nettoyage

```bash
# Arrêter et supprimer les conteneurs (garde les volumes)
docker compose -f docker-compose.sonarqube.yml down

# Supprimer TOUT (conteneurs + volumes + données)
# ⚠️  ATTENTION : Supprime toutes les données SonarQube !
docker compose -f docker-compose.sonarqube.yml down -v
```

### Backup des données

```bash
# Backup PostgreSQL
docker exec ecommerce-sonarqube-postgres pg_dump -U sonar sonarqube > sonarqube_backup_$(date +%Y%m%d).sql

# Restore
docker exec -i ecommerce-sonarqube-postgres psql -U sonar sonarqube < sonarqube_backup_20241215.sql
```

---

## Troubleshooting

### Problème : SonarQube ne démarre pas

**Symptômes :**
- Le conteneur redémarre continuellement
- Erreur `max virtual memory areas vm.max_map_count [65530] is too low`

**Solution :**
```bash
# Linux
sudo sysctl -w vm.max_map_count=262144

# Windows (WSL2)
wsl -d docker-desktop
sysctl -w vm.max_map_count=262144
exit
```

### Problème : Erreur de connexion à la base de données

**Symptômes :**
- Logs : `Unable to connect to Database`
- SonarQube ne démarre pas complètement

**Solutions :**
1. Vérifiez que PostgreSQL est bien démarré :
   ```bash
   docker logs ecommerce-sonarqube-postgres
   ```

2. Vérifiez les credentials dans `.env`

3. Redémarrez dans l'ordre :
   ```bash
   docker-compose -f docker-compose.sonarqube.yml restart sonarqube-postgres
   docker-compose -f docker-compose.sonarqube.yml restart sonarqube
   ```

### Problème : Port 9000 déjà utilisé

**Symptômes :**
- Erreur `port is already allocated`

**Solution :**
Changez le port dans `.env` :
```bash
SONAR_PORT=9001
```

Puis relancez :
```bash
docker compose -f docker-compose.sonarqube.yml up -d
```

### Problème : Analyse Maven échoue

**Symptômes :**
- Erreur `Not authorized. Please check the properties sonar.token`

**Solutions :**
1. Vérifiez que le token est correct
2. Régénérez un nouveau token si nécessaire
3. Utilisez le token dans la commande :
   ```bash
   mvn sonar:sonar -Dsonar.token=VOTRE_TOKEN
   ```

### Problème : Mémoire insuffisante

**Symptômes :**
- SonarQube est très lent
- Le conteneur s'arrête brutalement
- Erreur `OutOfMemoryError`

**Solution :**
Augmentez les limites dans `docker-compose.sonarqube.yml` :
```yaml
mem_limit: 4g
mem_reservation: 1g
```

### Problème : Le réseau ecommerce-network n'existe pas

**Symptômes :**
- Erreur `network ecommerce-network not found`

**Solution :**
Démarrez d'abord l'infrastructure principale :
```bash
docker compose up -d
```

Puis lancez SonarQube :
```bash
docker compose -f docker-compose.sonarqube.yml up -d
```

### Vérification de santé complète

```bash
# Vérifier que tous les services sont UP
docker compose -f docker-compose.sonarqube.yml ps

# Tester l'API SonarQube
curl http://localhost:9000/api/system/health

# Vérifier la version
curl http://localhost:9000/api/system/status | jq
```

---

## Ressources supplémentaires

### Documentation officielle
- [SonarQube Documentation](https://docs.sonarqube.org/latest/)
- [SonarQube Scanner for Maven](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/)
- [SonarScanner for JavaScript/TypeScript](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner/)

### Plugins recommandés
- **Java** : Intégré par défaut
- **TypeScript** : Intégré par défaut
- **JaCoCo** : Pour la couverture de code Java

### Support
- [Community Forum](https://community.sonarsource.com/)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/sonarqube)

---

**Dernière mise à jour :** Décembre 2024