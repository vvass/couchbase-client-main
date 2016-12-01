FROM centos:centos7
MAINTAINER vvass

FROM java:8

ENV SCALA_VERSION 2.11.8
ENV SBT_VERSION 0.13.11

#creat user and password
RUN echo "root:docker" | chpasswd

# Install Scala
## Piping curl directly in tar
RUN \
  curl -fsL http://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
  echo >> /root/.bashrc && \
  echo 'export PATH=~/scala-$SCALA_VERSION/bin:$PATH' >> /root/.bashrc

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb http://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion && \
  apt-get install git

# Prepare environment
ENV JAVA_HOME /opt/java

#create directories
RUN mkdir /opt/couchbase/

#cd to opt folder
WORKDIR /opt/couchbase/

#clone project
RUN git clone https://vvass:4bulgaria@github.com/vvass/couchbase-client2.git
WORKDIR /opt/couchbase/couchbase-client2

EXPOSE 9000
