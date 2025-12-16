# Guide Complet : Configuration des GitHub Secrets

## Table des Matières
1. [Introduction aux GitHub Secrets](#introduction-aux-github-secrets)
2. [Accéder aux Secrets GitHub](#accéder-aux-secrets-github)
3. [Liste des Secrets Nécessaires](#liste-des-secrets-nécessaires)
4. [Configuration Étape par Étape](#configuration-étape-par-étape)
5. [Utilisation dans les Workflows](#utilisation-dans-les-workflows)
6. [Tester les Secrets](#tester-les-secrets)
7. [Bonnes Pratiques de Sécurité](#bonnes-pratiques-de-sécurité)
8. [Dépannage](#dépannage)

---

## 1. Introduction aux GitHub Secrets

### Qu'est-ce qu'un GitHub Secret ?

Un **GitHub Secret** est une variable d'environnement chiffrée stockée de manière sécurisée dans votre repository GitHub. Les secrets permettent de stocker des informations sensibles (tokens, mots de passe, clés API) qui doivent être utilisées dans vos workflows GitHub Actions sans les exposer dans le code.

### Pourquoi Utiliser des Secrets ?

#### Problème Sans Secrets

**Code dangereux (à ne JAMAIS faire) :**
```yaml
# ❌ DANGER : Token exposé publiquement dans le code
- name: SonarQube Scan
  run: mvn sonar:sonar -Dsonar.token=squ_abc123xyz789
```

**Conséquences :**
- Token visible par tous (repo public)
- Risque d'utilisation malveillante du token
- Accès non autorisé à votre SonarQube/SonarCloud
- Possible suppression ou modification de vos analyses

#### Solution Avec Secrets

**Code sécurisé :**
```yaml
# ✅ SÉCURISÉ : Token stocké comme secret chiffré
- name: SonarQube Scan
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: mvn sonar:sonar
```

**Avantages :**
- Token jamais visible dans le code
- Chiffrement automatique par GitHub (AES-256)
- Logs masqués (GitHub cache automatiquement les valeurs des secrets)
- Gestion centralisée des credentials
- Rotation facile (changer le token en un clic)

### Types de Secrets GitHub

#### 1. Repository Secrets
- **Portée** : Un seul repository
- **Usage** : Projets individuels
- **Accès** : Workflows de ce repo uniquement

#### 2. Organization Secrets
- **Portée** : Tous les repos d'une organisation
- **Usage** : Partager un secret entre plusieurs repos
- **Accès** : Tous les repos de l'organisation (ou sélection)

#### 3. Environment Secrets
- **Portée** : Un environnement spécifique (production, staging)
- **Usage** : Credentials différents par environnement
- **Accès** : Workflows déployant vers cet environnement

**Pour ce projet, nous utiliserons des Repository Secrets.**

### Comment GitHub Chiffre les Secrets

**Processus de Chiffrement :**
```
Vous entrez le secret (texte clair)
     ↓
GitHub chiffre avec libsodium sealed boxes
     ↓
Stockage chiffré (AES-256-GCM)
     ↓
Déchiffrement au runtime dans le workflow
     ↓
Injection dans les variables d'environnement
     ↓
Masquage automatique dans les logs
```

**Sécurité :**
- Chiffrement au repos (dans la base de données GitHub)
- Chiffrement en transit (TLS/HTTPS)
- Pas de déchiffrement possible via l'API GitHub
- Impossible de récupérer la valeur une fois créée (seulement mettre à jour)

---

## 2. Accéder aux Secrets GitHub

### Pré-requis

- Compte GitHub avec accès au repository `safe-zone`
- Permissions nécessaires :
  - **Admin** ou **Write** sur le repository (pour créer/modifier des secrets)
  - Si organisation : rôle **Organization Owner** ou permissions explicites

### Chemin d'Accès : Instructions Détaillées

#### Méthode 1 : Via l'Interface Web GitHub (Recommandée)

**Étape 1 : Aller sur GitHub.com**
```
1. Ouvrir un navigateur web
2. Aller sur https://github.com
3. Se connecter si nécessaire
```

**Étape 2 : Naviguer vers votre Repository**
```
1. Cliquer sur votre profil (icône en haut à droite)
2. Cliquer sur "Your repositories"
3. Trouver et cliquer sur "safe-zone"

Ou directement :
https://github.com/VOTRE-USERNAME/safe-zone
```

**Étape 3 : Accéder aux Settings**
```
1. Une fois sur la page du repo, regarder la barre de navigation horizontale
2. Cliquer sur l'onglet "Settings" (icône engrenage)

Note : Si vous ne voyez pas "Settings", vous n'avez pas les permissions nécessaires.
Demandez à l'owner du repo de vous ajouter comme collaborator.
```

**Représentation visuelle (texte) :**
```
[Code] [Issues] [Pull requests] [Actions] [Projects] [Wiki] [Security] [Insights] [Settings]
                                                                                      ↑
                                                                                  Cliquer ici
```

**Étape 4 : Naviguer vers Secrets and Variables**
```
1. Dans la sidebar gauche de Settings, descendre dans la section "Security"
2. Cliquer sur "Secrets and variables"
3. Un sous-menu s'ouvre, cliquer sur "Actions"

Sidebar ressemble à :
General
    Collaborators
    Branches
    Tags
    Rules
    Hooks
Security
    Code security and analysis
    Deploy keys
    Secrets and variables  ← Cliquer ici
        → Actions          ← Puis cliquer ici
        → Codespaces
        → Dependabot
```

**Étape 5 : Vous êtes arrivé !**
```
URL finale : https://github.com/VOTRE-USERNAME/safe-zone/settings/secrets/actions

Vous devriez voir :
- Titre : "Actions secrets and variables"
- Onglets : "Secrets" | "Variables"
- Bouton vert : "New repository secret"
```

#### Méthode 2 : URL Directe

```
Format : https://github.com/OWNER/REPO/settings/secrets/actions

Exemple : https://github.com/jbenromd/safe-zone/settings/secrets/actions

Remplacer :
- OWNER : Votre username GitHub ou nom d'organisation
- REPO : safe-zone
```

#### Méthode 3 : Via GitHub CLI (Pour Utilisateurs Avancés)

**Installation de GitHub CLI :**
```bash
# Linux (Debian/Ubuntu)
sudo apt install gh

# macOS
brew install gh

# Windows
winget install GitHub.cli
```

**Authentification :**
```bash
gh auth login
# Suivre les instructions interactives
```

**Lister les secrets existants :**
```bash
gh secret list --repo VOTRE-USERNAME/safe-zone
```

**Créer un secret :**
```bash
gh secret set SONAR_TOKEN --repo VOTRE-USERNAME/safe-zone
# Puis coller la valeur du token quand demandé
```

---

## 3. Liste des Secrets Nécessaires

### Pour une Configuration avec SonarCloud (Recommandé)

#### Secret 1 : SONAR_TOKEN

**Description :**
Token d'authentification pour SonarCloud, permettant à GitHub Actions d'envoyer les résultats d'analyse de code.

**Format :**
```
squ_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```
- Commence par `squ_` (SonarQube User Token)
- Suivi de 40 caractères alphanumériques
- Longueur totale : 44 caractères

**Exemple (fictif) :**
```
squ_abc123def456ghi789jkl012mno345pqr678stu
```

**Comment l'Obtenir :**
```
1. Aller sur https://sonarcloud.io
2. Se connecter avec GitHub
3. Cliquer sur votre avatar (haut droite)
4. "My Account" → "Security" tab
5. Section "Generate Tokens"
6. Name: GitHub-Actions-SafeZone
7. Type: Project Analysis Token (ou Global Analysis Token)
8. Expiration: No expiration (ou 90 days pour plus de sécurité)
9. Cliquer "Generate"
10. COPIER LE TOKEN IMMÉDIATEMENT (ne sera plus affiché après)
```

**Permissions Requises :**
- Exécuter des analyses de code
- Uploader les résultats
- Accéder aux Quality Gates

**Portée :**
- Tous les projets SonarCloud de votre organisation
- ou un projet spécifique (selon le type de token choisi)

**Expiration :**
- Recommandation sécurité : 90 jours
- Pratique pour projet école : No expiration
- Note : Si expiration, mettre un rappel dans le calendrier pour renouveler

**Utilisation dans le Workflow :**
```yaml
- name: SonarCloud Scan
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: mvn sonar:sonar
```

---

### Secrets Additionnels (Optionnels)

#### Secret 2 : DOCKER_USERNAME (Optionnel)

**Description :**
Nom d'utilisateur Docker Hub pour publier les images Docker des microservices.

**Format :**
```
votre-username-docker
```
- Chaîne alphanumérique simple
- Pas de caractères spéciaux (sauf `-` et `_`)

**Exemple :**
```
jbenromd
```

**Comment l'Obtenir :**
```
1. Créer un compte sur https://hub.docker.com
2. Votre username est visible en haut à droite après connexion
```

**Utilisation :**
```yaml
- name: Login to Docker Hub
  uses: docker/login-action@v3
  with:
    username: ${{ secrets.DOCKER_USERNAME }}
    password: ${{ secrets.DOCKER_PASSWORD }}
```

#### Secret 3 : DOCKER_PASSWORD (Optionnel)

**Description :**
Token d'accès Docker Hub (ou mot de passe) pour l'authentification.

**Format :**
- **Mot de passe** : Votre mot de passe Docker Hub
- **Access Token (recommandé)** : Chaîne de 36 caractères UUID

**Exemple Access Token (fictif) :**
```
dckr_pat_a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6
```

**Comment Obtenir un Access Token (recommandé) :**
```
1. https://hub.docker.com → Se connecter
2. Account Settings → Security
3. "New Access Token"
4. Access Token Description: GitHub-Actions-SafeZone
5. Access permissions: Read, Write, Delete
6. "Generate"
7. COPIER LE TOKEN IMMÉDIATEMENT
```

**Pourquoi Access Token > Mot de Passe :**
- Révocable individuellement (pas besoin de changer le password principal)
- Permissions limitées (scope spécifique)
- Traçabilité (logs d'utilisation)
- Expiration possible

#### Secret 4 : SLACK_WEBHOOK_URL (Optionnel)

**Description :**
URL de webhook Slack pour recevoir des notifications de build (succès/échec).

**Format :**
```
https://hooks.slack.com/services/{WORKSPACE_ID}/{CHANNEL_ID}/{TOKEN}
```

**Exemple (fictif) :**
```
URL format: https://hooks.slack.com/services/XXX/YYY/ZZZ
où XXX, YYY et ZZZ sont des identifiants uniques générés par Slack
```

**Comment l'Obtenir :**
```
1. Aller sur https://api.slack.com/apps
2. Créer une nouvelle app ou utiliser existante
3. Features → Incoming Webhooks
4. Activer "Activate Incoming Webhooks"
5. Cliquer "Add New Webhook to Workspace"
6. Sélectionner le channel (ex: #github-ci-cd)
7. Autoriser
8. Copier le Webhook URL
```

**Utilisation :**
```yaml
- name: Notify Slack
  if: failure()
  uses: slackapi/slack-github-action@v1
  with:
    webhook: ${{ secrets.SLACK_WEBHOOK_URL }}
    payload: |
      {
        "text": "Build failed on main branch!"
      }
```

#### Secret 5 : SONAR_HOST_URL (Si Self-Hosted SonarQube)

**Description :**
URL du serveur SonarQube self-hosted (seulement si vous n'utilisez pas SonarCloud).

**Format :**
```
https://sonarqube.votredomaine.com
```
ou
```
http://IP-DU-SERVEUR:9000
```

**Exemple :**
```
https://sonarqube.zone01.com
```

**Note :**
- Pour SonarCloud : Ce secret n'est PAS nécessaire (URL hardcodée : `https://sonarcloud.io`)
- Seulement pour configuration self-hosted

---

## 4. Configuration Étape par Étape

### Configuration du Secret SONAR_TOKEN

#### Étape 1 : Générer le Token sur SonarCloud

**1.1 Accéder à SonarCloud**
```
URL : https://sonarcloud.io
Cliquer sur "Log in" → "Log in with GitHub"
Autoriser SonarCloud si première connexion
```

**1.2 Naviguer vers Security Settings**
```
Une fois connecté :
1. Cliquer sur votre avatar (cercle en haut à droite)
2. Dans le menu déroulant, cliquer sur "My Account"
3. Une nouvelle page s'ouvre avec plusieurs onglets
4. Cliquer sur l'onglet "Security"
```

**Représentation visuelle (texte) :**
```
Page "My Account"
Onglets : [Profile] [Security] [Notifications] [Organizations]
                      ↑
                  Cliquer ici
```

**1.3 Générer un Token**
```
Section "Generate Tokens"

Formulaire :
- Name : GitHub-Actions-SafeZone
  (Description pour identifier le token)

- Type : [Dropdown]
  Options disponibles :
  • User Token (recommandé pour projets multiples)
  • Project Analysis Token (pour un seul projet)

  Choisir : User Token

- Expires in : [Dropdown]
  Options :
  • 30 days
  • 90 days
  • No expiration

  Recommandation projet école : No expiration
  Recommandation production : 90 days

Cliquer sur le bouton [Generate]
```

**1.4 Copier le Token**
```
Un token s'affiche :
┌─────────────────────────────────────────────────┐
│ ✅ Token generated successfully                 │
│                                                  │
│ squ_abc123def456ghi789jkl012mno345pqr678stu    │
│                                                  │
│ ⚠️ Make sure to copy it now as you will not be │
│    able to see this token again!                │
│                                                  │
│ [Copy]                                          │
└─────────────────────────────────────────────────┘

Actions :
1. Cliquer sur [Copy] ou sélectionner le token et Ctrl+C
2. Coller IMMÉDIATEMENT dans un fichier temporaire (Notepad, Notes, etc.)
3. NE PAS fermer la page avant d'avoir testé le token
```

**⚠️ IMPORTANT :**
- Le token ne sera JAMAIS réaffiché
- Si vous le perdez, vous devrez en générer un nouveau
- Gardez-le temporairement dans un endroit sûr le temps de la configuration

#### Étape 2 : Créer le Secret dans GitHub

**2.1 Naviguer vers les Secrets**
```
1. Aller sur https://github.com/VOTRE-USERNAME/safe-zone
2. Cliquer sur "Settings"
3. Sidebar gauche : "Secrets and variables" → "Actions"
```

**2.2 Cliquer sur "New repository secret"**
```
Bouton vert en haut à droite de la page
┌──────────────────────────┐
│ New repository secret    │
└──────────────────────────┘
```

**2.3 Remplir le Formulaire**
```
Formulaire de création de secret :

┌─────────────────────────────────────────────────┐
│ New secret                                      │
├─────────────────────────────────────────────────┤
│                                                  │
│ Name *                                          │
│ ┌─────────────────────────────────────────────┐ │
│ │ SONAR_TOKEN                                  │ │
│ └─────────────────────────────────────────────┘ │
│                                                  │
│ Secret *                                        │
│ ┌─────────────────────────────────────────────┐ │
│ │ squ_abc123def456ghi789jkl012mno345pqr678stu │ │
│ └─────────────────────────────────────────────┘ │
│                                                  │
│ [Add secret]                                    │
└─────────────────────────────────────────────────┘

Instructions :
1. Champ "Name" : Taper exactement SONAR_TOKEN
   - Tout en majuscules
   - Pas d'espaces
   - Underscore autorisé

2. Champ "Secret" : Coller le token copié de SonarCloud
   - Ctrl+V ou Cmd+V
   - Vérifier qu'il commence par "squ_"
   - Vérifier qu'il n'y a pas d'espace avant/après

3. Cliquer sur le bouton vert [Add secret]
```

**2.4 Vérification**
```
Après avoir cliqué "Add secret", vous êtes redirigé vers la liste des secrets.

Vous devriez voir :
┌─────────────────────────────────────────────────┐
│ Repository secrets                              │
├─────────────────────────────────────────────────┤
│ SONAR_TOKEN                    Updated now      │
│                                [Update] [Remove]│
└─────────────────────────────────────────────────┘

✅ Le secret est créé avec succès !

Note : La valeur du token n'est pas affichée (sécurité).
Vous ne pouvez que le mettre à jour ou le supprimer.
```

#### Étape 3 : Vérifier les Permissions

**3.1 Vérifier les Permissions du Workflow**
```
1. Aller sur Settings → Actions → General
2. Descendre à "Workflow permissions"
3. Vérifier que c'est configuré comme :

   ○ Read repository contents permission
   ● Read and write permissions  ← Sélectionner celui-ci

4. ☑ Allow GitHub Actions to create and approve pull requests

5. Cliquer [Save] si vous avez fait des changements
```

**Pourquoi c'est nécessaire :**
- Permet aux workflows de lire les secrets
- Permet d'uploader les résultats SonarCloud
- Nécessaire pour commenter les PRs avec les résultats

---

### Configuration des Secrets Optionnels (Docker Hub)

#### Étape 1 : Créer DOCKER_USERNAME

```
1. GitHub → safe-zone → Settings → Secrets and variables → Actions
2. Cliquer "New repository secret"

Formulaire :
┌─────────────────────────────────────────────────┐
│ Name *                                          │
│ ┌─────────────────────────────────────────────┐ │
│ │ DOCKER_USERNAME                              │ │
│ └─────────────────────────────────────────────┘ │
│                                                  │
│ Secret *                                        │
│ ┌─────────────────────────────────────────────┐ │
│ │ jbenromd                                     │ │
│ └─────────────────────────────────────────────┘ │
│                                                  │
│ [Add secret]                                    │
└─────────────────────────────────────────────────┘

3. Cliquer [Add secret]
```

#### Étape 2 : Créer DOCKER_PASSWORD

**2.1 Générer un Access Token Docker Hub**
```
1. Aller sur https://hub.docker.com
2. Se connecter
3. Cliquer sur votre username (haut droite) → "Account Settings"
4. Onglet "Security"
5. Section "Access Tokens"
6. Cliquer [New Access Token]

Formulaire :
Access Token Description : GitHub-Actions-SafeZone
Access permissions : [✓] Read [✓] Write [✓] Delete

7. Cliquer [Generate]
8. COPIER LE TOKEN (dckr_pat_xxxxxx)
```

**2.2 Créer le Secret GitHub**
```
GitHub → safe-zone → Settings → Secrets and variables → Actions
Cliquer "New repository secret"

┌─────────────────────────────────────────────────┐
│ Name *                                          │
│ ┌─────────────────────────────────────────────┐ │
│ │ DOCKER_PASSWORD                              │ │
│ └─────────────────────────────────────────────┘ │
│                                                  │
│ Secret *                                        │
│ ┌─────────────────────────────────────────────┐ │
│ │ dckr_pat_abc123...                           │ │
│ └─────────────────────────────────────────────┘ │
│                                                  │
│ [Add secret]                                    │
└─────────────────────────────────────────────────┘

Cliquer [Add secret]
```

---

## 5. Utilisation dans les Workflows

### Syntaxe de Base

**Référencer un secret dans un workflow :**
```yaml
${{ secrets.NOM_DU_SECRET }}
```

**Important :**
- `secrets.` est obligatoire (contexte spécial GitHub)
- `NOM_DU_SECRET` doit correspondre EXACTEMENT au nom du secret
- Sensible à la casse : `SONAR_TOKEN` ≠ `sonar_token`

### Exemples d'Utilisation

#### Exemple 1 : SonarCloud Scan (Maven)

```yaml
- name: SonarCloud Scan
  working-directory: backend/user-service
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: mvn sonar:sonar
```

**Explication :**
- `env:` définit une variable d'environnement
- `SONAR_TOKEN` est accessible dans le processus Maven
- Maven SonarQube plugin lit automatiquement la variable `SONAR_TOKEN`

#### Exemple 2 : SonarCloud avec Action GitHub

```yaml
- name: SonarCloud Scan Frontend
  uses: SonarSource/sonarcloud-github-action@master
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  with:
    projectBaseDir: frontend
```

**Explication :**
- Action officielle SonarCloud
- Token passé via `env:`
- Configuration dans `sonar-project.properties`

#### Exemple 3 : Docker Hub Login

```yaml
- name: Login to Docker Hub
  uses: docker/login-action@v3
  with:
    username: ${{ secrets.DOCKER_USERNAME }}
    password: ${{ secrets.DOCKER_PASSWORD }}
```

**Explication :**
- Secrets passés directement dans `with:`
- Action gère l'authentification
- Credentials jamais exposés dans les logs

#### Exemple 4 : Passer un Secret comme Argument

```yaml
- name: Deploy to Server
  run: |
    curl -X POST https://api.example.com/deploy \
      -H "Authorization: Bearer ${{ secrets.API_TOKEN }}" \
      -d '{"version": "1.0.0"}'
```

**Explication :**
- Secret utilisé dans une commande shell
- Automatiquement masqué dans les logs par GitHub
- Utile pour scripts custom

#### Exemple 5 : Conditionner un Job sur la Présence d'un Secret

```yaml
deploy:
  runs-on: ubuntu-latest
  if: ${{ secrets.DOCKER_PASSWORD != '' }}
  steps:
    - name: Deploy Docker Images
      run: echo "Deploying..."
```

**Explication :**
- Job s'exécute seulement si le secret existe
- Utile pour rendre certaines étapes optionnelles
- Évite les erreurs si secret pas configuré

### Secrets dans Workflows Déclenchés par des Forks

**⚠️ SÉCURITÉ CRITIQUE**

**Comportement par défaut :**
```yaml
# Workflow déclenché par un fork
# Les secrets NE SONT PAS accessibles par défaut
```

**Pourquoi ?**
- Un attaquant pourrait :
  1. Forker votre repo
  2. Modifier le workflow pour afficher `${{ secrets.SONAR_TOKEN }}`
  3. Créer une PR
  4. Lire le secret dans les logs

**Protection GitHub :**
- Secrets des forks : NON accessibles par défaut
- Nécessite approbation manuelle d'un maintainer

**Configuration :**
```
Settings → Actions → General
→ Fork pull request workflows from outside collaborators

Options :
• Require approval for first-time contributors ← Recommandé
• Require approval for all outside collaborators
• Require approval for all outside collaborators and first-time contributors
```

**Recommandation projet école :**
```
Sélectionner : "Require approval for first-time contributors"
```

---

## 6. Tester les Secrets

### Méthode 1 : Test Manuel dans un Workflow

**Créer un workflow de test temporaire :**

**Fichier : `.github/workflows/test-secrets.yml`**
```yaml
name: Test Secrets Configuration

on:
  workflow_dispatch:  # Déclenchement manuel uniquement

jobs:
  test-secrets:
    name: Verify Secrets are Configured
    runs-on: ubuntu-latest
    steps:
      - name: Check SONAR_TOKEN exists
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: |
          if [ -z "$SONAR_TOKEN" ]; then
            echo "❌ SONAR_TOKEN is not configured"
            exit 1
          else
            echo "✅ SONAR_TOKEN is configured"
            echo "Token starts with: ${SONAR_TOKEN:0:7}..." # Affiche seulement les 7 premiers caractères
          fi

      - name: Check DOCKER_USERNAME exists (optional)
        env:
          DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
        run: |
          if [ -z "$DOCKER_USERNAME" ]; then
            echo "⚠️ DOCKER_USERNAME is not configured (optional)"
          else
            echo "✅ DOCKER_USERNAME is configured: $DOCKER_USERNAME"
          fi

      - name: Check DOCKER_PASSWORD exists (optional)
        env:
          DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
        run: |
          if [ -z "$DOCKER_PASSWORD" ]; then
            echo "⚠️ DOCKER_PASSWORD is not configured (optional)"
          else
            echo "✅ DOCKER_PASSWORD is configured"
            echo "Token starts with: ${DOCKER_PASSWORD:0:10}..."
          fi

      - name: All checks passed
        run: echo "✅ All required secrets are configured correctly!"
```

**Comment exécuter ce workflow :**
```
1. Commit et push ce fichier
   git add .github/workflows/test-secrets.yml
   git commit -m "ci: add secrets testing workflow"
   git push

2. Aller sur GitHub → Actions
3. Sélectionner "Test Secrets Configuration" dans la sidebar
4. Cliquer sur "Run workflow" (bouton à droite)
5. Cliquer sur "Run workflow" (confirmation)

6. Attendre 10-20 secondes
7. Cliquer sur le workflow qui vient de démarrer
8. Cliquer sur le job "Verify Secrets are Configured"
9. Lire les logs de chaque step

Résultats attendus :
✅ SONAR_TOKEN is configured
Token starts with: squ_abc...
⚠️ DOCKER_USERNAME is not configured (optional)  # Si pas configuré
⚠️ DOCKER_PASSWORD is not configured (optional)  # Si pas configuré
✅ All required secrets are configured correctly!
```

**Nettoyer après le test :**
```bash
# Supprimer le workflow de test (optionnel)
git rm .github/workflows/test-secrets.yml
git commit -m "ci: remove secrets test workflow"
git push
```

### Méthode 2 : Test avec SonarCloud Réel

**Créer un workflow minimaliste :**

**Fichier : `.github/workflows/test-sonarcloud.yml`**
```yaml
name: Test SonarCloud Connection

on:
  workflow_dispatch:

jobs:
  test-sonarcloud:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0  # Full history for SonarCloud

      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Test SonarCloud API
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: |
          echo "Testing connection to SonarCloud..."

          # Vérifier que le token est présent
          if [ -z "$SONAR_TOKEN" ]; then
            echo "❌ SONAR_TOKEN not found"
            exit 1
          fi

          # Tester l'authentification avec l'API SonarCloud
          response=$(curl -s -u "$SONAR_TOKEN:" https://sonarcloud.io/api/authentication/validate)

          if echo "$response" | grep -q '"valid":true'; then
            echo "✅ SonarCloud authentication successful!"
            echo "Response: $response"
          else
            echo "❌ SonarCloud authentication failed"
            echo "Response: $response"
            exit 1
          fi

      - name: Test Maven SonarQube Plugin
        working-directory: backend/user-service
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: |
          echo "Testing Maven Sonar plugin..."
          mvn sonar:sonar -Dsonar.verbose=true
```

**Exécuter :**
```
GitHub → Actions → Test SonarCloud Connection → Run workflow
```

**Résultats attendus :**
```
✅ SonarCloud authentication successful!
Response: {"valid":true}

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**Si erreurs :**
- Vérifier que le token commence bien par `squ_`
- Vérifier qu'il n'y a pas d'espace avant/après dans le secret GitHub
- Vérifier que le token n'a pas expiré sur SonarCloud
- Vérifier que `sonar.organization` est correct dans pom.xml

### Méthode 3 : Test avec GitHub CLI

**Pré-requis : GitHub CLI installé et authentifié**

```bash
# Lister tous les secrets (noms seulement, pas les valeurs)
gh secret list --repo VOTRE-USERNAME/safe-zone

# Résultat attendu :
SONAR_TOKEN           Updated 2024-12-15
DOCKER_USERNAME       Updated 2024-12-15
DOCKER_PASSWORD       Updated 2024-12-15
```

**Note :** GitHub CLI ne peut PAS afficher les valeurs des secrets (par design de sécurité).

---

## 7. Bonnes Pratiques de Sécurité

### 1. Principe du Moindre Privilège

**Créer des Tokens avec Permissions Minimales**

**Exemple SonarCloud :**
```
❌ Mauvais : Global Analysis Token avec accès à tous les projets
✅ Bon : Project Analysis Token avec accès à un seul projet

Si vous devez utiliser un Global Token :
- Limiter la durée de vie (90 jours max)
- Documenter pourquoi le scope global est nécessaire
```

**Exemple Docker Hub :**
```
❌ Mauvais : Mot de passe complet du compte
✅ Bon : Access Token avec permissions Read, Write uniquement

Pas besoin de "Delete" si vous ne supprimez jamais d'images via CI/CD
```

### 2. Rotation Régulière des Secrets

**Calendrier Recommandé :**
```
Secrets critiques (production) : Tous les 90 jours
Secrets projet école : Tous les 6 mois ou fin de projet

Actions :
1. Générer un nouveau token sur le service (SonarCloud, Docker Hub)
2. Mettre à jour le secret GitHub
3. Déclencher un build de test pour valider
4. Révoquer l'ancien token
```

**Automatisation possible :**
```yaml
# Recevoir une notification avant expiration
- name: Check Token Expiration
  run: |
    # Script custom pour vérifier les expirations
    echo "Token expires in X days"
```

### 3. Ne Jamais Logger les Secrets

**❌ DANGEREUX :**
```yaml
- name: Debug
  run: |
    echo "Token is: ${{ secrets.SONAR_TOKEN }}"  # ❌ NE JAMAIS FAIRE ÇA
```

**Pourquoi ?**
- GitHub masque automatiquement les secrets dans les logs
- MAIS seulement si le format est reconnu
- Si vous loggez manuellement, risque d'exposition

**✅ SÉCURISÉ :**
```yaml
- name: Debug
  run: |
    if [ -n "$SONAR_TOKEN" ]; then
      echo "Token is configured ✅"
      echo "Token length: ${#SONAR_TOKEN}"
      echo "Token starts with: ${SONAR_TOKEN:0:4}..."  # 4 premiers caractères OK
    else
      echo "Token is NOT configured ❌"
    fi
```

### 4. Protéger les Branches Principales

**Configuration Branch Protection :**
```
GitHub → Settings → Branches → Branch protection rules → main

Règles recommandées :
☑ Require a pull request before merging
  ☑ Require approvals: 1
☑ Require status checks to pass before merging
  ☑ Require branches to be up to date before merging
  Status checks: build-user-service, build-product-service, etc.
☑ Do not allow bypassing the above settings
☑ Require conversation resolution before merging
```

**Impact sur les secrets :**
- Impossible de push directement sur main
- Modifications de workflow nécessitent une PR
- Révision par un pair avant modification de secrets

### 5. Audit et Monitoring

**Vérifier l'Utilisation des Secrets :**
```
GitHub → Settings → Actions → General
→ Actions permissions
→ Activer : "Allow actions created by GitHub"
```

**Surveiller les Logs d'Actions :**
```
GitHub → Actions → Workflow run
→ Vérifier que les secrets ne sont pas exposés
→ Chercher des patterns inhabituels (beaucoup de failures)
```

**Alertes :**
```yaml
# Recevoir une notification si un secret est utilisé de manière suspecte
- name: Alert on Suspicious Activity
  if: failure()
  run: |
    curl -X POST ${{ secrets.SLACK_WEBHOOK_URL }} \
      -d '{"text": "⚠️ Workflow failed - possible security issue"}'
```

### 6. Documentation des Secrets

**Créer un fichier `.github/SECRETS.md` (sans les valeurs) :**

```markdown
# Secrets Configuration

## Required Secrets

### SONAR_TOKEN
- **Purpose:** Authenticate with SonarCloud for code analysis
- **Format:** `squ_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX`
- **How to get:** SonarCloud → My Account → Security → Generate Token
- **Permissions:** Analyze projects
- **Expiration:** 90 days (renew on YYYY-MM-DD)
- **Last updated:** 2024-12-15

### DOCKER_USERNAME (Optional)
- **Purpose:** Docker Hub authentication for image publishing
- **Format:** `username-string`
- **How to get:** Docker Hub → Account Settings
- **Last updated:** 2024-12-15

### DOCKER_PASSWORD (Optional)
- **Purpose:** Docker Hub access token
- **Format:** `dckr_pat_XXXXXX`
- **How to get:** Docker Hub → Account Settings → Security → Access Tokens
- **Permissions:** Read, Write
- **Last updated:** 2024-12-15

## Setup Instructions

See: docs/03-GITHUB-SECRETS-SETUP.md

## Troubleshooting

If workflows fail with authentication errors:
1. Check secret names match exactly (case-sensitive)
2. Verify token hasn't expired
3. Re-generate token and update secret
```

### 7. Secrets dans les Environnements Multi-Stages

**Si vous avez plusieurs environnements (dev, staging, prod) :**

```yaml
# Utiliser des Environment Secrets
deploy-staging:
  runs-on: ubuntu-latest
  environment: staging  # Utilise les secrets de l'environnement "staging"
  steps:
    - name: Deploy
      env:
        API_KEY: ${{ secrets.API_KEY }}  # API_KEY de staging
      run: deploy.sh

deploy-production:
  runs-on: ubuntu-latest
  environment: production  # Utilise les secrets de "production"
  steps:
    - name: Deploy
      env:
        API_KEY: ${{ secrets.API_KEY }}  # API_KEY de production (différent)
      run: deploy.sh
```

**Avantages :**
- Secrets différents par environnement
- Protection supplémentaire pour production (approbation manuelle)
- Traçabilité des déploiements

---

## 8. Dépannage

### Problème 1 : Secret Non Accessible dans le Workflow

**Symptôme :**
```yaml
- name: SonarCloud Scan
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: mvn sonar:sonar

# Erreur dans les logs :
[ERROR] Not authorized. Please check the properties sonar.login and sonar.password.
```

**Causes possibles :**

#### Cause A : Nom du Secret Incorrect

**Vérification :**
```
GitHub → Settings → Secrets and variables → Actions
→ Vérifier que le nom est exactement "SONAR_TOKEN"
```

**Solution :**
```
Si le secret s'appelle "SONARCLOUD_TOKEN" dans GitHub,
modifier le workflow :
  SONAR_TOKEN: ${{ secrets.SONARCLOUD_TOKEN }}
```

#### Cause B : Secret Pas Configuré

**Vérification :**
```
Workflow → Run details → Job → Step logs
Chercher : "WARNING: SONAR_TOKEN is empty"
```

**Solution :**
```
Créer le secret (voir section 4)
```

#### Cause C : Permissions Insuffisantes

**Vérification :**
```
Settings → Actions → General → Workflow permissions
```

**Solution :**
```
Sélectionner : "Read and write permissions"
Cliquer [Save]
```

### Problème 2 : Token Expiré

**Symptôme :**
```
[ERROR] Error during SonarScanner execution
[ERROR] User token has expired
```

**Solution :**
```
1. Aller sur SonarCloud → My Account → Security
2. Révoquer l'ancien token (si visible)
3. Générer un nouveau token
4. GitHub → Settings → Secrets → SONAR_TOKEN → Update
5. Coller le nouveau token
6. [Update secret]
7. Re-run le workflow
```

### Problème 3 : Secret Visible dans les Logs

**Symptôme :**
```
Logs GitHub Actions affichent :
[INFO] Token: squ_abc123xyz789...
```

**Cause :**
- Script custom qui echo le token
- GitHub ne masque pas toujours les formats personnalisés

**Solution Immédiate :**
```
1. RÉVOQUER LE TOKEN IMMÉDIATEMENT
   SonarCloud → My Account → Security → Revoke Token

2. Générer un nouveau token

3. Mettre à jour le secret GitHub

4. Modifier le workflow pour retirer l'echo

5. Vérifier que personne n'a copié le token
```

**Prévention :**
```yaml
# Ne jamais faire :
- run: echo "Token: ${{ secrets.SONAR_TOKEN }}"

# Toujours faire :
- run: echo "Token is configured: $([ -n "$SONAR_TOKEN" ] && echo 'YES' || echo 'NO')"
```

### Problème 4 : Workflow des Forks Ne Fonctionne Pas

**Symptôme :**
```
Fork d'un contributeur externe :
[ERROR] Not authorized

Mais ça fonctionne sur le repo principal
```

**Explication :**
```
Par défaut, GitHub NE donne PAS accès aux secrets pour les workflows
déclenchés par des forks (mesure de sécurité).
```

**Solution :**
```
Deux options :

Option A : Approuver manuellement chaque PR de fork
Settings → Actions → General
→ Fork pull request workflows from outside collaborators
→ Sélectionner "Require approval for first-time contributors"

Mainteneur doit cliquer "Approve and run" pour chaque PR

Option B : Exclure l'analyse SonarCloud pour les forks
- name: SonarCloud Scan
  if: github.event.pull_request.head.repo.full_name == github.repository
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: mvn sonar:sonar
```

### Problème 5 : Erreur "Resource not accessible by integration"

**Symptôme :**
```
Error: Resource not accessible by integration
```

**Cause :**
```
Le workflow essaie de faire une action nécessitant des permissions élevées
(ex: créer une release, modifier une PR) mais n'a pas les bonnes permissions.
```

**Solution :**
```yaml
# Ajouter les permissions explicites dans le workflow
jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read      # Lire le code
      pull-requests: write # Commenter les PRs
      checks: write       # Créer des checks
    steps:
      - name: Checkout
        uses: actions/checkout@v4
      # ...
```

### Problème 6 : Cache des Secrets

**Symptôme :**
```
Vous avez mis à jour un secret mais le workflow utilise encore l'ancienne valeur
```

**Cause :**
```
Rare, mais peut arriver si GitHub Actions a un problème de synchronisation.
```

**Solution :**
```
1. Attendre 5 minutes (propagation du secret)
2. Re-run le workflow
3. Si ça persiste :
   - Supprimer le secret
   - Attendre 1 minute
   - Re-créer le secret avec la nouvelle valeur
```

---

## Résumé : Checklist Complète

### Avant de Commencer

- [ ] Compte GitHub avec accès Admin au repository
- [ ] Compte SonarCloud créé et connecté à GitHub
- [ ] Projets SonarCloud créés (user-service, product-service, media-service, frontend)

### Configuration des Secrets

- [ ] Générer le token SonarCloud
- [ ] Copier le token dans un endroit sûr temporairement
- [ ] Créer le secret SONAR_TOKEN dans GitHub
- [ ] Vérifier que le secret apparaît dans la liste
- [ ] (Optionnel) Créer DOCKER_USERNAME et DOCKER_PASSWORD

### Configuration des Workflows

- [ ] Mettre à jour les pom.xml avec sonar.organization et sonar.projectKey
- [ ] Créer/modifier .github/workflows/ci-cd.yml
- [ ] Utiliser `${{ secrets.SONAR_TOKEN }}` dans le workflow
- [ ] Configurer les permissions du workflow (Read and write)

### Tests

- [ ] Créer un workflow de test temporaire
- [ ] Exécuter le workflow manuellement (workflow_dispatch)
- [ ] Vérifier que les secrets sont détectés
- [ ] Tester une analyse SonarCloud réelle
- [ ] Vérifier les résultats sur SonarCloud dashboard
- [ ] Supprimer le workflow de test (optionnel)

### Sécurité

- [ ] Vérifier que les secrets ne sont jamais loggés
- [ ] Configurer la protection de branche main
- [ ] Documenter les secrets dans .github/SECRETS.md
- [ ] Planifier la rotation des tokens (calendrier)
- [ ] Configurer les permissions pour les forks

### Finalisation

- [ ] Commit et push tous les changements
- [ ] Créer une Pull Request de test
- [ ] Vérifier que l'analyse SonarCloud fonctionne dans la PR
- [ ] Merger la PR
- [ ] Ajouter des badges dans README.md (optionnel)

---

**Document créé le** : 2025-12-15
**Auteur** : Documentation CI/CD Zone01
**Version** : 1.0
**Statut** : Complet