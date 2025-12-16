# Guide de démarrage rapide - SonarCloud CI/CD

Configuration rapide en 5 minutes pour activer l'analyse SonarCloud avec GitHub Actions.

---

## Checklist de configuration (5 min)

### 1. Créer l'organisation SonarCloud (1 min)

```bash
# Aller sur https://sonarcloud.io
# Se connecter avec GitHub
# Créer organisation: zone01-ecommerce
# Autoriser l'accès au repository safe-zone
```

### 2. Créer les 4 projets SonarCloud (2 min)

| Service | Project Key | Action |
|---------|-------------|--------|
| User Service | `ecommerce-user-service` | New Project → Import from GitHub |
| Product Service | `ecommerce-product-service` | New Project → Import from GitHub |
| Media Service | `ecommerce-media-service` | New Project → Import from GitHub |
| Frontend | `ecommerce-frontend` | New Project → Import from GitHub |

**Pour chaque projet:**
- Désactiver "Automatic Analysis"
- Sélectionner "GitHub Actions" comme méthode

### 3. Générer le token SonarCloud (30 sec)

```bash
# Sur SonarCloud:
# Avatar → My Account → Security → Generate Token
# Name: GitHub-Actions-Ecommerce
# Type: Global Analysis Token
# Copier le token généré
```

### 4. Ajouter le secret GitHub (30 sec)

```bash
# Sur GitHub Repository:
# Settings → Secrets and variables → Actions → New secret
# Name: SONAR_TOKEN
# Value: [Coller le token SonarCloud]
# Add secret
```

### 5. Activer les permissions GitHub Actions (30 sec)

```bash
# Settings → Actions → General → Workflow permissions
# Sélectionner: "Read and write permissions"
# Cocher: "Allow GitHub Actions to create and approve pull requests"
# Save
```

### 6. Validation (30 sec)

```bash
cd /home/kheesi/Bureau/Zone01/Java/safe-zone
.github/workflows/validate-config.sh
```

---

## Test rapide

### Créer une Pull Request de test

```bash
# Créer une branche de test
git checkout -b test/sonarcloud-integration

# Faire une modification mineure
echo "# Test SonarCloud" >> README.md

# Commit et push
git add README.md
git commit -m "test: validate SonarCloud integration"
git push origin test/sonarcloud-integration

# Créer la PR sur GitHub
gh pr create --title "Test SonarCloud Integration" \
             --body "Testing automated SonarCloud analysis"
```

### Vérifier l'exécution

1. Aller sur GitHub → Actions
2. Voir les workflows en cours d'exécution
3. Attendre 5-10 minutes
4. Vérifier les commentaires sur la PR
5. Vérifier les résultats sur SonarCloud

---

## Workflows disponibles

### 1. Backend uniquement
Déclenché automatiquement si modification dans `backend/**`

```bash
# Analyser seulement les services backend
git add backend/
git commit -m "feat: update backend services"
git push
```

### 2. Frontend uniquement
Déclenché automatiquement si modification dans `frontend/**`

```bash
# Analyser seulement le frontend
git add frontend/
git commit -m "feat: update frontend"
git push
```

### 3. Analyse complète
Déclenché automatiquement sur toute modification

```bash
# Analyser tout le projet
git add .
git commit -m "feat: update project"
git push
```

---

## Commandes utiles

### Validation locale

```bash
# Backend - User Service
cd backend/user-service
mvn clean verify

# Frontend
cd frontend
npm test -- --no-watch --code-coverage
npm run build
```

### Lancer un workflow manuellement

```bash
# Via GitHub CLI
gh workflow run sonarqube-full.yml

# Voir les workflows disponibles
gh workflow list

# Voir l'historique d'exécution
gh run list --workflow=sonarqube-full.yml
```

### Voir les logs d'exécution

```bash
# Dernière exécution
gh run view

# Exécution spécifique
gh run view <run-id> --log
```

### Gérer les secrets

```bash
# Lister les secrets
gh secret list

# Ajouter/modifier un secret
gh secret set SONAR_TOKEN

# Supprimer un secret
gh secret remove SONAR_TOKEN
```

---

## Résolution rapide des problèmes

### Workflow ne se déclenche pas

```bash
# Vérifier les paths dans le workflow
cat .github/workflows/sonarqube-backend.yml | grep "paths:"

# Vérifier le statut des workflows
gh workflow list

# Activer le workflow si désactivé
gh workflow enable sonarqube-full.yml
```

### Quality Gate échoue

```bash
# Voir les détails sur SonarCloud
# https://sonarcloud.io/organizations/zone01-ecommerce

# Vérifier localement
cd backend/user-service
mvn clean verify sonar:sonar \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=zone01-ecommerce \
  -Dsonar.projectKey=ecommerce-user-service \
  -Dsonar.token=$SONAR_TOKEN
```

### Tests échouent en CI

```bash
# Backend - tester avec les mêmes options que CI
mvn clean verify -B -DskipTests=false

# Frontend - tester avec ChromeHeadless
npm test -- --no-watch --browsers=ChromeHeadless
```

### Erreur "SONAR_TOKEN not found"

```bash
# Vérifier le secret
gh secret list | grep SONAR_TOKEN

# Re-créer le secret si manquant
gh secret set SONAR_TOKEN
# Coller le token SonarCloud
```

---

## Liens rapides

### SonarCloud
- [Organisation](https://sonarcloud.io/organizations/zone01-ecommerce)
- [User Service](https://sonarcloud.io/project/overview?id=ecommerce-user-service)
- [Product Service](https://sonarcloud.io/project/overview?id=ecommerce-product-service)
- [Media Service](https://sonarcloud.io/project/overview?id=ecommerce-media-service)
- [Frontend](https://sonarcloud.io/project/overview?id=ecommerce-frontend)

### GitHub
- [Actions](https://github.com/kheesi/safe-zone/actions)
- [Settings → Secrets](https://github.com/kheesi/safe-zone/settings/secrets/actions)
- [Workflows](https://github.com/kheesi/safe-zone/actions/workflows)

### Documentation
- [Configuration complète](./SONARCLOUD_SETUP.md)
- [Documentation workflows](./workflows/README.md)
- [SonarCloud Docs](https://docs.sonarcloud.io/)

---

## Support

### Problème avec la configuration?

1. Lancer le script de validation:
   ```bash
   .github/workflows/validate-config.sh
   ```

2. Consulter la documentation complète:
   ```bash
   cat .github/SONARCLOUD_SETUP.md
   ```

3. Vérifier les workflows:
   ```bash
   cat .github/workflows/README.md
   ```

### Besoin d'aide?

- Consulter les logs GitHub Actions
- Vérifier les issues sur SonarCloud
- Contacter l'équipe DevOps

---

**Dernière mise à jour:** 2025-12-15
**Version:** 1.0
**Prêt en production:** ✅ Oui