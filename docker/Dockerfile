FROM openjdk:11-jre
ARG DEB_PATH
RUN echo debpath=${DEB_PATH}
COPY ${DEB_PATH} ./project.deb

RUN mkdir /etc/logentries \
    && chmod -R +x /etc/logentries \
    && mkdir -p /usr/lib/jvm
COPY properties /etc/logentries/orchestrator_test_dropwizard.properties
RUN apt-get update \
    && apt-get install -y apt-transport-https \
    && echo "deb [arch=amd64] https://deb.logentries.net/stretch production main"  >> /etc/apt/sources.list.d/deblogentriesnet.list \
    && curl https://deb.logentries.net/key | apt-key add - \
    && apt-get update \
    && apt-get install -y leconfigure-current supervisor \
    && dpkg --configure -a \
    && dpkg -i project.deb \
    && ln -s /usr/local/openjdk-11 /usr/lib/jvm/java11 \
    && mkdir /var/log/logentries && touch /var/log/logentries/orchestrator_test_dropwizard.log

CMD ["tail", "-F", "/var/log/logentries/orchestrator_test_dropwizard.log"]