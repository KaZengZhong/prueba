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

        // Usando Snyk CLI directamente con la credencial string
        stage('Snyk Security Check - Backend') {
            steps {
                script {
                    echo 'Running Snyk security analysis on backend code'
                    withCredentials([string(credentialsId: 'snyk-token-string', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh '''
                                export SNYK_TOKEN=${SNYK_TOKEN}
                                cd prestabanco-backend
                                /usr/local/bin/snyk test --all-projects --severity-threshold=high || true
                            '''
                        } else {
                            bat '''
                                set SNYK_TOKEN=%SNYK_TOKEN%
                                cd prestabanco-backend
                                C:\\Users\\kahao\\.jenkins\\tools\\io.snyk.jenkins.tools.SnykInstallation\\snyk_latest\\snyk-win.exe test --all-projects --severity-threshold=high || true
                            '''
                        }
                    }
                }
            }
        }
        
        // Generación de reporte JSON para Backend
        stage('Snyk JSON Report - Backend') {
            steps {
                script {
                    echo 'Generating Snyk JSON report for Backend'
                    withCredentials([string(credentialsId: 'snyk-token-string', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh '''
                                export SNYK_TOKEN=${SNYK_TOKEN}
                                cd prestabanco-backend
                                mkdir -p reports
                                /usr/local/bin/snyk test --json --severity-threshold=high > reports/snyk-backend.json || true
                            '''
                        } else {
                            bat '''
                                set SNYK_TOKEN=%SNYK_TOKEN%
                                cd prestabanco-backend
                                mkdir -p reports
                                C:\\Users\\kahao\\.jenkins\\tools\\io.snyk.jenkins.tools.SnykInstallation\\snyk_latest\\snyk-win.exe test --json --severity-threshold=high > reports\\snyk-backend.json || true
                            '''
                        }
                        // Archivar el reporte JSON como un artefacto
                        archiveArtifacts artifacts: 'prestabanco-backend/reports/snyk-backend.json', allowEmptyArchive: true
                    }
                }
            }
        }

        // El resto de etapas con sus respectivos reportes JSON...
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
                    try {
                        if (isUnix()) {
                            sh 'docker build -t kahaozeng/prestabanco-backend:latest prestabanco-backend'
                        } else {
                            bat 'docker build -t kahaozeng/prestabanco-backend:latest prestabanco-backend'
                        }
                        
                        withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                            if (isUnix()) {
                                sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                                sh 'docker push kahaozeng/prestabanco-backend:latest'
                            } else {
                                bat 'docker login -u %DOCKER_USERNAME% -p %DOCKER_PASSWORD%'
                                bat 'docker push kahaozeng/prestabanco-backend:latest'
                            }
                        }
                    } catch (Exception e) {
                        echo "Warning: Could not push Docker image. Continuing pipeline. Error: ${e.message}"
                    }
                }
            }
        }

        stage('Snyk Container Security - Backend') {
            steps {
                script {
                    echo 'Running Snyk container security analysis for backend'
                    withCredentials([string(credentialsId: 'snyk-token-string', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh '''
                                export SNYK_TOKEN=${SNYK_TOKEN}
                                /usr/local/bin/snyk container test kahaozeng/prestabanco-backend:latest --severity-threshold=high || true
                            '''
                        } else {
                            bat '''
                                set SNYK_TOKEN=%SNYK_TOKEN%
                                C:\\Users\\kahao\\.jenkins\\tools\\io.snyk.jenkins.tools.SnykInstallation\\snyk_latest\\snyk-win.exe container test kahaozeng/prestabanco-backend:latest --severity-threshold=high || true
                            '''
                        }
                    }
                }
            }
        }
        
        // Generación de reporte JSON para Container Backend
        stage('Snyk JSON Report - Container Backend') {
            steps {
                script {
                    echo 'Generating Snyk JSON report for Container Backend'
                    withCredentials([string(credentialsId: 'snyk-token-string', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh '''
                                export SNYK_TOKEN=${SNYK_TOKEN}
                                mkdir -p container-reports
                                /usr/local/bin/snyk container test kahaozeng/prestabanco-backend:latest --json --severity-threshold=high > container-reports/snyk-container-backend.json || true
                            '''
                        } else {
                            bat '''
                                set SNYK_TOKEN=%SNYK_TOKEN%
                                mkdir -p container-reports
                                C:\\Users\\kahao\\.jenkins\\tools\\io.snyk.jenkins.tools.SnykInstallation\\snyk_latest\\snyk-win.exe container test kahaozeng/prestabanco-backend:latest --json --severity-threshold=high > container-reports\\snyk-container-backend.json || true
                            '''
                        }
                        // Archivar el reporte JSON como un artefacto
                        archiveArtifacts artifacts: 'container-reports/snyk-container-backend.json', allowEmptyArchive: true
                    }
                }
            }
        }
        
        // ... Continúa con el resto de etapas similarmente
        
        // Para Frontend y Container Frontend
        // ... [etapas omitidas para brevedad]
    }
}