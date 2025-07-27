// Jenkinsfile
// This file defines the Continuous Integration/Continuous Delivery pipeline for the To-Do List application.

pipeline {
    // Agent defines where the pipeline will run. 'any' means Jenkins can run it on any available agent.
    // For production, you might specify a label to run on a specific build agent.
    agent any

    // Environment variables used throughout the pipeline.
    // Replace 'your-gcp-project-id' with your actual Google Cloud Project ID.
    // These will be used for tagging Docker images for Google Container Registry (GCR).
    environment {
    GCP_PROJECT_ID = 'cbd-a-6712'
    // New IMAGE_NAME format for Artifact Registry: <REGION>-docker.pkg.dev/<PROJECT_ID>/<REPOSITORY_NAME>/<IMAGE_NAME_SUFFIX>
    IMAGE_NAME = "asia-south1-docker.pkg.dev/${GCP_PROJECT_ID}/todo-app-images/todo-app-backend" // <<< IMPORTANT: Updated path
}

    // Stages define the logical steps of your pipeline.
    stages {
        // Stage 1: Checkout Code from GitHub
        stage('Checkout Code') {
            steps {
                // Uses the Git SCM plugin to clone the repository.
                // Since you chose not to use credentials, we've removed the credentialsId parameter.
                git branch: 'main', url: 'https://github.com/Siddartha-balla/todo-app-java-devops.git'
            }
        }

        // Stage 2: Build Java Backend using Maven
        stage('Build Java Backend') {
            steps {
                dir('backend') {
                    // Wrap the Maven command with 'withMaven' and specify the Maven installation name
                    withMaven(maven: 'Maven 3.9.11') { // <<< IMPORTANT: Use the name you gave in Jenkins Tools config (e.g., 'Maven 3.9.11' or 'Maven 3.x.x')
                        bat "mvn clean package -DskipTests"
                    }
                }
            }
        }

        // Stage 3: Build Docker Image for the Java Backend
        stage('Build Docker Image') {
            steps {
                // Navigates into the 'backend' directory where the Dockerfile is located.
                dir('backend') {
                    script {
                        // Builds the Docker image.
                        // Tags the image with both the Jenkins BUILD_NUMBER (for unique versions)
                        // and 'latest' (for easy reference to the most recent successful build).
                        // Note: 'docker.build' is a Jenkins Pipeline step, which handles calling Docker CLI.
                        docker.build("${IMAGE_NAME}:${env.BUILD_NUMBER}", ".")
                        docker.build("${IMAGE_NAME}:latest", ".")
                    }
                }
            }
        }

        // Stage 4: Push Docker Image to Google Container Registry (GCR)
        

stage('Push Docker Image to GCR') {
    steps {
        script {
            // Use 'file' binding for the 'Secret file' credential
            withCredentials([file(credentialsId: 'gcp-service-account-json-file', variable: 'GCP_KEY_FILE_PATH')]) {
                // Authenticate gcloud using the path to the temporary file
                bat "gcloud auth activate-service-account --key-file=%GCP_KEY_FILE_PATH%"
                bat "gcloud auth configure-docker"

                // Push the Docker images to GCR
                docker.image("${IMAGE_NAME}:${env.BUILD_NUMBER}").push()
                docker.image("${IMAGE_NAME}:latest").push()
            }
        }
    }
}



        // Stage 5: Deploy to Kubernetes (Placeholder for now)
        // This stage will be fully implemented once we set up Kubernetes on GCP.
        stage('Deploy to Kubernetes') {
            steps {
                echo "Deployment to Kubernetes will happen here."
                echo "This stage will involve kubectl commands to apply deployment and service manifests."
            }
        }
    }

    // Post-build actions (e.g., notifications, cleanup)
    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
        // You can add 'unstable', 'aborted' blocks as well
    }
}