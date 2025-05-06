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

        // Versión simplificada para Snyk
        stage('Snyk Security Check - Backend') {
            steps {
                script {
                    echo 'Running Snyk security analysis on backend code'
                    snykSecurity(
                        snykInstallation: 'snyk@latest',
                        snykTokenId: 'snyk-token',
                        targetFile: 'prestabanco-backend/pom.xml',
                        additionalArguments: '--all-projects --severity-threshold=high',
                        failOnIssues: false
                        // Eliminamos monitorOnBuild ya que no es compatible
                    )
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

        // Versión simplificada para Snyk
        stage('Snyk Container Security - Backend') {
            steps {
                script {
                    echo 'Running Snyk container security analysis for backend'
                    snykSecurity(
                        snykInstallation: 'snyk@latest',
                        snykTokenId: 'snyk-token',
                        targetFile: 'prestabanco-backend/Dockerfile',
                        additionalArguments: '--docker kahaozeng/prestabanco-backend:latest --severity-threshold=high',
                        failOnIssues: false
                    )
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

        // Versión simplificada para Snyk
        stage('Snyk Security Check - Frontend') {
            steps {
                script {
                    echo 'Running Snyk security analysis on frontend code'
                    snykSecurity(
                        snykInstallation: 'snyk@latest',
                        snykTokenId: 'snyk-token',
                        targetFile: 'prestabanco-frontend/package.json',
                        additionalArguments: '--severity-threshold=high',
                        failOnIssues: false
                    )
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

        // Versión simplificada para Snyk
        stage('Snyk Container Security - Frontend') {
            steps {
                script {
                    echo 'Running Snyk container security analysis for frontend'
                    snykSecurity(
                        snykInstallation: 'snyk@latest',
                        snykTokenId: 'snyk-token',
                        targetFile: 'prestabanco-frontend/Dockerfile',
                        additionalArguments: '--docker kahaozeng/prestabanco-frontend:latest --severity-threshold=high',
                        failOnIssues: false
                    )
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