### Istio service mesh with grpc and rest microservices

#### Project structure
- web       <- rest microservice(aka api gateway)
- users     <- grpc microservice
- insurance <- grpc microservice

routes: web, web->a , web->a->b, web->b , web->b->a
/api/simple_check
/api/simple_check_a
/api/simple_check_b
/api/complex_check_a
/api/complex_check_b

#### Original examples

[//]: # (https://github.com/marcel-dempers/docker-development-youtube-series)
https://stackoverflow.com/questions/72073613/istio-installation-failed-apple-silicon-m1

#### Prerequisites
connect to kubernetes cluster(local)
- sbt docker:publishLocal
- istioctl install ( I got docker pull issue from local docker desktop kubernetes, describe failed pod and pull image manually )
- istioctl operator init --hub=ghcr.io/resf/istio
- ```
kubectl apply -f - <<EOF
apiVersion: install.istio.io/v1alpha1
kind: IstioOperator
metadata:
  namespace: istio-system
  name: example-istiocontrolplane
spec:
  hub: ghcr.io/resf/istio
  profile: demo
EOF
- kubectl apply -f ${ISTIO_HOME}/samples/addons/

#### Setup
```bash
kubectl create configmap web   --from-file=./deployment/kubernetes/applications/web/config.env
kubectl create configmap users --from-file=./deployment/kubernetes/applications/users/config.env
kubectl apply -f ./deployment/kubernetes/applications/web/
kubectl apply -f ./deployment/kubernetes/applications/users/
```

#### port forwards
kubectl port-forward svc/kiali 20001:20001 -n istio-system
kubectl port-forward svc/web 9000:9000 -n default

