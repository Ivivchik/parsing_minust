FROM ubuntu:20.04
LABEL authors="Ivan Petrov"
ARG USER_INSTALL_NAME="ivipetrov"
ARG USER_INSTALL_HOME="/home/${USER_INSTALL_NAME}"
RUN useradd -u 5000 -d ${USER_INSTALL_HOME} -s /bin/bash -m ${USER_INSTALL_NAME}
RUN env
ENV PROJECT_DIR="$USER_INSTALL_HOME/${PROJECT_NAME}/parsing_minust" DEBIAN_FRONTEND=noninteractive
RUN mkdir ${PROJECT_DIR}
RUN apt-get update && apt-get install -y apt-transport-https
RUN apt install -y openjdk-8-jdk-headless
RUN apt install -y openjdk-8-jre-headless
RUN apt install -y wget
RUN apt install -y dbus-x11
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt install -y ./google-chrome-stable_current_amd64.deb
COPY parsing_scala.jar ${PROJECT_DIR} 
COPY chromedriver ${PROJECT_DIR}
RUN chown -R $USER_INSTALL_NAME ${PROJECT_DIR} 
RUN chmod -R 777 ${PROJECT_DIR}
USER ${USER_INSTALL_NAME}
WORKDIR ${PROJECT_DIR}
RUN ls -l ${PROJECT_DIR}
CMD env && java -jar parsing_scala.jar