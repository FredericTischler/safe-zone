# GitHub Configuration & CI/CD

Ce dossier contient toute la configuration GitHub Actions et l'intégration SonarCloud pour le projet E-Commerce.

---

## Structure du dossier

```
.github/
├── README.md                        # Ce fichier
├── QUICKSTART.md                    # Guide de démarrage rapide (5 min)
├── SONARCLOUD_SETUP.md              # Configuration détaillée SonarCloud
└── workflows/
    ├── README.md                    # Documentation des workflows
    ├── sonarqube-backend.yml        # Analyse backend (3 services)
    ├── sonarqube-frontend.yml       # Analyse frontend (Angular)
    ├── sonarqube-full.yml           # Analyse complète du projet
    └── validate-config.sh           # Script de validation
```

---

## Démarrage rapide

### Pour démarrer en 5 minutes

```bash
# Suivre le guide de démarrage rapide
cat .github/QUICKSTART.md
```

### Pour une configuration complète

```bash
# Suivre le guide détaillé
cat .github/SONARCLOUD_SETUP.md
```

---

## Workflows disponibles

### 1. Backend Analysis (`sonarqube-backend.yml`)

**Description:** Analyse les 3 microservices backend en parallèle

**Services:**
- User Service (ecommerce-user-service)
- Product Service (ecommerce-product-service)
- Media Service (ecommerce-media-service)

**Déclenchement:**
- Push sur `main` si `backend/**` modifié
- Pull Request vers `main` si `backend/**` modifié

**Durée:** ~5-8 minutes

**Technologies:**
- Java 17
- Maven
- Spring Boot 3.2.0
- JaCoCo (coverage)

### 2. Frontend Analysis (`sonarqube-frontend.yml`)

**Description:** Analyse le frontend Angular

**Déclenchement:**
- Push sur `main` si `frontend/**` modifié
- Pull Request vers `main` si `frontend/**` modifié

**Durée:** ~4-6 minutes

**Technologies:**
- Angular 20
- Node.js 20
- TypeScript 5.9
- Karma + Jasmine (tests)

### 3. Full Analysis (`sonarqube-full.yml`)

**Description:** Analyse complète de tous les composants avec résumé global

**Déclenchement:**
- Push sur `main` (tous chemins)
- Pull Request vers `main` (tous chemins)

**Durée:** ~6-10 minutes

**Jobs:**
1. Backend (3 services en parallèle)
2. Frontend
3. Summary (agrégation et rapport)

---

## Fonctionnalités

### Analyse de code automatique

- ✅ Détection bugs et vulnerabilités
- ✅ Code smells et dette technique
- ✅ Couverture de tests (coverage)
- ✅ Code dupliqué
- ✅ Quality Gates personnalisables

### Intégration PR

- ✅ Commentaires automatiques sur les PR
- ✅ Statut des Quality Gates
- ✅ Liens directs vers SonarCloud
- ✅ Tableau récapitulatif complet

### Optimisations

- ✅ Cache Maven, npm et SonarCloud
- ✅ Exécution parallèle des services
- ✅ Path-based triggers (exécution sélective)
- ✅ Artifacts avec rapports de coverage
- ✅ Fail-fast désactivé (continue même si erreur)

### Gestion d'erreurs

- ✅ Timeout configurables
- ✅ Continue-on-error pour steps non-critiques
- ✅ Logs détaillés
- ✅ Messages d'erreur explicites

---

## Configuration requise

### Secrets GitHub

| Secret | Description | Requis |
|--------|-------------|--------|
| `SONAR_TOKEN` | Token SonarCloud pour authentification | ✅ Oui |
| `GITHUB_TOKEN` | Token GitHub (auto-généré) | ✅ Automatique |

### Permissions GitHub Actions

Dans **Settings → Actions → General → Workflow permissions:**
- ✅ Read and write permissions
- ✅ Allow GitHub Actions to create and approve pull requests

### Projets SonarCloud requis

