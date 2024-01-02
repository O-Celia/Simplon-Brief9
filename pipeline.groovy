pipeline {
    agent any 
    environment {
        SONAR_ORGANIZATION_KEY = 'simplon-celiaouedraogo'
        SONAR_TOKEN = credentials('sonarcloud')
    }
    stages {
        stage('Pré-Cleanup') {
            steps {
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
        stage('Deploy Canary and the rest of the application') {
            steps {
                sh('''
                cd Brief8-celia
                kubectl apply -f canary-deployment.yaml
                kubectl apply -f canary-traffic-rules.yaml
                kubectl apply -f ./app
                ''')
            }
        }
        stage('Test de charge') {
            steps {
                sh('''seq 250 | parallel --max-args 0  --jobs 20 "curl -k -iF 'vote=Jenkins' vote.simplon-celia.space"''')
            }
        }
        stage("Dependency Check"){
            steps {
                dependencyCheck additionalArguments: '', odcInstallation: 'owasp-dependency-check'
            }
        }
        stage("DC Results"){
            steps {
                dependencyCheckPublisher failedTotalCritical: 0, failedTotalHigh: 5, pattern: ''
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
                        }
                    }
                }
            }
        }
        stage('Promote to production or Rollback') {
            steps {
                script {
                    def owaspSuccess = currentBuild.rawBuild.getAction(hudson.plugins.dependencycheck.DependencyCheckAction.class).result.isBetterOrEqualTo(Result.SUCCESS)
                    def qgStatus = waitForQualityGate()
                    def sonarCloudSuccess = qgStatus.status == 'OK'

                    if (owaspSuccess && sonarCloudSuccess) {
                        echo "Promoting canary to production."
                        sh 'kubectl set image deployment/vote-app vote-app=celiaoued/vote-app:CANARY_TAG'
                        sh 'kubectl apply -f original-traffic-rules.yaml'
                    } else {
                        echo "Rolling back canary deployment."
                        sh 'kubectl rollout undo deployment vote-app-canary'
                        sh 'kubectl apply -f original-traffic-rules.yaml'
                    }
                }
            }
        }
    }
}
