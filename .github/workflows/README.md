# GitHub Actions Workflows - SonarCloud Integration

Ce dossier contient les workflows GitHub Actions pour l'intégration continue et l'analyse de code avec SonarCloud.

## Vue d'ensemble

### Workflows disponibles

1. **`sonarqube-backend.yml`** - Analyse des microservices backend
2. **`sonarqube-frontend.yml`** - Analyse du frontend Angular
3. **`sonarqube-full.yml`** - Analyse complète de tout le projet

---

## 1. SonarCloud Backend Analysis

**Fichier:** `sonarqube-backend.yml`

### Déclencheurs
- Push sur la branche `main` (uniquement si `backend/**` modifié)
- Pull Request vers `main` (uniquement si `backend/**` modifié)

### Services analysés
- **user-service** (ecommerce-user-service)
- **product-service** (ecommerce-product-service)
- **media-service** (ecommerce-media-service)

### Fonctionnalités
- Analyse parallèle des 3 services (matrix strategy)
- Java 17 + Maven
- Cache Maven et SonarCloud pour optimisation
- Génération de rapports de couverture avec JaCoCo
- Vérification Quality Gate
- Upload des artifacts (rapports de couverture)
- Commentaire automatique sur les PR

### Optimisations
- `fail-fast: false` - Continue même si un service échoue
- Cache Maven partagé entre les builds
- Cache SonarCloud par service
- Exécution parallèle des 3 services

---

## 2. SonarCloud Frontend Analysis

**Fichier:** `sonarqube-frontend.yml`

### Déclencheurs
- Push sur la branche `main` (uniquement si `frontend/**` modifié)
- Pull Request vers `main` (uniquement si `frontend/**` modifié)

### Technologies
- Angular 20
- Node.js 20
- TypeScript 5.9

### Fonctionnalités
- Installation des dépendances npm
- Linting ESLint (optionnel)
- Tests avec couverture (Karma + ChromeHeadless)
- Build de production
- Analyse SonarCloud
- Vérification Quality Gate
- Upload des artifacts (coverage + build)
- Commentaire automatique sur les PR

### Optimisations
- Cache npm et node_modules
- Cache SonarCloud
- ChromeHeadless pour CI
- Build de production pour vérifier la compilation

---

## 3. SonarCloud Full Analysis

**Fichier:** `sonarqube-full.yml`

### Déclencheurs
- Push sur la branche `main` (tous les chemins)
- Pull Request vers `main` (tous les chemins)

### Architecture
Ce workflow est composé de 3 jobs orchestrés :

#### Job 1: Backend Analysis
- Analyse des 3 microservices en parallèle (matrix)
- Export des statuts vers le job summary

#### Job 2: Frontend Analysis
- Analyse du frontend Angular
- Export du statut vers le job summary

#### Job 3: Summary
- **Dépendances:** Attend backend-analysis et frontend-analysis
- Agrège tous les résultats
- Génère un tableau récapitulatif complet
- Poste un commentaire unique avec tous les résultats
- Échoue si au moins un Quality Gate a échoué

### Fonctionnalités avancées
- Commentaire PR mis à jour (plutôt que créer plusieurs commentaires)
- Tableau récapitulatif avec liens directs vers SonarCloud
- Statut global calculé
- Messages conditionnels selon succès/échec
- Gestion d'erreurs robuste avec `if: always()`

---

## Configuration requise

### Secrets GitHub

Vous devez configurer ces secrets dans votre dépôt GitHub :

**Settings → Secrets and variables → Actions → New repository secret**

| Secret | Description | Obligatoire |
|--------|-------------|-------------|
| `SONAR_TOKEN` | Token d'authentification SonarCloud | ✅ Oui |
| `GITHUB_TOKEN` | Token GitHub (fourni automatiquement) | ✅ Automatique |

### Obtenir le SONAR_TOKEN

