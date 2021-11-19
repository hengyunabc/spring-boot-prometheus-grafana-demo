## spring boot expose prometheus demo

### 构建jar

```
./mvnw clean package -DskipTests
```

### 构建docker镜像

```
docker build . -t hengyunabc/expose-prometheus-demo:0.0.1-SNAPSHOT
```

本地启动测试：

```
docker run -it -p8080:8080 -p8090:8090 hengyunabc/expose-prometheus-demo:0.0.1-SNAPSHOT
```

访问： http://localhost:8090/actuator/prometheus


