# 拉取基础镜像
FROM openjdk:8-jre
# 设置作者信息
MAINTAINER s

# 把hello_springboot.jar添加到容器里，并重命名为app.jar
# 因为hello_springboot.jar和Dockerfile在同一个目录下，所以只写文件名即可
ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} mayizhuangyuan.jar
RUN mkdir -p /config
# 执行命令，此处运行app.jar
RUN bash -c 'touch /mayizhuangyuan.jar'
ENTRYPOINT ["java","-jar","mayizhuangyuan.jar","--spring.config.location=/config/application.properties"]
