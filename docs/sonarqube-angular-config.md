# Configuration Angular pour SonarQube

## Configuration du projet Angular

### 1. Installer le scanner SonarQube

```bash
cd frontend
npm install --save-dev sonarqube-scanner
```

### 2. Créer le fichier de configuration

Créez le fichier `sonar-project.properties` à la racine du projet frontend :

**`frontend/sonar-project.properties`**

```properties
# ===================================
# SONARQUBE - FRONTEND CONFIGURATION
# ===================================

# Identification du projet
sonar.projectKey=ecommerce-frontend
sonar.projectName=E-Commerce Frontend
sonar.projectVersion=1.0

# Chemins sources
sonar.sources=src
sonar.tests=src
sonar.test.inclusions=**/*.spec.ts

# Exclusions (ne pas analyser)
sonar.exclusions=\
  **/node_modules/**,\
  **/*.spec.ts,\
  **/test/**,\
  **/dist/**,\
  **/build/**,\
  **/*.config.ts,\
  **/environments/**,\
  **/.angular/**,\
  **/coverage/**

# Exclusions de couverture
sonar.coverage.exclusions=\
  **/*.spec.ts,\
  **/test/**,\
  **/*.config.ts,\
  **/environments/**,\
  **/main.ts,\
  **/*.module.ts

# TypeScript
sonar.typescript.lcov.reportPaths=coverage/lcov.info

# Configuration
sonar.sourceEncoding=UTF-8

# Language
sonar.language=ts

# Ignorer les duplications dans les tests
sonar.cpd.exclusions=**/*.spec.ts
```

### 3. Ajouter les scripts NPM

Ajoutez ces scripts dans `package.json` :

```json
{
  "scripts": {
    "test": "ng test",
    "test:ci": "ng test --code-coverage --watch=false --browsers=ChromeHeadless",
    "sonar": "sonar-scanner",
    "analyze": "npm run test:ci && npm run sonar"
  }
}
```

## Utilisation

### Analyse complète (tests + SonarQube)

```bash
cd frontend

# Générer le coverage + analyser
npm run analyze
```

### Analyse sans relancer les tests

Si vous avez déjà les rapports de coverage :

```bash
cd frontend
npm run sonar
```

### Analyse manuelle avec paramètres

```bash
cd frontend

# 1. Générer le coverage
npm run test:ci

# 2. Analyser avec SonarQube
npx sonar-scanner \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=VOTRE_TOKEN
```

## Configuration Karma pour le coverage

Assurez-vous que votre fichier `karma.conf.js` est configuré pour générer le rapport LCOV :

```javascript
module.exports = function (config) {
  config.set({
    // ... autres configurations ...

    coverageReporter: {
      dir: require('path').join(__dirname, './coverage'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'text-summary' },
        { type: 'lcovonly' }  // Important pour SonarQube
      ]
    },

    // ... autres configurations ...
  });
};
```

## Structure des fichiers après configuration

```
frontend/
├── sonar-project.properties    # Configuration SonarQube
├── package.json                # Scripts NPM mis à jour
├── karma.conf.js               # Configuration Karma
├── coverage/                   # Rapports de couverture (généré)
│   └── lcov.info              # Rapport LCOV pour SonarQube
├── src/
│   ├── app/
│   └── ...
└── node_modules/
```

## Commandes utiles

### Générer uniquement le coverage

```bash
ng test --code-coverage --watch=false --browsers=ChromeHeadless
```

### Analyser avec un token personnalisé

```bash
npx sonar-scanner \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=VOTRE_TOKEN \
  -Dsonar.projectKey=ecommerce-frontend
```

### Mode debug

```bash
npx sonar-scanner -X
```

## Exclusions recommandées

### Fichiers à exclure de l'analyse

- **Configuration** : `*.config.ts`, `environments/**`
- **Tests** : `*.spec.ts`, `test/**`
- **Build** : `dist/**`, `.angular/**`, `node_modules/**`
- **Coverage** : `coverage/**`

