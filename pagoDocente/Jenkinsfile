pipeline {
    agent any

    stages {
        stage('Compilar') {
            steps {
                script {
                    echo '🔨 Compilando el proyecto...'
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Ejecutar Pruebas') {
            steps {
                script {
                    echo '🧪 Ejecutando pruebas unitarias...'
                    sh 'mvn test'
                }
            }
        }

        stage('Publicar Resultados') {
            steps {
                script {
                    echo '📦 Archivando resultados de pruebas...'
                    junit 'target/surefire-reports/*.xml'  // Ruta por defecto para JUnit en Maven
                    archiveArtifacts artifacts: 'target/surefire-reports/*.xml', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        always {
            echo '✅ Pipeline ejecutado.'
        }
        success {
            echo '🎉 Todas las pruebas fueron exitosas.'
        }
        failure {
            echo '❌ Hubo fallos en alguna etapa del pipeline.'
        }
    }
}
