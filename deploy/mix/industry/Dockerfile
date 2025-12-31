FROM eclipse-temurin:21-jre

# /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime &&
RUN  echo 'Asia/Shanghai' >/etc/timezone

COPY *.jar /app.jar

COPY /html /usr/local/openresty/nginx/html

COPY nginx.conf /usr/local/openresty/nginx/conf/nginx.conf

# 暴露端口
EXPOSE 8001

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /app.jar  $PARAMS"]

