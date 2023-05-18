# Outils de sécurité

## Outils utilisés

### OWASP Dependency-Check

Identification des vulnérabilités des dépendances des applications : analyse des fichiers de configuration et de construction pour identifier les dépendances utilisées et recherche des références connues de vulnérabilités de sécurité dans ces dépendances et production d'un rapport. L'éditeur travaille indépendament, lis le rapport et génère des métriques, résultats, la tendance qu'il extraie du rapport.

- Avantages : open source, facile à utiliser, plugin sur Jenkins, rapports clairs et détaillés, utilise des bases de données de vulnérabilités, peut analyser différents langages de programmation
- Inconvénients : ne peut pas détecter de nouvelles vulnérabilités ou des vulnérabilités spécifiques à l'application, limites de performance pour de grandes applications, ne peut pas résoudre les vulnérabilités détectées ni apporter d'indication de résolution


### SonarCloud

Outil d'analyse de code en ligne pour trouver les problèmes de qualité et de sécurité, suivre leur progression au fil du temps et d'assurer une qualité constante. Il fournit des informations détaillées sur les vulnérabilités, les bugs, les codes mal formatés et autres problèmes.

- Avantages : analyses de code rapides et détaillées, nombreux langages de programmation, fournit des tableaux de bord, facilement intégré dans des pipelines Jenkins, règles de qualité peuvent être personnalisées, sécurité du code est prise en compte
- Inconvénients : configuration initiale compliquée, faux positifs possibles, analyses en temps réel peuvent être limitées selon la plateforme,  personnalisation des règles de qualité peut nécessiter une expertise

## Outils non utilisés

### SonarQube

Plateforme d'analyse statique de code qui aide à mesurer et à améliorer la qualité du code. Il fournit des fonctionnalités de surveillance continue de la qualité du code, de la sécurité et de la conformité aux règles de codage.

- Avantages: Permet d'améliorer la qualité du code, détecte les problèmes dès leur apparition, permet de personnaliser les régles de qualité, peut être intégré aux outils de développement
- Inconvénients:  L'intégration et la configuration initiales de SonarQube peuvent nécessiter du temps et des efforts, la configuration des règles personnalisées et des paramètres peuvent être complexes, possibilité de faux positifs et faux négatifs, peut rencontrer des limitations techniques pour l'analyse de certains langages de programmation ou pour l'analyse de projets très volumineux

### Clair

Outil utilisé pour analyser les vulnérabilités connues présentes dans les bibliothèques, les dépendances et les composants logiciels des images de conteneurs. Il fournit des informations détaillées sur leur impact potentiel et les correctifs disponibles. 

- Avantages: Peut être intégré dans les pipelines CICD ce qui permet une analyse automatique et continue, analyse les images de conteneurs dès le processus de développement pour détecter les vulnérabilités et prendre des mesures pour les corriger avant le déploiement, utilise une base de données de vulnérabilités connues, ce qui permet de disposer de données fiables, génère des notifications et des alertes lorsque des vulnérabilités sont détectées
- Inconvénients : capacité à détecter les vulnérabilités dépend de la qualité et de la mise à jour de la base de données de vulnérabilités utilisée, ne détecte que les vulnérabilités connues et répertoriées dans sa base de données, pas les vulnérabilités inconnues ou les failles de sécurité spécifiques à une application particulière, peut générer des faux positifs ou des faux négatifs 

### Trivy 

Outil utilisé pour analyser les images de conteneurs et les systèmes de fichiers à la recherche de vulnérabilités connues. Il est spécifiquement conçu pour détecter les failles de sécurité dans les environnements basés sur des conteneurs tels que Docker. Il analyse statique des systèmes de fichiers et des images de conteneurs pour identifier les vulnérabilités connues dans les packages, les bibliothèques et les dépendances, et fournit des rapports détaillés sur les vulnérabilités détectée

- Avantages: utilise des bases de données de vulnérabilités bien connues pour obtenir des résultats précis et fiables sur les vulnérabilités présentes, peut être intégré dans les pipelines CI/CD, ce qui permet une analyse automatique et continue, fournit des rapports détaillés sur les vulnérabilités détectées, conçu pour être simple à utiliser et à intégrer dans les workflows existants
- Inconvénients: se concentre sur la détection des vulnérabilités connues répertoriées dans les bases de données de référence et peut ne pas détecter les vulnérabilités inconnues ou les failles spécifiques à une application particulière, ne peut pas détecter les vulnérabilités qui peuvent être exploitées dynamiquement ou dans des conditions spécifiques, peut générer des faux positifs ou des faux négatifs 

### Grype

Outil utilisé pour l'analyse des vulnérabilités des images de conteneurs. Il est conçu pour détecter les vulnérabilités connues dans les bibliothèques et les dépendances des images de conteneurs, en se concentrant sur les informations et les métadonnées fournies par les gestionnaires de packages, fournit des rapports détaillés sur les vulnérabilités détectées

- Avantages: utilise des bases de données de vulnérabilités populaires pour obtenir des résultats précis et fiables, peut être intégré dans les pipelines CI/CD, prend en charge plusieurs gestionnaires de packages couramment utilisés pour une flexibilité pour les environnements de conteneurs hétérogènes, fournit des rapports détaillés sur les vulnérabilités détectées,  peut être intégré avec d'autres outils de sécurité et de gestion des conteneurs pour fournir une analyse de sécurité complète
- Inconvénients: se concentre sur la détection des vulnérabilités connues répertoriées dans les bases de données de référence, ne pas détecter les vulnérabilités inconnues ou les failles spécifiques à une application particulière, ne peut pas détecter les vulnérabilités qui peuvent être exploitées dynamiquement ou dans des conditions spécifiques, peut générer des faux positifs ou des faux négatifs

### OWASP Zap

Outil utilisé pour effectuer des tests de sécurité automatisés et manuels des applications web. Il génère des rapports détaillés sur les vulnérabilités détectées lors des tests, est hautement extensible et offre de nombreuses fonctionnalités supplémentaires via des plugins

- Avantages: détecte un large éventail de vulnérabilités courantes dans les applications web, permet d'automatiser les tests de sécurité, dispose d'une interface utilisateur conviviale qui rend l'outil accessible, est hautement personnalisable et extensible
- Inconvénients: certaines fonctionnalités sont complexes à utiliser, peut générer des faux positifs ou faux négatifs, peut entraîner une surcharge réseau, la précision des résultats dépend de la configuration correcte de l'outil et de la définition des scénarios de test appropriés
