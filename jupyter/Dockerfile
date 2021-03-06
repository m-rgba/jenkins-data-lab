FROM jupyter/all-spark-notebook

USER root

# Make user directory
RUN mkdir /home/martin
RUN chown -R $NB_USER /home/martin

# Back to user
USER $NB_USER

RUN conda install --quiet --yes \
    'boto3' \
    'psycopg2' \
    'papermill'