1. Se connecter à [SonarCloud](https://sonarcloud.io)
2. Aller dans **My Account → Security**
3. Générer un nouveau token avec les permissions nécessaires
4. Copier le token (il ne sera plus visible après)
5. L'ajouter comme secret dans GitHub

### Configuration SonarCloud

#### Organisation
- **Nom:** `zone01-ecommerce`

#### Projets requis

Créer 4 projets dans SonarCloud :

| Service | Project Key | Project Name |
|---------|-------------|--------------|
| User Service | `ecommerce-user-service` | E-Commerce User Service |
| Product Service | `ecommerce-product-service` | E-Commerce Product Service |
| Media Service | `ecommerce-media-service` | E-Commerce Media Service |
| Frontend | `ecommerce-frontend` | E-Commerce Frontend |

#### Configuration recommandée

Pour chaque projet, configurer :
- **New Code Definition:** Previous version
- **Quality Gate:** Sonar way (ou personnalisé)
- **Branch Analysis:** main

---

## Utilisation

### Exécution automatique

Les workflows se déclenchent automatiquement :
- Sur push vers `main`
- Sur création/mise à jour de Pull Request vers `main`

### Exécution manuelle

Vous pouvez déclencher manuellement un workflow depuis GitHub :
1. Aller dans **Actions**
2. Sélectionner le workflow
3. Cliquer sur **Run workflow**

### Visualisation des résultats

#### Dans GitHub Actions
- Aller dans l'onglet **Actions**
- Sélectionner le workflow exécuté
- Voir les logs détaillés de chaque étape

#### Dans SonarCloud
- Accéder à [SonarCloud](https://sonarcloud.io/organizations/zone01-ecommerce)
- Sélectionner le projet
- Voir les métriques de qualité, bugs, vulnérabilités, code smells, etc.

#### Dans les Pull Requests
- Les résultats sont automatiquement postés en commentaire
- Tableau récapitulatif avec statuts et liens directs

---

## Artifacts

Les workflows génèrent des artifacts qui sont conservés pendant 30 jours :

### Backend
- `coverage-user-service` - Rapports JaCoCo pour user-service
- `coverage-product-service` - Rapports JaCoCo pour product-service
- `coverage-media-service` - Rapports JaCoCo pour media-service

### Frontend
- `coverage-frontend` - Rapports de couverture Angular (lcov, html)
- `frontend-build` - Build de production (7 jours)

### Téléchargement
1. Aller dans **Actions → Workflow run**
2. Descendre jusqu'à la section **Artifacts**
3. Télécharger l'artifact souhaité

---

## Maintenance et optimisation

### Cache

Les workflows utilisent plusieurs niveaux de cache :

| Type | Emplacement | Clé |
|------|-------------|-----|
| Maven | `~/.m2/repository` | Basé sur hash des pom.xml |
| npm | `~/.npm` + `node_modules` | Basé sur hash de package-lock.json |
| SonarCloud | `~/.sonar/cache` | Par service/frontend |

**Nettoyage :** Les caches sont automatiquement invalidés si les fichiers de dépendances changent.

### Optimisations appliquées

1. **Parallel execution** - Les services backend s'exécutent en parallèle
2. **Fail-fast: false** - Continue même si un service échoue
3. **Selective path triggers** - Ne s'exécute que si nécessaire
4. **Artifact retention** - 30 jours (configurable)
5. **Shallow clone avoidance** - `fetch-depth: 0` pour SonarCloud
6. **ChromeHeadless** - Tests frontend sans UI pour CI

### Temps d'exécution estimés

| Workflow | Durée estimée | Parallélisation |
|----------|---------------|-----------------|
| sonarqube-backend.yml | 5-8 min | Oui (3 services) |
| sonarqube-frontend.yml | 4-6 min | Non |
| sonarqube-full.yml | 6-10 min | Oui (tous) |

---

## Dépannage

### Erreur: "Quality Gate failed"

**Cause :** Le code ne respecte pas les standards SonarCloud

**Solution :**
1. Cliquer sur le lien "View Analysis" dans le commentaire PR
2. Identifier les issues (bugs, vulnerabilities, code smells)
3. Corriger les problèmes
4. Pusher les corrections

### Erreur: "SONAR_TOKEN not found"

**Cause :** Le secret SONAR_TOKEN n'est pas configuré

**Solution :**
1. Aller dans **Settings → Secrets → Actions**
2. Créer le secret `SONAR_TOKEN`
3. Relancer le workflow

### Erreur: "Project not found in SonarCloud"

**Cause :** Le projet n'existe pas dans SonarCloud

**Solution :**
1. Se connecter à SonarCloud
2. Créer le projet avec le bon Project Key
3. Configurer l'organisation `zone01-ecommerce`
4. Relancer le workflow

### Tests échouent localement mais passent en CI

**Cause :** Différences d'environnement

**Solution :**
```bash
# Backend - Utiliser les mêmes commandes que CI
cd backend/user-service
mvn clean verify -B

# Frontend - Utiliser ChromeHeadless
cd frontend
npm test -- --browsers=ChromeHeadless --no-watch
```

### Cache corrompu

**Solution :**
1. Aller dans **Actions → Caches**
2. Supprimer les caches problématiques
3. Relancer le workflow

---

## Structure des commentaires PR

### Backend individuel
```
✅ SonarCloud Analysis - User Service
Quality Gate: ✅ PASSED
Details:
- View Full Analysis
- Project Key: ecommerce-user-service
- Build: Workflow Run
```

### Frontend
```
✅ SonarCloud Analysis - Frontend (Angular)
Quality Gate: ✅ PASSED
Details:
- View Full Analysis
- Project Key: ecommerce-frontend
- Framework: Angular 20 with TypeScript
- Build Status: Tests ✅, Build ✅, Coverage Available
```

### Full Summary
```
🔍 SonarCloud Full Analysis Summary
Overall Status: ✅ PASSED

📊 Analysis Results
[Tableau avec tous les services]

🔗 Quick Links
- Organization
- Workflow Run
- Branch, Commit

✅ All Quality Gates Passed
```

---

## Ressources

### Documentation officielle
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [SonarCloud GitHub Action](https://github.com/SonarSource/sonarcloud-github-action)

### Liens utiles
- [SonarCloud Organization](https://sonarcloud.io/organizations/zone01-ecommerce)
- [Quality Gates](https://docs.sonarcloud.io/improving/quality-gates/)
- [Coverage](https://docs.sonarcloud.io/enriching/test-coverage/overview/)

### Support
Pour toute question ou problème :
1. Consulter les logs dans GitHub Actions
2. Vérifier les issues SonarCloud
3. Contacter l'équipe DevOps

---

**Dernière mise à jour:** 2025-12-15
**Mainteneur:** Zone01 E-Commerce Team