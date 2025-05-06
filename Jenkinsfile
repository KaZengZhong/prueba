pipeline {
    agent any
    tools {
        maven 'maven'
    }

    stages {
        stage('Checkout repository') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/KaZengZhong/prueba']])
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

        // Análisis Snyk del backend usando StringCredentials
        stage('Snyk Security Check - Backend') {
            steps {
                script {
                    echo 'Running Snyk security analysis on backend code'
                    withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh 'export SNYK_TOKEN=${SNYK_TOKEN} && cd prestabanco-backend && snyk test --all-projects --severity-threshold=high || true'
                            sh 'export SNYK_TOKEN=${SNYK_TOKEN} && cd prestabanco-backend && snyk monitor --all-projects || true'
                        } else {
                            bat 'set SNYK_TOKEN=%SNYK_TOKEN% && cd prestabanco-backend && snyk test --all-projects --severity-threshold=high || true'
                            bat 'set SNYK_TOKEN=%SNYK_TOKEN% && cd prestabanco-backend && snyk monitor --all-projects || true'
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
                        sh 'docker build -t kahaozeng/prestabanco-backend:latest prestabanco-backend'
                    } else {
                        bat 'docker build -t kahaozeng/prestabanco-backend:latest prestabanco-backend'
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
                        sh 'docker push kahaozeng/prestabanco-backend:latest'
                    } else {
                        bat 'docker push kahaozeng/prestabanco-backend:latest'
                    }
                }
            }
        }

        // Análisis Snyk de la imagen de Docker del backend
        stage('Snyk Container Security - Backend') {
            steps {
                script {
                    echo 'Running Snyk container security analysis for backend'
                    withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh 'export SNYK_TOKEN=${SNYK_TOKEN} && snyk container test kahaozeng/prestabanco-backend:latest --severity-threshold=high || true'
                            sh 'export SNYK_TOKEN=${SNYK_TOKEN} && snyk container monitor kahaozeng/prestabanco-backend:latest || true'
                        } else {
                            bat 'set SNYK_TOKEN=%SNYK_TOKEN% && snyk container test kahaozeng/prestabanco-backend:latest --severity-threshold=high || true'
                            bat 'set SNYK_TOKEN=%SNYK_TOKEN% && snyk container monitor kahaozeng/prestabanco-backend:latest || true'
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

        // Análisis Snyk del frontend
        stage('Snyk Security Check - Frontend') {
            steps {
                script {
                    echo 'Running Snyk security analysis on frontend code'
                    withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh 'export SNYK_TOKEN=${SNYK_TOKEN} && cd prestabanco-frontend && snyk test --severity-threshold=high || true'
                            sh 'export SNYK_TOKEN=${SNYK_TOKEN} && cd prestabanco-frontend && snyk monitor || true'
                        } else {
                            bat 'set SNYK_TOKEN=%SNYK_TOKEN% && cd prestabanco-frontend && snyk test --severity-threshold=high || true'
                            bat 'set SNYK_TOKEN=%SNYK_TOKEN% && cd prestabanco-frontend && snyk monitor || true'
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
                        sh 'docker build -t kahaozeng/prestabanco-frontend:latest prestabanco-frontend'
                    } else {
                        bat 'docker build -t kahaozeng/prestabanco-frontend:latest prestabanco-frontend'
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
                        sh 'docker push kahaozeng/prestabanco-frontend:latest'
                    } else {
                        bat 'docker push kahaozeng/prestabanco-frontend:latest'
                    }
                }
            }
        }

        // Análisis Snyk de la imagen de Docker del frontend
        stage('Snyk Container Security - Frontend') {
            steps {
                script {
                    echo 'Running Snyk container security analysis for frontend'
                    withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh 'export SNYK_TOKEN=${SNYK_TOKEN} && snyk container test kahaozeng/prestabanco-frontend:latest --severity-threshold=high || true'
                            sh 'export SNYK_TOKEN=${SNYK_TOKEN} && snyk container monitor kahaozeng/prestabanco-frontend:latest || true'
                        } else {
                            bat 'set SNYK_TOKEN=%SNYK_TOKEN% && snyk container test kahaozeng/prestabanco-frontend:latest --severity-threshold=high || true'
                            bat 'set SNYK_TOKEN=%SNYK_TOKEN% && snyk container monitor kahaozeng/prestabanco-frontend:latest || true'
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