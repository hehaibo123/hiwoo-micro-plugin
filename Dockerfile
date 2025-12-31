FROM eclipse-temurin:21-jre AS java-builder

RUN  echo 'Asia/Shanghai' >/etc/timezone

COPY *.jar /app.jar

# 暴露端口
EXPOSE 8001

FROM openresty/openresty:1.27.1.2-3-bullseye-fat AS final

# 安装 supervisor 用于管理多个进程
RUN apt-get update && \
    apt-get install -y  supervisor && \
    apt-get clean

# 创建应用目录结构
RUN mkdir -p /var/log/supervisor

COPY --from=java-builder /app.jar /app.jar

COPY /jre21 /jre21

COPY /html /usr/local/openresty/nginx/html

COPY nginx.conf /usr/local/openresty/nginx/conf/nginx.conf

# 复制 supervisor 配置文件
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# 暴露端口
EXPOSE 181 8001

# 使用 supervisor 启动所有服务
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]