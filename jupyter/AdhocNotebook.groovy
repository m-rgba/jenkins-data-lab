node{
    // Create UUID for container name
    env.runUUID = UUID.randomUUID().toString()

    stage('Build New Jupyter Image') {
        checkout scm
        sh "docker build \
            -t jupyter \
            ${WORKSPACE}/jupyter \
            "
    }
    stage('Create Jupyter Container') {
        sh "docker run \
            --name ${runUUID}-jupyter \
            --restart=no \
            -e NB_USER=martin \
            -w /home/martin \
            -d \
            jupyter \
            "
    }
    stage('Copy Notebook and Run') {
        catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
            sh "docker cp ${WORKSPACE}/${params.filePath} ${runUUID}-jupyter:/home/martin/${runUUID}.ipynb"
            sh "docker exec ${runUUID}-jupyter papermill --progress-bar /home/martin/${runUUID}.ipynb /home/martin/${runUUID}-output.ipynb"
        }
    }
    stage('Save Result Artifacts & Container Cleanup') {
        sh "mkdir -p ${WORKSPACE}/artifacts/"
        sh "docker exec ${runUUID}-jupyter jupyter nbconvert --to html /home/martin/${runUUID}-output.ipynb"
        sh "docker cp ${runUUID}-jupyter:/home/martin/${runUUID}-output.html ${WORKSPACE}/artifacts/${runUUID}-output.html"
        archiveArtifacts "artifacts/*"

        sh "docker stop ${runUUID}-jupyter || true"
        sh "docker rm -v ${runUUID}-jupyter || true"
    }
}