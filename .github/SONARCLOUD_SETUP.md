# Configuration SonarCloud - Guide de démarrage rapide

Ce guide vous explique comment configurer SonarCloud pour l'intégration avec les workflows GitHub Actions.

---

## Prérequis

- Compte GitHub avec accès administrateur au dépôt
- Compte SonarCloud (gratuit pour projets open source)
- Accès à l'organisation `zone01-ecommerce` sur SonarCloud

---

## Étape 1 : Configuration SonarCloud

### 1.1 Créer l'organisation (si elle n'existe pas)

1. Se connecter à [SonarCloud](https://sonarcloud.io)
2. Cliquer sur **"+"** → **Analyze new project**
3. Choisir **GitHub** comme provider
4. Autoriser SonarCloud à accéder à votre organisation GitHub
5. Créer l'organisation avec le nom : `zone01-ecommerce`

### 1.2 Créer les 4 projets

Pour chaque service, créer un projet :

#### User Service
```
Project Key: ecommerce-user-service
Display Name: E-Commerce User Service
```

#### Product Service
```
Project Key: ecommerce-product-service
Display Name: E-Commerce Product Service
```

#### Media Service
```
Project Key: ecommerce-media-service
Display Name: E-Commerce Media Service
```

#### Frontend
```
Project Key: ecommerce-frontend
Display Name: E-Commerce Frontend
Main Language: TypeScript/JavaScript
```

### 1.3 Désactiver l'analyse automatique

Pour chaque projet :
1. Aller dans **Administration → Analysis Method**
2. Désactiver **Automatic Analysis**
3. Sélectionner **GitHub Actions**

---

## Étape 2 : Générer le Token SonarCloud

### 2.1 Créer un token

1. Se connecter à [SonarCloud](https://sonarcloud.io)
2. Cliquer sur votre avatar (en haut à droite) → **My Account**
3. Aller dans l'onglet **Security**
4. Dans la section **Generate Tokens** :
   - **Name:** `GitHub Actions - E-Commerce`
   - **Type:** Global Analysis Token
   - **Expiration:** No expiration (ou 90 jours selon politique)
5. Cliquer sur **Generate**
6. **IMPORTANT:** Copier le token immédiatement (il ne sera plus visible)

Exemple de format de token généré :
```
sqp_[40_CARACTERES_ALPHANUMERIQUES_GENERES_PAR_SONARCLOUD]
```

> **IMPORTANT:** Ne jamais commiter le vrai token dans le code. Utilisez toujours les GitHub Secrets.

### 2.2 Vérifier les permissions

Le token doit avoir les permissions suivantes :
- Analyze Projects
- Browse
- Execute Analysis

---

## Étape 3 : Configuration GitHub

### 3.1 Ajouter le secret SONAR_TOKEN

1. Aller sur votre dépôt GitHub
2. Cliquer sur **Settings** (onglet du dépôt)
3. Dans le menu de gauche : **Secrets and variables → Actions**
4. Cliquer sur **New repository secret**
5. Remplir :
   ```
   Name: SONAR_TOKEN
   Value: [Coller le token SonarCloud]
   ```
6. Cliquer sur **Add secret**

### 3.2 Vérifier les permissions GitHub Actions

1. Toujours dans **Settings**
2. Aller dans **Actions → General**
3. Dans **Workflow permissions**, sélectionner :
   - **Read and write permissions**
4. Cocher : **Allow GitHub Actions to create and approve pull requests**
5. Cliquer sur **Save**

---

## Étape 4 : Configuration Quality Gates (optionnel)

### 4.1 Quality Gate par défaut

SonarCloud utilise par défaut la Quality Gate "Sonar way" qui vérifie :
- 0 nouveaux bugs
- 0 nouvelles vulnérabilités
- Couverture ≥ 80% sur nouveau code
- Code dupliqué ≤ 3%
- Security Hotspots reviewés

### 4.2 Personnaliser la Quality Gate

Pour personnaliser pour votre organisation :

1. Aller sur SonarCloud → **Quality Gates**
2. Créer une nouvelle Quality Gate ou modifier "Sonar way"
3. Définir vos conditions :

**Exemple de conditions recommandées :**
```
New Code:
- Coverage ≥ 80%
- Duplicated Lines ≤ 3%
- Maintainability Rating = A
- Reliability Rating = A
- Security Rating = A

Overall Code:
- Security Hotspots Reviewed ≥ 100%
```

4. Assigner la Quality Gate à tous vos projets

---

## Étape 5 : Tester la configuration

### 5.1 Test manuel

1. Faire une modification mineure dans le code
2. Créer une Pull Request
3. Vérifier que les workflows se déclenchent
4. Attendre la fin de l'analyse (5-10 minutes)
5. Vérifier les commentaires sur la PR
6. Vérifier les résultats sur SonarCloud

### 5.2 Test de chaque composant

#### Backend - User Service
```bash
cd backend/user-service
# Tester localement
mvn clean verify sonar:sonar \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-user-service \
  -Dsonar.token=YOUR_TOKEN
```

#### Frontend
```bash
cd frontend
# Tester localement
npm test -- --no-watch --code-coverage
npx sonar-scanner \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-frontend \
  -Dsonar.token=YOUR_TOKEN
```

---

## Étape 6 : Configurer les branches

### 6.1 Branches principales

Pour chaque projet dans SonarCloud :
1. Aller dans **Administration → Branches and Pull Requests**
2. Définir la branche principale : `main`
3. Configurer **New Code Definition** :
   - **Previous version** (recommandé pour CI/CD)
   - Ou **Number of days** (ex: 30 jours)

### 6.2 Analyse de PR

Les Pull Requests sont automatiquement analysées et comparées à la branche `main`.

---

## Étape 7 : Badges (optionnel)

### 7.1 Obtenir les badges

Pour chaque projet :
1. Aller sur SonarCloud → Projet → **Information**
2. Section **Badges**, copier les URLs

### 7.2 Ajouter au README

```markdown
# E-Commerce Platform

## Quality Status

### Backend Services

[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=ecommerce-user-service&metric=alert_status)](https://sonarcloud.io/dashboard?id=ecommerce-user-service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ecommerce-user-service&metric=coverage)](https://sonarcloud.io/dashboard?id=ecommerce-user-service)

### Frontend

[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=ecommerce-frontend&metric=alert_status)](https://sonarcloud.io/dashboard?id=ecommerce-frontend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ecommerce-frontend&metric=coverage)](https://sonarcloud.io/dashboard?id=ecommerce-frontend)
```

---

## Configuration des fichiers de projet

### Backend - sonar-project.properties (optionnel)

Créer `backend/[service]/sonar-project.properties` :

```properties
# User Service
sonar.projectKey=ecommerce-user-service
sonar.projectName=E-Commerce User Service
sonar.organization=zone01-ecommerce

# Sources
sonar.sources=src/main/java
sonar.tests=src/test/java

# Java
sonar.java.binaries=target/classes
sonar.java.test.binaries=target/test-classes

# Coverage
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

# Exclusions
sonar.exclusions=**/config/**,**/dto/**,**/entity/**,**/*Application.java
sonar.coverage.exclusions=**/config/**,**/dto/**,**/entity/**,**/*Application.java
```

### Frontend - sonar-project.properties (optionnel)

Créer `frontend/sonar-project.properties` :

```properties
# Frontend
sonar.projectKey=ecommerce-frontend
sonar.projectName=E-Commerce Frontend
sonar.organization=zone01-ecommerce

# Sources
sonar.sources=src
sonar.tests=src
sonar.test.inclusions=**/*.spec.ts

# Exclusions
sonar.exclusions=**/*.spec.ts,**/node_modules/**,**/dist/**,**/coverage/**,**/*.module.ts,**/main.ts,**/environments/**
sonar.coverage.exclusions=**/*.spec.ts,**/*.module.ts,**/main.ts,**/environments/**

# Coverage
sonar.javascript.lcov.reportPaths=coverage/frontend/lcov.info
sonar.typescript.lcov.reportPaths=coverage/frontend/lcov.info
```

---

## Checklist de configuration

### SonarCloud
- [ ] Organisation `zone01-ecommerce` créée
- [ ] 4 projets créés avec les bons Project Keys
- [ ] Automatic Analysis désactivé
- [ ] GitHub Actions sélectionné comme méthode d'analyse
- [ ] Token SonarCloud généré
- [ ] Quality Gates configurées
- [ ] Branches principales définies

### GitHub
- [ ] Secret `SONAR_TOKEN` ajouté
- [ ] Workflow permissions configurées (Read and write)
- [ ] Pull Request permissions activées
- [ ] Workflows `.github/workflows/*.yml` présents

### Tests locaux
- [ ] Backend - User Service analyse OK
- [ ] Backend - Product Service analyse OK
- [ ] Backend - Media Service analyse OK
- [ ] Frontend analyse OK

### Validation finale
- [ ] Pull Request test créée
- [ ] Workflows se déclenchent automatiquement
- [ ] Analyses SonarCloud complètes
- [ ] Commentaires PR générés
- [ ] Quality Gates vérifiées
- [ ] Artifacts uploadés

---

## Résolution des problèmes courants

### Erreur: "Organization not found"

**Solution :**
```yaml
# Vérifier l'orthographe dans les workflows
-Dsonar.organization=zone01-ecommerce  # ✅ Correct
-Dsonar.organization=zone01-eCommerce  # ❌ Incorrect (sensible à la casse)
```

### Erreur: "Project not found"

**Solution :**
1. Vérifier que le projet existe dans SonarCloud
2. Vérifier le Project Key exact (sensible à la casse)
3. Vérifier que le token a accès à ce projet

### Erreur: "Insufficient privileges"

**Solution :**
1. Régénérer un nouveau token SonarCloud
2. Vérifier qu'il a les permissions "Execute Analysis"
3. Mettre à jour le secret GitHub

### Erreur: "Quality Gate timeout"

**Solution :**
```yaml
# Augmenter le timeout dans les workflows
timeout-minutes: 10  # Au lieu de 5
```

### Coverage à 0%

**Solution Backend :**
```bash
# Vérifier que JaCoCo génère bien le rapport
mvn clean test
ls -la target/site/jacoco/jacoco.xml  # Doit exister
```

**Solution Frontend :**
```bash
# Vérifier que les tests génèrent la couverture
npm test -- --no-watch --code-coverage
ls -la coverage/frontend/lcov.info  # Doit exister
```

---

## Commandes utiles

### Nettoyer les caches GitHub Actions
```bash
gh cache list
gh cache delete <cache-key>
```

### Lister les secrets GitHub
```bash
gh secret list
```

### Ajouter un secret via CLI
```bash
gh secret set SONAR_TOKEN
# Coller le token quand demandé
```

### Déclencher un workflow manuellement
```bash
gh workflow run sonarqube-full.yml
```

### Voir les logs d'un workflow
```bash
gh run list --workflow=sonarqube-full.yml
gh run view <run-id> --log
```

---

## Ressources additionnelles

### Documentation
- [SonarCloud Docs](https://docs.sonarcloud.io/)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [SonarCloud GitHub Action](https://github.com/marketplace/actions/sonarcloud-scan)

### Exemples
- [SonarCloud Examples](https://github.com/SonarSource/sonarcloud-github-action-samples)

### Support
- [SonarCloud Community](https://community.sonarsource.com/)
- [GitHub Actions Community](https://github.com/orgs/community/discussions/categories/actions)

---

**Configuration complétée le :** [Date]
**Par :** [Votre nom]
**Version :** 1.0