| Project Key | Service | Status |
|-------------|---------|--------|
| `ecommerce-user-service` | User Service | À créer |
| `ecommerce-product-service` | Product Service | À créer |
| `ecommerce-media-service` | Media Service | À créer |
| `ecommerce-frontend` | Frontend Angular | À créer |

**Organisation:** `zone01-ecommerce`

---

## Utilisation

### Validation de la configuration

```bash
# Lancer le script de validation
cd /home/kheesi/Bureau/Zone01/Java/safe-zone
.github/workflows/validate-config.sh
```

### Déclencher un workflow manuellement

```bash
# Via GitHub CLI
gh workflow run sonarqube-full.yml

# Voir les workflows disponibles
gh workflow list

# Voir l'état d'exécution
gh run list --workflow=sonarqube-full.yml
```

### Voir les résultats

```bash
# Logs de la dernière exécution
gh run view --log

# Logs d'une exécution spécifique
gh run view <run-id> --log
```

### Tests locaux

```bash
# Backend - User Service
cd backend/user-service
mvn clean verify

# Frontend
cd frontend
npm test -- --no-watch --code-coverage
npm run build
```

---

## Exemples de commentaires PR

### Backend Service

```markdown
✅ SonarCloud Analysis - User Service
Quality Gate: ✅ PASSED

Details:
- 🔍 View Full Analysis
- 📊 Project Key: ecommerce-user-service
- 🏗️ Build: Workflow Run
```

### Full Summary

```markdown
🔍 SonarCloud Full Analysis Summary
Overall Status: ✅ PASSED

📊 Analysis Results
| Component | Status | Quality Gate | Details |
|-----------|--------|--------------|---------|
| User Service | ✅ | PASSED | View Analysis |
| Product Service | ✅ | PASSED | View Analysis |
| Media Service | ✅ | PASSED | View Analysis |
| Frontend | ✅ | PASSED | View Analysis |

✅ All Quality Gates Passed
Great work! The code meets all quality standards.
```

---

## Métriques SonarCloud

### Métriques principales

- **Bugs:** Erreurs de code qui peuvent causer des problèmes
- **Vulnerabilities:** Failles de sécurité
- **Code Smells:** Problèmes de maintenabilité
- **Coverage:** Pourcentage de code testé
- **Duplications:** Code dupliqué
- **Security Hotspots:** Points sensibles à reviewer

### Quality Gates par défaut

- 0 nouveaux bugs
- 0 nouvelles vulnerabilités
- Couverture ≥ 80% sur nouveau code
- Code dupliqué ≤ 3%
- Security Hotspots 100% reviewés

---

## Artifacts générés

### Backend Services

**Location:** Actions → Workflow → Artifacts

- `coverage-user-service` (30 jours)
  - JaCoCo HTML reports
  - jacoco.xml
  - Surefire reports

- `coverage-product-service` (30 jours)
- `coverage-media-service` (30 jours)

### Frontend

- `coverage-frontend` (30 jours)
  - lcov.info
  - HTML coverage reports
  - Karma reports

- `frontend-build` (7 jours)
  - Production build
  - dist/ folder

---

## Maintenance

### Mise à jour des versions

#### Workflows
```yaml
# Dans les fichiers .yml
uses: actions/checkout@v4       # Vérifier dernière version
uses: actions/setup-java@v4     # Vérifier dernière version
uses: actions/setup-node@v4     # Vérifier dernière version
```

#### Dependencies
```bash
# Backend - Vérifier versions Maven
mvn versions:display-dependency-updates

# Frontend - Vérifier versions npm
npm outdated
```

### Nettoyage des caches

```bash
# Lister les caches
gh cache list

# Supprimer un cache spécifique
gh cache delete <cache-key>

# Supprimer tous les caches (attention!)
gh cache list | awk '{print $1}' | xargs -I {} gh cache delete {}
```

### Monitoring

