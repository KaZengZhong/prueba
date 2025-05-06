pipeline {
    agent any
    tools {
        maven 'maven'
    }

    environment {
        // Definimos variables para Snyk
        SNYK_TOKEN = credentials('snyk-token')
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

        // Añadimos stage para análisis Snyk del backend
        stage('Snyk Security Check - Backend') {
            steps {
                script {
                    echo 'Running Snyk security analysis on backend code'
                    withCredentials([string(credentialsId: 'snyk-api-token', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh 'cd prestabanco-backend && snyk test --all-projects --severity-threshold=high || true'
                            sh 'cd prestabanco-backend && snyk monitor --all-projects || true'
                        } else {
                            bat 'cd prestabanco-backend && snyk test --all-projects --severity-threshold=high || true'
                            bat 'cd prestabanco-backend && snyk monitor --all-projects || true'
                        }
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

        // Añadimos stage para escanear la imagen de Docker del backend
        stage('Snyk Container Security - Backend') {
            steps {
                script {
                    echo 'Running Snyk container security analysis for backend'
                    withCredentials([string(credentialsId: 'snyk-api-token', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh 'snyk container test franciscoxd1123/prestabanco-backend:latest --severity-threshold=high || true'
                            sh 'snyk container monitor franciscoxd1123/prestabanco-backend:latest || true'
                        } else {
                            bat 'snyk container test franciscoxd1123/prestabanco-backend:latest --severity-threshold=high || true'
                            bat 'snyk container monitor franciscoxd1123/prestabanco-frontend:latest || true'
                        }
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

        // Añadimos stage para análisis Snyk del frontend
        stage('Snyk Security Check - Frontend') {
            steps {
                script {
                    echo 'Running Snyk security analysis on frontend code'
                    withCredentials([string(credentialsId: 'snyk-api-token', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh 'cd prestabanco-frontend && snyk test --severity-threshold=high || true'
                            sh 'cd prestabanco-frontend && snyk monitor || true'
                        } else {
                            bat 'cd prestabanco-frontend && snyk test --severity-threshold=high || true'
                            bat 'cd prestabanco-frontend && snyk monitor || true'
                        }
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

        // Añadimos stage para escanear la imagen de Docker del frontend
        stage('Snyk Container Security - Frontend') {
            steps {
                script {
                    echo 'Running Snyk container security analysis for frontend'
                    withCredentials([string(credentialsId: 'snyk-api-token', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh 'snyk container test franciscoxd1123/prestabanco-frontend:latest --severity-threshold=high || true'
                            sh 'snyk container monitor franciscoxd1123/prestabanco-frontend:latest || true'
                        } else {
                            bat 'snyk container test franciscoxd1123/prestabanco-frontend:latest --severity-threshold=high || true'
                            bat 'snyk container monitor franciscoxd1123/prestabanco-frontend:latest || true'
                        }
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