FROM ubuntu:14.04

#
# UTF-8 by default
#
RUN locale-gen en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

#
# Pull Zulu OpenJDK binaries from official repository:
#
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0x219BD9C9
RUN echo "deb http://repos.azulsystems.com/ubuntu `lsb_release -cs` main" >> /etc/apt/sources.list.d/zulu.list
RUN apt-get -qq update
RUN apt-get -qqy install zulu-8=8.8.0.3

COPY . .
RUN ./gradlew build
EXPOSE 4567

CMD ["./gradlew", "run"]
