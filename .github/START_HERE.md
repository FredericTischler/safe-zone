# COMMENCEZ ICI - Guide visuel

Guide ultra-simplifié pour démarrer avec SonarCloud CI/CD en 10 minutes.

---

## Vous êtes nouveau? Suivez ces 3 étapes

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  ÉTAPE 1: Configuration (5 min)                            │
│  ├─ Créer compte SonarCloud                                │
│  ├─ Créer organisation: zone01-ecommerce                   │
│  ├─ Créer 4 projets                                        │
│  ├─ Générer token                                          │
│  └─ Ajouter secret GitHub                                  │
│                                                             │
│  📖 Guide: QUICKSTART.md                                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  ÉTAPE 2: Validation (2 min)                               │
│  ├─ Lancer script de validation                            │
│  └─ Vérifier que tout est OK                               │
│                                                             │
│  💻 Commande: ./github/workflows/validate-config.sh        │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  ÉTAPE 3: Test (3 min)                                     │
│  ├─ Créer une Pull Request test                            │
│  ├─ Voir les workflows s'exécuter                          │
│  └─ Vérifier les résultats                                 │
│                                                             │
│  📖 Guide: QUICKSTART.md (section "Test rapide")           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Que faire selon votre rôle?

### Je suis ADMIN / DEVOPS

```
1. Configuration SonarCloud (30 min)
   📖 Lire: SONARCLOUD_SETUP.md

2. Configuration GitHub (5 min)
   📖 Lire: QUICKSTART.md

3. Validation complète
   💻 Lancer: validate-config.sh

4. Documentation workflows
   📖 Lire: workflows/README.md
```

### Je suis DÉVELOPPEUR

```
1. Comprendre le système (10 min)
   📖 Lire: README.md

2. Tests locaux (15 min)
   📖 Lire: LOCAL_TESTING.md

3. Premier test
   💻 Créer une PR test
   👀 Observer les workflows
   ✅ Corriger si Quality Gate échoue
```

### Je veux JUSTE COMPRENDRE

```
1. Vue d'ensemble rapide (5 min)
   📖 Lire: README.md (sections 1-3)

2. Navigation complète
   📖 Lire: INDEX.md

3. Parcourir selon besoin
   📖 Choisir dans INDEX.md
```

---

## Arbre de décision rapide

```
┌─────────────────────────────────────────┐
│  C'est votre première fois?             │
└──────────────┬──────────────────────────┘
               │
               ├─ OUI ──> QUICKSTART.md (5 min)
               │
               └─ NON ──> Voir ci-dessous
                          │
                          ├─ Besoin aide configuration? ──> SONARCLOUD_SETUP.md
                          │
                          ├─ Besoin tester en local? ──> LOCAL_TESTING.md
                          │
                          ├─ Besoin comprendre workflows? ──> workflows/README.md
                          │
                          ├─ Problème? ──> README.md (section Dépannage)
                          │
                          └─ Référence complète? ──> INDEX.md
```

---

## Les 3 workflows en image

### 1. Backend Analysis