### Fichiers à exclure du coverage

- **Tests** : `*.spec.ts`
- **Bootstrap** : `main.ts`
- **Modules** : `*.module.ts` (optionnel)
- **Environments** : `environments/**`

## Options avancées

### Analyser une branche spécifique

```bash
npx sonar-scanner \
  -Dsonar.branch.name=feature/my-feature
```

### Configurer les seuils de qualité

Dans l'interface web SonarQube :
1. Allez dans votre projet `ecommerce-frontend`
2. **Project Settings** > **Quality Gate**
3. Configurez les seuils :
   - Coverage minimum : 70%
   - Duplications maximum : 3%
   - Code Smells : A ou B

### Configuration TypeScript stricte

Si vous utilisez TypeScript strict, ajoutez dans `sonar-project.properties` :

```properties
sonar.typescript.tsconfigPath=tsconfig.json
```

## Intégration CI/CD

### GitHub Actions

```yaml
- name: Install dependencies
  run: |
    cd frontend
    npm ci

- name: Run tests with coverage
  run: |
    cd frontend
    npm run test:ci

- name: SonarQube Analysis
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: |
    cd frontend
    npx sonar-scanner \
      -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.token=$SONAR_TOKEN
```

### Jenkins

```groovy
stage('Frontend Analysis') {
    steps {
        dir('frontend') {
            sh 'npm ci'
            sh 'npm run test:ci'

            withSonarQubeEnv('SonarQube') {
                sh 'npx sonar-scanner'
            }
        }
    }
}
```

## Troubleshooting

### Erreur : "lcov.info not found"

**Solution :** Assurez-vous que les tests ont été exécutés avec `--code-coverage` :

```bash
ng test --code-coverage --watch=false --browsers=ChromeHeadless
```

### Erreur : "SonarQube server not found"

**Solution :** Vérifiez que SonarQube est démarré :

```bash
docker ps | grep sonarqube
curl http://localhost:9000/api/system/status
```

### Erreur : "Project not found"

**Solution :** Créez d'abord le projet dans l'interface web SonarQube.

### Coverage à 0%

**Vérifications :**
1. Le fichier `coverage/lcov.info` existe ?
2. Le chemin dans `sonar-project.properties` est correct ?
3. Les tests ont bien été exécutés avec `--code-coverage` ?

```bash
# Vérifier le fichier
ls -la coverage/

# Vérifier le contenu
head coverage/lcov.info
```

## Analyse de tous les projets

Script pour analyser backend + frontend :

```bash
#!/bin/bash
# analyze-all.sh

SONAR_TOKEN="votre_token_ici"

echo "=========================================="
echo "Analyzing Backend Services"
echo "=========================================="

for service in user-service product-service media-service; do
  echo "→ Analyzing $service..."
  cd backend/$service
  mvn clean verify sonar:sonar -Dsonar.token=$SONAR_TOKEN
  cd ../..
done

echo ""
echo "=========================================="
echo "Analyzing Frontend"
echo "=========================================="

cd frontend
npm run test:ci
npx sonar-scanner -Dsonar.token=$SONAR_TOKEN
cd ..

echo ""
echo "All analyses completed!"
echo "View results at: http://localhost:9000"
```

## Métriques importantes

### Ce que SonarQube analyse

- **Bugs** : Problèmes qui peuvent causer des erreurs
- **Vulnerabilities** : Failles de sécurité
- **Code Smells** : Problèmes de maintenabilité
- **Coverage** : Couverture de tests
- **Duplications** : Code dupliqué
- **Complexity** : Complexité cyclomatique

### Objectifs recommandés pour Angular

- **Coverage** : ≥ 70%
- **Duplications** : ≤ 3%
- **Maintainability Rating** : A ou B
- **Reliability Rating** : A
- **Security Rating** : A

---

📖 **Documentation complète** : [sonarqube-setup.md](./sonarqube-setup.md)