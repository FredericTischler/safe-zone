# SonarQube - Quick Start Guide

> **Note** : Les commandes utilisent `docker compose` (v2). Si vous avez l'ancienne version, remplacez par `docker-compose`. Les scripts fournis gèrent cela automatiquement.

## Démarrage rapide (3 étapes)

### 1. Démarrer SonarQube

**Linux/Mac :**
```bash
./scripts/start-sonarqube.sh
```

**Windows :**
```cmd
.\scripts\start-sonarqube.bat
```

### 2. Se connecter

- URL : http://localhost:9000
- Username : `admin`
- Password : `admin`

**⚠️ Changez le mot de passe lors de la première connexion !**

### 3. Analyser votre code

#### Backend Java (Maven)

```bash
# Depuis le répertoire d'un microservice
cd backend/user-service

# Analyser
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=ecommerce-user-service \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=VOTRE_TOKEN
```

#### Frontend Angular

```bash
cd frontend

# Installer le scanner (première fois)
npm install --save-dev sonarqube-scanner

# Créer sonar-project.properties (voir doc complète)

# Analyser
npx sonar-scanner \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=VOTRE_TOKEN
```

## Générer un token d'authentification

1. Connectez-vous à http://localhost:9000
2. **My Account** (avatar en haut à droite) > **Security**
3. **Generate Token**
   - Name : `ecommerce-analysis`
   - Type : `Global Analysis Token`
4. Copiez le token et ajoutez-le à `.env` :
   ```bash
   SONAR_TOKEN=votre_token_ici
   ```

## Arrêter SonarQube

**Linux/Mac :**
```bash
./scripts/stop-sonarqube.sh
```

**Windows :**
```cmd
.\scripts\stop-sonarqube.bat
```

## Commandes utiles

```bash
# Voir les logs
docker logs -f ecommerce-sonarqube

# Redémarrer
docker compose -f docker-compose.sonarqube.yml restart

# Statut
docker compose -f docker-compose.sonarqube.yml ps
```

## Problèmes courants

### SonarQube ne démarre pas (Linux)

```bash
sudo sysctl -w vm.max_map_count=262144
```

### SonarQube ne démarre pas (Windows/WSL2)

Ouvrez PowerShell en tant qu'administrateur :
```powershell
wsl -d docker-desktop
sysctl -w vm.max_map_count=262144
exit
```

### Port 9000 déjà utilisé

Modifiez le port dans `.env` :
```bash
SONAR_PORT=9001
```

---

📖 **Documentation complète** : [docs/sonarqube-setup.md](./sonarqube-setup.md)