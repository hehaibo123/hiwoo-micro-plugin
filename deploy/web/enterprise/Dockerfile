FROM openresty/openresty:1.27.1.2-3-bullseye-fat AS final

COPY /html /usr/local/openresty/nginx/html

COPY nginx.conf /usr/local/openresty/nginx/conf/nginx.conf

# 暴露端口
EXPOSE 183