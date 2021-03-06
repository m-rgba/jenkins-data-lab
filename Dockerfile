FROM jenkins/jenkins:lts
COPY ./config-jenkins/plugins.txt /var/jenkins_plugins/plugins.txt
RUN jenkins-plugin-cli --plugin-file /var/jenkins_plugins/plugins.txt