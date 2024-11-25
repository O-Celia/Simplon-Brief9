# **Automatisation du déploiement de Jenkins avec Docker, automatisation du déploiement de l'application du Brief 8 en canary dans la pipeline Jenkins, déploiement d'outils de sécurité (OWASP et SonarCloud) et test de charge avec Bash** <br/>

Ce projet vise à mettre en place une infrastructure pour un cluster Kubernetes AKS, déployer une application avec Jenkins en suivant un pipeline CI/CD, et intégrer des outils de sécurité pour garantir la qualité et la fiabilité du code. La mise en place complète est décrite dans l'Executive SUmmary.

## Mise en place

### 1. Déploiement du Cluster AKS

Création d'un groupe de ressources Azure. <br/>
Mise en place d’un cluster AKS avec un nœud, intégration de l'identité managée et configuration d'un Application Gateway pour l'ingress.<br/> 
Ajout de deux namespaces distincts : qal et prod pour séparer les environnements de déploiement.

### 2. Installation de Jenkins avec Docker

Construction d'une image personnalisée Jenkins à l'aide d'un Dockerfile intégrant des outils comme SonarScanner, Node.js, et des utilitaires supplémentaires. <br/>
Déploiement de Jenkins avec docker-compose pour une gestion simplifiée des conteneurs. <br/>
Configuration de l’accès via une interface web locale.

### 3. Configuration de Jenkins

Installation des plugins essentiels pour le CI/CD (Pipeline, Docker, GitHub, Workspace Cleanup, etc.). <br/>
Ajout de credentials nécessaires pour :
- Connexion à DockerHub.
- Intégration avec SonarCloud.

### 4. Configuration DNS de l’application

Création d'un enregistrement DNS pour rendre l'application accessible via un nom de domaine convivial. <br/>
Utilisation de Gandi pour configurer le DNS et associer l'IP publique de l'application.

### 5. Mise en place du Pipeline Jenkins

Étapes du pipeline :
- Nettoyage des environnements pour éviter les conflits résiduels.
- Clonage du dépôt GitHub contenant le code source de l'application.
- Construction de l'image Docker pour l'application.
- Publication de l'image sur DockerHub après authentification.
- Déploiement sur Kubernetes avec mise à jour automatique des tags.
- Tests de charge sur le site web déployé.
- Analyse de sécurité avec Dependency-Check et SonarCloud.

### 6. Tests de Sécurité

OWASP Dependency-Check : Analyse des dépendances pour identifier les vulnérabilités connues. <br/>
SonarCloud : Analyse de la qualité et de la sécurité du code source.

## Outils de Sécurité

### Outils utilisés

OWASP Dependency-Check
- Analyse des dépendances et génération de rapports clairs.
- Points forts : Open source, intégration facile dans Jenkins.
- Limites : Ne détecte pas les vulnérabilités spécifiques à une application.

SonarCloud
- Plateforme en ligne pour l’analyse de la qualité du code.
- Points forts : Rapports détaillés et intégration facile avec les pipelines CI/CD.
- Limites : Configuration initiale complexe.

### Outils envisagés mais non utilisés

- Clair
- Trivy
- Grype

Ces outils peuvent être intégrés dans des projets futurs pour renforcer l'analyse des vulnérabilités des conteneurs et dépendances.

## Notes

Ce projet est conçu pour être évolutif et adaptable à des scénarios de production. <br/>
Les outils et techniques utilisés visent à automatiser les processus tout en maintenant des normes de qualité élevées.


