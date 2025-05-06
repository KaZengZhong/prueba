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

        // Usando Snyk CLI exclusivamente para análisis de código (SAST) en backend
        stage('Snyk SAST - Backend') {
            steps {
                script {
                    echo 'Running Snyk SAST analysis on backend code'
                    withCredentials([string(credentialsId: 'snyk-token-string', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh '''
                                export SNYK_TOKEN=${SNYK_TOKEN}
                                cd prestabanco-backend
                                # Solo análisis de código (SAST)
                                /usr/local/bin/snyk code test --severity-threshold=high || true
                            '''
                        } else {
                            bat '''
                                set SNYK_TOKEN=%SNYK_TOKEN%
                                cd prestabanco-backend
                                REM Solo análisis de código (SAST)
                                C:\\Users\\kahao\\.jenkins\\tools\\io.snyk.jenkins.tools.SnykInstallation\\snyk_latest\\snyk-win.exe code test --severity-threshold=high || true
                            '''
                        }
                    }
                }
            }
        }
        
        // Generación de reporte HTML para Backend (solo código SAST)
        stage('Snyk SAST Report - Backend') {
            steps {
                script {
                    echo 'Generating Snyk SAST HTML report for Backend'
                    withCredentials([string(credentialsId: 'snyk-token-string', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh '''
                                export SNYK_TOKEN=${SNYK_TOKEN}
                                cd prestabanco-backend
                                mkdir -p reports
                                # Reporte de código (SAST)
                                /usr/local/bin/snyk code test --json --severity-threshold=high > reports/snyk-sast-output.json || true
                                /usr/local/bin/snyk-to-html -i reports/snyk-sast-output.json -o reports/snyk-backend-sast-report.html || true
                            '''
                        } else {
                            bat '''
                                set SNYK_TOKEN=%SNYK_TOKEN%
                                cd prestabanco-backend
                                mkdir -p reports
                                REM Reporte de código (SAST)
                                C:\\Users\\kahao\\.jenkins\\tools\\io.snyk.jenkins.tools.SnykInstallation\\snyk_latest\\snyk-win.exe code test --json --severity-threshold=high > reports\\snyk-sast-output.json || true
                                npx snyk-to-html -i reports\\snyk-sast-output.json -o reports\\snyk-backend-sast-report.html || true
                            '''
                        }
                        // Archivar el reporte HTML como artefacto
                        archiveArtifacts artifacts: 'prestabanco-backend/reports/snyk-backend-sast-report.html', allowEmptyArchive: true
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
                    try {
                        if (isUnix()) {
                            sh 'docker build -t kahaozeng/prestabanco-backend:latest prestabanco-backend'
                        } else {
                            bat 'docker build -t kahaozeng/prestabanco-backend:latest prestabanco-backend'
                        }
                        
                        // Usar credenciales de Docker correctas
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

        // Usando Snyk CLI directamente para contenedores
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

        // Usando Snyk CLI exclusivamente para análisis de código (SAST) en frontend
        stage('Snyk SAST - Frontend') {
            steps {
                script {
                    echo 'Running Snyk SAST analysis on frontend code'
                    withCredentials([string(credentialsId: 'snyk-token-string', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh '''
                                export SNYK_TOKEN=${SNYK_TOKEN}
                                cd prestabanco-frontend
                                # Solo análisis de código (SAST)
                                /usr/local/bin/snyk code test --severity-threshold=high || true
                            '''
                        } else {
                            bat '''
                                set SNYK_TOKEN=%SNYK_TOKEN%
                                cd prestabanco-frontend
                                REM Solo análisis de código (SAST)
                                C:\\Users\\kahao\\.jenkins\\tools\\io.snyk.jenkins.tools.SnykInstallation\\snyk_latest\\snyk-win.exe code test --severity-threshold=high || true
                            '''
                        }
                    }
                }
            }
        }
        
        // Generación de reporte HTML para Frontend (solo código SAST)
        stage('Snyk SAST Report - Frontend') {
            steps {
                script {
                    echo 'Generating Snyk SAST HTML report for Frontend'
                    withCredentials([string(credentialsId: 'snyk-token-string', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh '''
                                export SNYK_TOKEN=${SNYK_TOKEN}
                                cd prestabanco-frontend
                                mkdir -p reports
                                # Reporte de código (SAST)
                                /usr/local/bin/snyk code test --json --severity-threshold=high > reports/snyk-sast-output.json || true
                                /usr/local/bin/snyk-to-html -i reports/snyk-sast-output.json -o reports/snyk-frontend-sast-report.html || true
                            '''
                        } else {
                            bat '''
                                set SNYK_TOKEN=%SNYK_TOKEN%
                                cd prestabanco-frontend
                                mkdir -p reports
                                REM Reporte de código (SAST)
                                C:\\Users\\kahao\\.jenkins\\tools\\io.snyk.jenkins.tools.SnykInstallation\\snyk_latest\\snyk-win.exe code test --json --severity-threshold=high > reports\\snyk-sast-output.json || true
                                npx snyk-to-html -i reports\\snyk-sast-output.json -o reports\\snyk-frontend-sast-report.html || true
                            '''
                        }
                        // Archivar el reporte HTML como artefacto
                        archiveArtifacts artifacts: 'prestabanco-frontend/reports/snyk-frontend-sast-report.html', allowEmptyArchive: true
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
                    try {
                        if (isUnix()) {
                            sh 'docker build -t kahaozeng/prestabanco-frontend:latest prestabanco-frontend'
                        } else {
                            bat 'docker build -t kahaozeng/prestabanco-frontend:latest prestabanco-frontend'
                        }
                        
                        // Usar credenciales de Docker correctas
                        withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                            if (isUnix()) {
                                sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                                sh 'docker push kahaozeng/prestabanco-frontend:latest'
                            } else {
                                bat 'docker login -u %DOCKER_USERNAME% -p %DOCKER_PASSWORD%'
                                bat 'docker push kahaozeng/prestabanco-frontend:latest'
                            }
                        }
                    } catch (Exception e) {
                        echo "Warning: Could not push Docker image. Continuing pipeline. Error: ${e.message}"
                    }
                }
            }
        }

        // Usando Snyk CLI directamente para contenedores frontend
        stage('Snyk Container Security - Frontend') {
            steps {
                script {
                    echo 'Running Snyk container security analysis for frontend'
                    withCredentials([string(credentialsId: 'snyk-token-string', variable: 'SNYK_TOKEN')]) {
                        if (isUnix()) {
                            sh '''
                                export SNYK_TOKEN=${SNYK_TOKEN}
                                /usr/local/bin/snyk container test kahaozeng/prestabanco-frontend:latest --severity-threshold=high || true
                            '''
                        } else {
                            bat '''
                                set SNYK_TOKEN=%SNYK_TOKEN%
                                C:\\Users\\kahao\\.jenkins\\tools\\io.snyk.jenkins.tools.SnykInstallation\\snyk_latest\\snyk-win.exe container test kahaozeng/prestabanco-frontend:latest --severity-threshold=high || true
                            '''
                        }
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    try {
                        if (isUnix()) {
                            sh 'docker-compose up -d'
                        } else {
                            bat 'docker-compose up -d'
                        }
                    } catch (Exception e) {
                        echo "Warning: Could not deploy with Docker Compose. Error: ${e.message}"
                    }
                }
            }
        }
    }
}