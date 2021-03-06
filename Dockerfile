FROM jenkins/jenkins:lts
USER root

# Install Docker > will use docker.sock to talk to host
RUN apt update -y && apt install -y apt-transport-https ca-certificates curl gnupg2 software-properties-common
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
RUN apt-get update -y && apt-get install -y \
    docker-ce \
    docker-compose
RUN usermod -aG docker,staff jenkins

USER jenkins

COPY ./config-jenkins/plugins.txt /var/jenkins_plugins/plugins.txt
RUN jenkins-plugin-cli --plugin-file /var/jenkins_plugins/plugins.txt