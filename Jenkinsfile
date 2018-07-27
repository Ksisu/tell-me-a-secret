pipeline {
    agent none
    stages {
        stage('Build') {
            agent any
            steps {
                ansiColor('xterm') {
                    script {
                        docker.build("buildimage", "-f DockerfileBuild .").inside() {
                            sh 'cp -r /app/target/docker/stage ${WORKSPACE}'
                        }
                    }
                }
            }
        }
        stage('Build & Push image') {
            agent any
            steps {
                ansiColor('xterm') {
                    script {
                        def image = docker.build("tell-me-a-secret/backend:${env.GIT_COMMIT}", "./stage")
                        docker.withRegistry('https://registry.wanari.net', 'jenkins-registry') {
                            image.push()
                            image.push("latest")
                        }
                    }
                }
            }
        }
    }
}