// Pipeline CI/CD do agenda-service — fluxo GitHub -> Jenkins -> SonarQube.
//
//   - Servidor 'SonarQube' (Manage Jenkins > System) apontando para
//     http://sonarqube:9000, com um token cadastrado como credential.
//   - Plugins: Pipeline, Git, SonarQube Scanner for Jenkins.
//   - Webhook no SonarQube -> http://jenkins:8080/sonarqube-webhook/ (para o Quality Gate).
//   - Container Jenkins com acesso ao Docker do host (Testcontainers) — ver docker-compose.
//
// O build usa o wrapper ./mvnw (Maven 3.8.6 pinado) e o JDK 17 já embutido na imagem do Jenkins.

pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '15'))
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        // Como o container do Jenkins roda como root, HOME=/root e o cache padrão (~/.m2)
        // ficaria FORA do volume persistente. repositório local no JENKINS_HOME,
        // que está no volume jenkins_data — assim as dependências não são re-baixadas a cada build.
        MAVEN_LOCAL_REPO = '/var/jenkins_home/.m2/repository'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                // Garante o bit de execução do wrapper (alguns checkouts podem perdê-lo).
                sh 'chmod +x mvnw'
            }
        }

        stage('Build & Test') {
            // Roda em TODAS as branches: compila, testes unit + integração (H2) + Testcontainers,
            // relatório JaCoCo e o gate de 30% de cobertura (fase verify).
            steps {
                sh './mvnw -B -ntp -Dmaven.repo.local=$MAVEN_LOCAL_REPO clean verify'
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
                success {
                    archiveArtifacts artifacts: 'target/agenda-*.jar', fingerprint: true
                }
            }
        }

        stage('SonarQube Analysis') {
            when {
                anyOf {
                    branch 'main'
                    expression { return env.BRANCH_NAME == null }
                }
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''./mvnw -B -ntp -Dmaven.repo.local=$MAVEN_LOCAL_REPO sonar:sonar \
                          -Dsonar.projectKey=agenda-service \
                          -Dsonar.projectName=agenda-service \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'''
                }
            }
        }

        stage('Quality Gate') {
            when {
                anyOf {
                    branch 'main'
                    expression { return env.BRANCH_NAME == null }
                }
            }
            steps {
                // Espera o SonarQube devolver o resultado via webhook; aborta o build se reprovar.
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        success { echo '✅ Pipeline concluído — Build, Testes e Quality Gate aprovados.' }
        failure { echo '❌ Pipeline falhou — verifique testes, cobertura ou o Quality Gate.' }
        always  { cleanWs() }
    }
}
