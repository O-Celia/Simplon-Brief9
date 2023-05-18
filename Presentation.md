---
title: Brief9
tags: presentation
slideOptions:
  theme: moon
  transition: 'fade'
  spotlight:
    enabled: true
---

# Brief 9 : Mise en place d'outillage DevSecOps

---

## Objectifs et ressources

----

### Objectifs

- Déployer un pipeline de déploiement continu pour l’application Azure Voting App et sa base de données Redis, avec mise à jour automatique de la version à chaque mise à jour de l’application
- Implémenter deux tests de sécurité automatisés


----

### Ressources réutilisées

- Pipeline-mise à jour de l'application : Brief 8
![](https://i.imgur.com/vTeXORv.png)

----

### Ressources réutilisées

- Kubernetes-déploiement de l'application : Brief 6
![](https://i.imgur.com/M7Piqz6.png =850x)

---

## Pipeline Jenkins

----

### Installation Jenkins

- Dockerfile

```consol
FROM jenkins/jenkins:lts
USER root
RUN curl -sL https://deb.nodesource.com/setup_14.x | bash -
RUN apt-get -y update && apt-get install -y jq parallel wget unzip nodejs
RUN wget -O /tmp/sonarscanner.zip https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.6.0.2311-linux.zip && \
    unzip /tmp/sonarscanner.zip -d /opt && \
    rm /tmp/sonarscanner.zip && \
    ln -s /opt/sonar-scanner-4.6.0.2311-linux/bin/sonar-scanner /usr/bin/sonar-scanner
COPY config /root/.kube/config
```

----

- Docker-compose

```consol
version: '3.8'
services:
  jenkins:
    build: .
    privileged: true
    user: root
    ports:
     - 8080:8080
     - 50000:50000
    container_name: jenkins
    volumes:
      - ./jenkins_configuration:/var/jenkins_home
      - /var/run/docker.sock:/var/run/docker.sock
      - /usr/bin/docker:/usr/bin/docker
      - /usr/local/bin/kubectl:/usr/bin/kubectl
```

----

### Configuration de Jenkins

- Plugins installés : kubernetes, kubernetes cli, kubernetes credentials, pipeline, workspace cleanup, github, docker, docker pipeline, OWASP Dependency-Check, SonarScanner
- Création de credentials pour DockerHub et SonarCloud

![](https://i.imgur.com/GF6I4Wh.png)


----

### Construction de l'image docker

- Suppression du workspace précédent et ajout DockerHub credentials

![](https://i.imgur.com/SbTXein.png)

----

### Image Docker

- Clone du GitHub contenant le Dockerfile
- Build de l'image à partir du Dockerfile

![](https://i.imgur.com/vfLFLoE.png)

----

### DockerHub

- Connexion à DockerHub, automatisation de la version de l'image, tag et push

![](https://i.imgur.com/lVp06ew.png)

----

### Déploiement automatisé

- Déploiement à partir de kubernetes et de la dernière image sur DockerHub

![](https://i.imgur.com/eCPIPAC.png)

---

## Tests

----

### Test de charge

- Envoi de 250 votes, 20 à la fois

![](https://i.imgur.com/ONHlwwi.png)

----

### OWASP Dependency Check

- Identification des vulnérabilités des dépendances des applications : analyse des fichiers de configuration et de construction pour identifier les dépendances utilisées et recherche des références connues de vulnérabilités de sécurité dans ces dépendances et production d'un rapport
- Editeur travaille indépendament, lis le rapport et génère des métriques, résultats, la tendance qu'il extraie du rapport

----

- Avantages : open source, facile à utiliser, plugin sur Jenkins, rapports clairs et détaillés, utilise des bases de données de vulnérabilités, peut analyser différents langages de programmation

- Inconvénients : ne peut pas détecter de nouvelles vulnérabilités ou des vulnérabilités spécifiques à l'application, limites de performance pour de grandes applications, ne peut pas résoudre les vulnérabilités détectées ni apporter d'indication de résolution

----

![](https://i.imgur.com/p6r8mR0.png)

----

![](https://i.imgur.com/pKDvOpN.png)

----

![](https://i.imgur.com/LlJX6zj.png)

----

![](https://i.imgur.com/2SeEeJq.png)

----

### SonarCloud

- Outil d'analyse de code en ligne pour trouver les problèmes de qualité et de sécurité, suivre leur progression au fil du temps et d'assurer une qualité constante 
- Fournit des informations détaillées sur les vulnérabilités, les bugs, les codes mal formatés et autres problèmes.

----

- Avantages : analyses de code rapides et détaillées, nombreux langages de programmation, fournit des tableaux de bord, facilement intégré dans des pipelines Jenkins, règles de qualité peuvent être personnalisées, sécurité du code est prise en compte

- Inconvénients : configuration initiale compliquée, faux positifs possibles, analyses en temps réel peuvent être limitées selon la plateforme,  personnalisation des règles de qualité peut nécessiter une expertise

----

![](https://i.imgur.com/mLxljJ9.png)


----

![](https://i.imgur.com/3nIjYuR.png)

---

![](https://i.imgur.com/e6CTzPv.png)