```
┌──────────────────────────────────────────────────────────┐
│ Déclenché quand: Modification dans backend/**           │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐        │
│  │   User     │  │  Product   │  │   Media    │        │
│  │  Service   │  │  Service   │  │  Service   │        │
│  └──────┬─────┘  └──────┬─────┘  └──────┬─────┘        │
│         │               │               │               │
│         ├─ Build + Test ├───────────────┤               │
│         ├─ Coverage     ├───────────────┤               │
│         ├─ SonarCloud   ├───────────────┤               │
│         └─ Quality Gate ┘───────────────┘               │
│                                                          │
│  Résultat: 3 commentaires PR (un par service)           │
│  Durée: 5-8 min                                          │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### 2. Frontend Analysis

```
┌──────────────────────────────────────────────────────────┐
│ Déclenché quand: Modification dans frontend/**          │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  ┌────────────────────────────────────────┐             │
│  │         Frontend (Angular)             │             │
│  └────────────┬───────────────────────────┘             │
│               │                                          │
│               ├─ npm install                             │
│               ├─ Lint (optionnel)                        │
│               ├─ Tests + Coverage                        │
│               ├─ Build production                        │
│               ├─ SonarCloud                              │
│               └─ Quality Gate                            │
│                                                          │
│  Résultat: 1 commentaire PR                              │
│  Durée: 4-6 min                                          │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### 3. Full Analysis

```
┌──────────────────────────────────────────────────────────┐
│ Déclenché quand: N'importe quelle modification          │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  JOB 1: Backend (parallèle)                              │
│  ┌────────┐  ┌────────┐  ┌────────┐                     │
│  │  User  │  │Product │  │ Media  │                     │
│  └───┬────┘  └───┬────┘  └───┬────┘                     │
│      └───────────┴───────────┘                           │
│                  │                                       │
│  JOB 2: Frontend │                                       │
│  ┌───────────────┴──────┐                                │
│  │     Frontend         │                                │
│  └──────────┬───────────┘                                │
│             │                                            │
│  JOB 3: Summary                                          │
│  ┌──────────┴───────────────────────────┐                │
│  │ ✅ Agrégation des résultats          │                │
│  │ 📊 Tableau récapitulatif             │                │
│  │ 💬 Commentaire PR unique complet     │                │
│  │ ❌ Fail si au moins un QG échoue     │                │
│  └──────────────────────────────────────┘                │
│                                                          │
│  Résultat: 1 commentaire PR avec tout                    │
│  Durée: 6-10 min                                         │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## Exemple de commentaire PR (Full Analysis)

```markdown
🔍 SonarCloud Full Analysis Summary

Overall Status: ✅ PASSED

📊 Analysis Results
┌─────────────────┬────────┬──────────────┬─────────┐
│ Component       │ Status │ Quality Gate │ Details │
├─────────────────┼────────┼──────────────┼─────────┤
│ User Service    │   ✅   │   PASSED     │  [View] │
│ Product Service │   ✅   │   PASSED     │  [View] │
│ Media Service   │   ✅   │   PASSED     │  [View] │
│ Frontend        │   ✅   │   PASSED     │  [View] │
└─────────────────┴────────┴──────────────┴─────────┘

✅ All Quality Gates Passed
Great work! The code meets all quality standards.
```

---

## Commandes les plus utilisées

### Pour ADMINS

```bash
# 1. Validation configuration
.github/workflows/validate-config.sh

# 2. Vérifier secrets GitHub
gh secret list

# 3. Ajouter secret
gh secret set SONAR_TOKEN

# 4. Voir workflows
gh workflow list

# 5. Déclencher workflow
gh workflow run sonarqube-full.yml
```

### Pour DÉVELOPPEURS

```bash
# 1. Test backend local
cd backend/user-service
mvn clean verify

# 2. Test frontend local
cd frontend
npm test -- --no-watch --code-coverage

# 3. Analyse locale backend
mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN

# 4. Analyse locale frontend
sonar-scanner -Dsonar.token=$SONAR_TOKEN

# 5. Voir logs workflow
gh run view --log
```

---

## FAQ Ultra-rapide

### Workflow ne se déclenche pas?
```bash
# Vérifier qu'il est activé
gh workflow view sonarqube-full.yml

# L'activer si nécessaire
gh workflow enable sonarqube-full.yml
```

### Quality Gate échoue?
```
1. Cliquer sur "View Analysis" dans le commentaire PR
2. Identifier les issues sur SonarCloud
3. Corriger les problèmes
4. Push → Le workflow se relance automatiquement
```

### Erreur SONAR_TOKEN?
```bash
# Vérifier le secret
gh secret list | grep SONAR_TOKEN

# Le recréer si manquant
gh secret set SONAR_TOKEN
# Coller le token quand demandé
```

### Tests échouent?
```bash
# Backend - tester localement avec mêmes options que CI
cd backend/user-service
mvn clean verify -B -DskipTests=false

# Frontend - utiliser ChromeHeadless comme CI
cd frontend
npm test -- --no-watch --browsers=ChromeHeadless
```

---

## Prochaine étape

### Vous êtes prêt? Choisissez votre parcours:

**Parcours RAPIDE (5 min)**
```bash
cat .github/QUICKSTART.md
```

**Parcours COMPLET (30 min)**
```bash
cat .github/SONARCLOUD_SETUP.md
```

**Parcours DÉVELOPPEUR (15 min)**
```bash
cat .github/LOCAL_TESTING.md
```

**Parcours RÉFÉRENCE (navigation libre)**
```bash
cat .github/INDEX.md
```

---

## Structure des fichiers (si perdu)

```
.github/
├── START_HERE.md           ⭐ Vous êtes ici
├── QUICKSTART.md           🚀 Démarrage 5 min
├── README.md               📚 Vue d'ensemble complète
├── SONARCLOUD_SETUP.md     ⚙️ Configuration détaillée
├── LOCAL_TESTING.md        🧪 Tests locaux
├── INDEX.md                📑 Navigation complète
├── FILES_CREATED.md        📝 Liste tous les fichiers
└── workflows/
    ├── README.md                    📖 Doc workflows
    ├── sonarqube-backend.yml        🔧 Backend
    ├── sonarqube-frontend.yml       🎨 Frontend
    ├── sonarqube-full.yml           🔄 Complet
    └── validate-config.sh           ✅ Validation
```

---

## Besoin d'aide?

### Documentation
1. [QUICKSTART.md](./QUICKSTART.md) - Démarrage rapide
2. [README.md](./README.md) - Vue d'ensemble
3. [INDEX.md](./INDEX.md) - Navigation complète

### Support
- GitHub Actions logs: `gh run view --log`
- Validation config: `.github/workflows/validate-config.sh`
- SonarCloud: https://sonarcloud.io/organizations/zone01-ecommerce

---

## Statut

```
┌────────────────────────────────────────┐
│ ✅ Workflows créés                     │
│ ✅ Documentation complète              │
│ ✅ Scripts validation opérationnels    │
│ ✅ Prêt pour production                │
│                                        │
│ ⏳ Configuration SonarCloud (à faire)  │
│ ⏳ Configuration GitHub (à faire)      │
│ ⏳ Tests PR (à faire)                  │
└────────────────────────────────────────┘
```

**Action suivante:** Lire [QUICKSTART.md](./QUICKSTART.md)

---

**Dernière mise à jour:** 2025-12-15
**Version:** 1.0.0
**Pour:** Débutants et utilisateurs pressés