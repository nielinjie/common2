#docker run -p <宿主机端口>:<容器端口> <镜像名>
 docker run -d -p 8888:8080 plantuml/plantuml-server


#这个不行，没有中文字体。虽然有比较好看的theme
#docker run --d -p 8888:8080 ghcr.io/rakutentech/plantuml:main
