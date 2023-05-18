# Brief 9

## Chapitre 1 : Déployer un cluster AKS
    
### Créer un cluster AKS avec 1 node

Connexion azure CLI
``az login``

Création d'un groupe de ressource
``az group create --name B9Celia --location westeurope``

Création du cluster AKS avec 1 nodes et la clé SSH de l'utilisateur dans le groupe de ressource
``az aks create -g B9Celia -n AKSCluster --node-count 1 --ssh-key-value ./.ssh/id_rsa.pub --enable-managed-identity -a ingress-appgw --appgw-name B8Gateway --appgw-subnet-cidr "10.225.0.0/16"``

Téléchargement des informations d’identification et configuration de l’interface de ligne de commande Kubernetes
``az aks get-credentials --resource-group B9Celia --name AKSCluster``

Créer 2 namespaces
``kubectl create namespace qal``
``kubectl create namespace prod``

## Chapitre 2 : Installation de Jenkins avec Docker

Utilisation d'un dockerfile et d'un docker-compose.yml pour installer Jenkins : 

```consol
FROM jenkins/jenkins:lts
USER root
RUN apt-get -y update && apt-get install -y jq parallel wget unzip nodejs
RUN wget -O /tmp/sonarscanner.zip https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.6.0.2311-linux.zip && \
    unzip /tmp/sonarscanner.zip -d /opt && \
    rm /tmp/sonarscanner.zip && \
    ln -s /opt/sonar-scanner-4.6.0.2311-linux/bin/sonar-scanner /usr/bin/sonar-scanner
COPY config /root/.kube/config
```

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
```consol
sudo docker compose up --build
# --remove-orphans si containers à supprimer
# --build si Dockerfile a changé, pour considérer les changements
```
Se connecter à http://localhost:8080/

Pour se connecter en tant que Jenkins (en cas de debug nécessaire): 
```consol
sudo docker ps
sudo docker exec -it container_ID bash
```

## Chapitre 3 : Configuration de Jenkins

Installation de plugins : 
*pipeline, workspace cleanup, github, docker, docker pipeline*

Ajout de credentials :
- docker pour docker hub
- token pour sonarcloud

## Chapitre 4 : Création de DNS l'application de vote

Création d’un enregistrement DNS pointant vers l’adresse IP de l’application sur Gandi : http://vote.simplon-celia.space

![](https://i.imgur.com/eXNRs5q.png)

## Chapitre 5 : Pipeline

```code
pipeline {
    agent any 
    environment {
        DOCKERHUB_CREDENTIALS=credentials('dockerhubaccount')
        SONAR_ORGANIZATION_KEY = 'simplon-celiaouedraogo'
        SONAR_TOKEN = credentials('sonarcloud')
    }
    stages {
        stage('Pré-Cleanup') {
            steps {
                // Clean before build
                cleanWs()
                echo "Building ${env.JOB_NAME}..."
            }
        }
        stage('Cloning the git') {
            steps {
                sh('''
                git clone https://github.com/Simplon-CeliaOuedraogo/Brief8-celia.git
                ''')
            }
        }
        stage('Build image') {
            steps {
                sh('''
                    cd Brief8-celia
                    docker build -t vote-app .
                ''')
            }
        }
        stage('Login and Push image on DockerHub') {
            steps {
                sh('''
                    echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin
                    cd Brief8-celia
                    PATCH=\044(cat azure-vote/main.py | grep -E "^ver = \\"[0-9.]+\\"\\$"|awk -F\\" {'print $2'})
                    docker tag vote-app celiaoued/vote-app:\044PATCH
                    docker push celiaoued/vote-app:\044PATCH
                ''')
            }
        }
        stage('Change tag yaml and launch vote site') {
            steps {
                sh('''
                git clone https://github.com/Simplon-CeliaOuedraogo/brief7-yaml.git app
                TAG=\044(curl -sSf https://registry.hub.docker.com/v2/repositories/celiaoued/vote-app/tags |jq '."results"[0]["name"]'| tr -d '"')
                sed -i "s/TAG/\044{TAG}/" ./app/vote.yaml
                kubectl apply -f ./app
                ''')
            }
        }
        stage('Test de charge') {
            steps {
                sh('''seq 250 | parallel --max-args 0  --jobs 20 "curl -k -iF 'vote=Jenkins' vote.simplon-celia.space"
                ''')
            }
        }
        stage("Dependency Check"){
            steps {
                dependencyCheck additionalArguments: '', odcInstallation: 'owasp-dependency-check'
            }
        }
        stage("DC Results"){
            steps {
                dependencyCheckPublisher failedTotalCritical: 0, failedTotalHigh: 5, pattern: '', stopBuild: true
            }
        }
        stage('SonarCloud analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonarcloud', variable: 'SONAR_TOKEN')]) {
                    script {
                        withSonarQubeEnv('sonarcloud') {
                            sh '/usr/bin/sonar-scanner \
                                -Dsonar.projectKey=voteapp \
                                -Dsonar.organization=$SONAR_ORGANIZATION_KEY \
                                -Dsonar.sources=. \
                                -Dsonar.host.url=https://sonarcloud.io \
                                -Dsonar.login=$SONAR_TOKEN'
                            waitForQualityGate abortPipeline: true
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            // Deconnexion Docker
            sh 'docker logout'
        }
    }
}
```

## Chapitre 6 : Tests

### OWASP Dependency Check

Installation du plugin : *OWASP Dependency-Check*
Configuration globale des outils : 
![](https://i.imgur.com/lJZFf0G.png)
Utilisation de Pipeline Syntax pour obtenir le script nécessaire pour lancer Dependency-check et Dependency Publisher
Choix d'un nombre maximal d'erreur permises avant de stopper le build : 
![](https://i.imgur.com/NQ2G9Vj.png)


### SonnarCloud

Installation du plugin SonarQube (peut-être pas nécessaire, à tester)

Installer SonnarScanner Cli sur la machine Jenkins (cf dockerfile)

Création d'un compte sur SonarCloud : 
- Création d'une organisation / connection à une organisation
- Création d'un projet (noter le project key : suite de "id="" dans la barre de recherche sur la page du projet)
- Création d'un token dans Compte/Security/Generate token (noter)

Ajout du token dans les credentials Jenkins

Configuration du système (peut-être pas nécessaire, à tester) : 

![](https://i.imgur.com/YMC0SOi.png)

Stop de la pipeline si Quality Gate non passée

