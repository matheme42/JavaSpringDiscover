FROM eclipse-mosquitto

RUN apk update
RUN apk upgrade --update-cache --available
RUN apk add openssl
RUN apk add git
RUN apk add make
RUN apk add vim

ENV PATH="${PATH}:/usr/local/go/bin"

WORKDIR "mosquitto/install"

CMD  tar -C /usr/local -xzf go1.22.2.linux-amd64.tar.gz && mosquitto
