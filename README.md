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


### 启动minikube k8s集群

```
minikube start --image-mirror-country='cn' --nodes 2 --kubernetes-version=v1.18.3
```

```
$ kubectl get nodes
NAME           STATUS     ROLES    AGE   VERSION
minikube       Ready      master   65s   v1.18.3
minikube-m02   NotReady   <none>   30s   v1.18.3
```

### 安装prometheus

```
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
helm install prometheus prometheus-community/kube-prometheus-stack
```

开启Prometheus UI端口转发：

```
kubectl port-forward $(kubectl get pods --selector "app.kubernetes.io/name=prometheus" --output=name) 9090
```

开启grafana UI端口转发：

```
kubectl port-forward $(kubectl get pods --selector "app.kubernetes.io/name=grafana" --output=name) 3000
```

### 发布spring boot应用到k8s

```
kubectl apply -f ./servicemonitor-deployment.yaml
```

在`servicemonitor-deployment.yaml`里定义了三个东东。要注意的是`Service`和`ServiceMonitor`都要打上label: `release: prometheus`。

#### Deployment

```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: expose-prometheus-demo
  labels:
    app: expose-prometheus-demo
spec:
  replicas: 1
  selector:
    matchLabels:
      app: expose-prometheus-demo
  template:
    metadata:
      labels:
        app: expose-prometheus-demo
    spec:
      containers:
      - name: expose-prometheus-demo
        image: hengyunabc/expose-prometheus-demo:0.0.1-SNAPSHOT
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
        - containerPort: 8090
```

#### Service

```yaml
---
apiVersion: v1
kind: Service
metadata:
  name: expose-prometheus-demo-service
  labels:
    app: expose-prometheus-demo
    release: prometheus
spec:
  selector:
    app: expose-prometheus-demo
  ports:
    - protocol: TCP
      name: http-traffic
      port: 8080
      targetPort: 8080
    - protocol: TCP
      name: metric-traffic
      port: 8090
      targetPort: 8090
```

#### ServiceMonitor

```yaml
---
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: expose-prometheus-demo-service-monitor
  labels:
    app: expose-prometheus-demo
    release: prometheus
spec:
  selector:
    matchLabels:
      app: expose-prometheus-demo
  endpoints:
  - port: metric-traffic
    path: "/actuator/prometheus"
```

### 配置grafana

* http://localhost:3000/

> grafana 默认的用户名密码是： `admin/prom-operator`

首先导入下面的开源`JVM (Micrometer)` dashboard：

* https://grafana.com/grafana/dashboards/4701

在Grafana UI里，在`Dashboards`,`Manage`,`Import`，填入`4701`。导入成功之后，就可以在Dashboards里找到`JVM (Micrometer)`。

打开之后可以看到 expose-prometheus-demo的监控信息。

