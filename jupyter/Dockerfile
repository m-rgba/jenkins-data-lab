FROM jupyter/all-spark-notebook

USER root

# JDBC Drivers for Postgres and MySQL
RUN apt-get update && apt-get install -y \
    libmysql-java libpostgresql-jdbc-java libpostgresql-jdbc-java-doc

# Make user directory
RUN mkdir /home/martin
RUN chown -R $NB_USER /home/martin

# Back to user
USER $NB_USER

RUN conda install --quiet --yes \
    'boto3' \
    'psycopg2' \
    'papermill'