  
node{
    stage('Build New Jupyter Image') {
        checkout scm
        sh "docker build \
            -t jupyter \
            ${WORKSPACE}/jupyter \
            "
    }
    stage('Create Jupyter Container') {
        sh "docker stop jupyter || true"
        sh "docker rm jupyter || true"
        sh "docker run \
            --name jupyter \
            --restart=always \
            -e VIRTUAL_PORT=8888 \
            -e VIRTUAL_HOST=${params.URL} \
            -e LETSENCRYPT_HOST=${params.URL} \
            -e LETSENCRYPT_EMAIL=${JENKINS_ADMIN_EMAIL} \
            -e JUPYTER_ENABLE_LAB=yes \
            -p 8888:8888 \
            -e NB_USER=martin \
            -w /home/martin \
            -d \
            jupyter \
            "
    }
}

// Dropped out of node to release executor while asking for confirmation:
// https://medium.com/faun/using-jenkins-input-step-correctly-2946bd2fd704
timeout(time: 15, unit: "MINUTES") {
    input message: 'Are you sure you want to copy over notebooks? Edited notebooks in the current container volume will be lost.', ok: 'Yes'
}

node {
    stage('Copy Over Notebooks') {
        sh "docker cp ${WORKSPACE}/jupyter/notebooks jupyter:/home/martin/repo-notebooks"
    }
    stage('Jupyter - Log Token') {
        retry(10){
            sleep(10)
            containerCheck = sh(returnStdout: true, script: "docker logs jupyter 2>&1 | grep -q '?token=' && echo 'true'")
            if (containerCheck.contains('true')) {
                echo "Jupyter Started - Token:"
                // Searches for token - takes first line - cuts after = delimiter:
                // https://stackoverflow.com/questions/14093452/grep-only-the-first-match-and-stop
                // https://unix.stackexchange.com/questions/24140/return-only-the-portion-of-a-line-after-a-matching-pattern
                sh "docker logs jupyter 2>&1 | grep '?token=' | head -1 | cut -f2- -d="
            }
            else {
                error "Waiting for Jupyter..."
            }
        }
    }
}