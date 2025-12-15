# Configuration Maven pour SonarQube

## Configuration à ajouter au pom.xml de chaque microservice

Ajoutez cette configuration à chaque `pom.xml` de vos microservices Java.

### 1. Propriétés

```xml
<properties>
    <!-- Propriétés existantes... -->
    <java.version>17</java.version>

    <!-- Configuration SonarQube -->
    <sonar.host.url>http://localhost:9000</sonar.host.url>
    <sonar.projectKey>ecommerce-user-service</sonar.projectKey>
    <sonar.projectName>E-Commerce User Service</sonar.projectName>
    <sonar.java.source>17</sonar.java.source>
    <sonar.java.binaries>target/classes</sonar.java.binaries>
    <sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>

    <!-- Version des plugins -->
    <jacoco.version>0.8.11</jacoco.version>
    <sonar-maven-plugin.version>4.0.0.4121</sonar-maven-plugin.version>
</properties>
```

**⚠️ Important** : Modifiez `sonar.projectKey` et `sonar.projectName` selon le service :
- `ecommerce-user-service` → E-Commerce User Service
- `ecommerce-product-service` → E-Commerce Product Service
- `ecommerce-media-service` → E-Commerce Media Service

### 2. Plugins Maven

```xml
<build>
    <plugins>
        <!-- Plugins existants... -->

        <!-- Plugin JaCoCo pour la couverture de code -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>${jacoco.version}</version>
            <executions>
                <!-- Préparer l'agent JaCoCo -->
                <execution>
                    <id>prepare-agent</id>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <!-- Générer le rapport après les tests -->
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <!-- Vérifier les seuils de couverture (optionnel) -->
                <execution>
                    <id>jacoco-check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>PACKAGE</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.50</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        <!-- Plugin SonarQube Maven -->
        <plugin>
            <groupId>org.sonarsource.scanner.maven</groupId>
            <artifactId>sonar-maven-plugin</artifactId>
            <version>${sonar-maven-plugin.version}</version>
        </plugin>
    </plugins>
</build>
```

## Exemples de configuration complète par service

### User Service

```xml
<properties>
    <sonar.projectKey>ecommerce-user-service</sonar.projectKey>
    <sonar.projectName>E-Commerce User Service</sonar.projectName>

    <!-- Exclusions spécifiques -->
    <sonar.exclusions>
        **/config/**,
        **/dto/**,
        **/entity/**,
        **/*Application.java
    </sonar.exclusions>

    <!-- Ignorer certains packages pour la couverture -->
    <sonar.coverage.exclusions>
        **/config/**,
        **/dto/**,
        **/entity/**,
        **/*Application.java
    </sonar.coverage.exclusions>
</properties>
```

### Product Service

```xml
<properties>
    <sonar.projectKey>ecommerce-product-service</sonar.projectKey>
    <sonar.projectName>E-Commerce Product Service</sonar.projectName>

    <sonar.exclusions>
        **/config/**,
        **/dto/**,
        **/entity/**,
        **/*Application.java
    </sonar.exclusions>

    <sonar.coverage.exclusions>
        **/config/**,
        **/dto/**,
        **/entity/**,
        **/*Application.java
    </sonar.coverage.exclusions>
</properties>
```

### Media Service

```xml
<properties>
    <sonar.projectKey>ecommerce-media-service</sonar.projectKey>
    <sonar.projectName>E-Commerce Media Service</sonar.projectName>

    <sonar.exclusions>
        **/config/**,
        **/dto/**,
        **/entity/**,
        **/*Application.java
    </sonar.exclusions>

    <sonar.coverage.exclusions>
        **/config/**,
        **/dto/**,
        **/entity/**,
        **/*Application.java
    </sonar.coverage.exclusions>
</properties>
```

## Commandes Maven

### Analyse simple

```bash
mvn clean verify sonar:sonar
```

### Analyse avec token (recommandé)

```bash
mvn clean verify sonar:sonar \
  -Dsonar.token=VOTRE_TOKEN
```

### Analyse sans exécuter les tests (si déjà exécutés)

```bash
mvn sonar:sonar \
  -Dsonar.token=VOTRE_TOKEN
```

### Analyser tous les microservices

```bash
#!/bin/bash
# Script pour analyser tous les services

SERVICES=("user-service" "product-service" "media-service")
SONAR_TOKEN="votre_token_ici"

for service in "${SERVICES[@]}"; do
  echo "==================================="
  echo "Analyzing $service..."
  echo "==================================="

  cd backend/$service

  mvn clean verify sonar:sonar \
    -Dsonar.token=$SONAR_TOKEN

  if [ $? -eq 0 ]; then
    echo "✓ $service analyzed successfully"
  else
    echo "✗ $service analysis failed"
  fi

  cd ../..
  echo ""
done

echo "All analyses completed!"
```

## Options avancées

### Skip les tests lors de l'analyse

```bash
mvn sonar:sonar -Dmaven.test.skip=true
```

### Analyser une branche spécifique

```bash
mvn sonar:sonar \
  -Dsonar.branch.name=feature/my-feature
```

### Mode debug

```bash
mvn sonar:sonar -X
```

## Qualité du code

### Configurer les seuils de couverture

Dans `pom.xml`, section JaCoCo :

```xml
<configuration>
    <rules>
        <rule>
            <element>BUNDLE</element>
            <limits>
                <limit>
                    <counter>LINE</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.70</minimum> <!-- 70% minimum -->
                </limit>
                <limit>
                    <counter>BRANCH</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.60</minimum> <!-- 60% minimum -->
                </limit>
            </limits>
        </rule>
    </rules>
</configuration>
```

## Exclusions recommandées

```xml
<properties>
    <!-- Ne pas analyser ces fichiers -->
    <sonar.exclusions>
        **/config/**,
        **/dto/**,
        **/entity/**,
        **/model/**,
        **/*Application.java,
        **/*Config.java,
        **/*Properties.java
    </sonar.exclusions>

    <!-- Ne pas calculer la couverture sur ces fichiers -->
    <sonar.coverage.exclusions>
        **/config/**,
        **/dto/**,
        **/entity/**,
        **/model/**,
        **/*Application.java,
        **/*Config.java,
        **/*Properties.java
    </sonar.coverage.exclusions>

    <!-- Ignorer les fichiers de test pour l'analyse de duplication -->
    <sonar.cpd.exclusions>
        **/*Test.java,
        **/*IT.java
    </sonar.cpd.exclusions>
</properties>
```

## Intégration CI/CD

### GitHub Actions

```yaml
- name: Analyze with SonarQube
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: |
    mvn clean verify sonar:sonar \
      -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.token=$SONAR_TOKEN
```

### Jenkins

```groovy
stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('SonarQube') {
            sh 'mvn clean verify sonar:sonar'
        }
    }
}
```

---

📖 **Documentation complète** : [sonarqube-setup.md](./sonarqube-setup.md)