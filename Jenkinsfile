pipeline {
    agent any
    tools {
        maven 'maven_3_9_6'
        nodejs 'node'
    }

    stages {
        stage('Checkout repository') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Franciscoxd1123/Pep2DevSecOpsArreglos']])
            }
        }

        stage('Build backend') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'cd prestabanco-backend && ./mvnw clean install -DskipTests'
                    } else {
                        bat 'cd prestabanco-backend && mvnw clean install -DskipTests'
                    }
                }
            }
        }

        stage('Test backend') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'cd prestabanco-backend && ./mvnw test'
                    } else {
                        bat 'cd prestabanco-backend && mvnw test'
                    }
                }
            }
        }

        stage('SonarQube analysis backend') {
            steps {
                withSonarQubeEnv('SonarQube_Server') {
                    script {
                        if (isUnix()) {
                            sh 'cd prestabanco-backend && ./mvnw clean verify sonar:sonar'
                        } else {
                            bat 'cd prestabanco-backend && mvnw clean verify sonar:sonar'
                        }
                    }
                }
            }
        }

        stage('Push backend') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'docker build -t franciscoxd1123/prestabanco-backend:latest prestabanco-backend'
                    } else {
                        bat 'docker build -t franciscoxd1123/prestabanco-backend:latest prestabanco-backend'
                    }
                }
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    script {
                        if (isUnix()) {
                            sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                        } else {
                            bat 'docker login -u %DOCKER_USERNAME% -p %DOCKER_PASSWORD%'
                        }
                    }
                }
                script {
                    if (isUnix()) {
                        sh 'docker push franciscoxd1123/prestabanco-backend:latest'
                    } else {
                        bat 'docker push franciscoxd1123/prestabanco-backend:latest'
                    }
                }
            }
        }

        stage('Install frontend dependencies') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'cd prestabanco-frontend && npm install'
                    } else {
                        bat 'cd prestabanco-frontend && npm install'
                    }
                }
            }
        }

        stage('Install SonarQube Scanner') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'cd prestabanco-frontend && npm install -g sonar-scanner'
                    } else {
                        bat 'cd prestabanco-frontend && npm install -g sonar-scanner'
                    }
                }
            }
        }

        stage('Build frontend') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'cd prestabanco-frontend && npm run build'
                    } else {
                        bat 'cd prestabanco-frontend && npm run build'
                    }
                }
            }
        }

        stage('SonarQube analysis frontend') {
            steps {
                withSonarQubeEnv('SonarQube_Server') {
                    script {
                        if (isUnix()) {
                            sh '''
                                cd prestabanco-frontend
                                sonar-scanner \
                                -Dsonar.projectKey=prestabanco-frontend \
                                -Dsonar.sources=src \
                                -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info \
                                -Dsonar.host.url=$SONAR_HOST_URL \
                                -Dsonar.login=$SONAR_AUTH_TOKEN
                            '''
                        } else {
                            bat '''
                                cd prestabanco-frontend
                                sonar-scanner ^
                                -Dsonar.projectKey=prestabanco-frontend ^
                                -Dsonar.sources=src ^
                                -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info ^
                                -Dsonar.host.url=%SONAR_HOST_URL% ^
                                -Dsonar.login=%SONAR_AUTH_TOKEN%
                            '''
                        }
                    }
                }
            }
        }

        stage('Push frontend') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'docker build -t franciscoxd1123/prestabanco-frontend:latest prestabanco-frontend'
                    } else {
                        bat 'docker build -t franciscoxd1123/prestabanco-frontend:latest prestabanco-frontend'
                    }
                }
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    script {
                        if (isUnix()) {
                            sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                        } else {
                            bat 'docker login -u %DOCKER_USERNAME% -p %DOCKER_PASSWORD%'
                        }
                    }
                }
                script {
                    if (isUnix()) {
                        sh 'docker push franciscoxd1123/prestabanco-frontend:latest'
                    } else {
                        bat 'docker push franciscoxd1123/prestabanco-frontend:latest'
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'docker-compose up -d'
                    } else {
                        bat 'docker-compose up -d'
                    }
                }
            }
        }
    }
}