```bash
# Voir l'historique des workflows
gh run list --limit 50

# Voir les workflows échoués
gh run list --status failure

# Statistiques de temps d'exécution
gh run list --json conclusion,durationMs,name --jq '.[] | "\(.name): \(.durationMs/1000)s"'
```

---

## Dépannage rapide

### Le workflow ne se déclenche pas

```bash
# Vérifier que le workflow est activé
gh workflow view sonarqube-full.yml

# Activer le workflow
gh workflow enable sonarqube-full.yml

# Vérifier les paths modifiés
git diff --name-only origin/main
```

### Erreur SONAR_TOKEN

```bash
# Vérifier le secret
gh secret list | grep SONAR_TOKEN

# Régénérer et mettre à jour
# 1. Aller sur SonarCloud → My Account → Security
# 2. Générer nouveau token
# 3. Mettre à jour sur GitHub
gh secret set SONAR_TOKEN
```

### Quality Gate échoue

1. Cliquer sur le lien "View Analysis" dans le commentaire
2. Identifier les issues sur SonarCloud
3. Corriger les problèmes
4. Push les corrections
5. Le workflow se relance automatiquement

### Tests échouent en CI

```bash
# Backend - reproduire l'environnement CI
mvn clean verify -B -DskipTests=false

# Frontend - utiliser ChromeHeadless comme CI
npm test -- --no-watch --browsers=ChromeHeadless
```

---

## Performance et optimisation

### Temps d'exécution typiques

| Workflow | Sans cache | Avec cache | Parallèle |
|----------|-----------|------------|-----------|
| Backend | 8-10 min | 5-7 min | 3 services |
| Frontend | 6-8 min | 4-5 min | 1 service |
| Full | 12-15 min | 7-10 min | Tous |

### Recommandations

1. **Path-based triggers:** Les workflows ne s'exécutent que si nécessaire
2. **Cache layers:** Maven, npm, et SonarCloud sont cachés
3. **Parallel execution:** Services backend en parallèle
4. **Fail-fast disabled:** Continue même si un service échoue
5. **Artifacts retention:** 7-30 jours selon importance

---

## Ressources

### Documentation

- [QUICKSTART.md](./QUICKSTART.md) - Démarrer en 5 minutes
- [SONARCLOUD_SETUP.md](./SONARCLOUD_SETUP.md) - Configuration complète
- [workflows/README.md](./workflows/README.md) - Documentation workflows

### Liens externes

- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [SonarCloud GitHub Action](https://github.com/marketplace/actions/sonarcloud-scan)

### Liens SonarCloud

- [Organisation](https://sonarcloud.io/organizations/zone01-ecommerce)
- [User Service](https://sonarcloud.io/project/overview?id=ecommerce-user-service)
- [Product Service](https://sonarcloud.io/project/overview?id=ecommerce-product-service)
- [Media Service](https://sonarcloud.io/project/overview?id=ecommerce-media-service)
- [Frontend](https://sonarcloud.io/project/overview?id=ecommerce-frontend)

---

## Support

### Problème de configuration?

```bash
# 1. Valider la configuration
.github/workflows/validate-config.sh

# 2. Consulter la documentation
cat .github/SONARCLOUD_SETUP.md

# 3. Voir les logs des workflows
gh run view --log
```

### Besoin d'aide?

1. Consulter les workflows GitHub Actions
2. Vérifier les logs SonarCloud
3. Consulter la documentation complète
4. Contacter l'équipe DevOps

---

## Statut

| Composant | Version | Status | Dernière vérification |
|-----------|---------|--------|----------------------|
| Backend Workflow | 1.0 | ✅ Production Ready | 2025-12-15 |
| Frontend Workflow | 1.0 | ✅ Production Ready | 2025-12-15 |
| Full Workflow | 1.0 | ✅ Production Ready | 2025-12-15 |
| SonarCloud Integration | Active | ✅ Configured | 2025-12-15 |

---

**Maintenu par:** Zone01 E-Commerce DevOps Team
**Dernière mise à jour:** 2025-12-15
**Version:** 1.0.0