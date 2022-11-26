https://istio.io/latest/blog/2021/simple-vms/
https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/


```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.6.1/aio/deploy/recommended.yaml
kubectl apply -f ./deployment/kubernetes/dashboard/
kubectl -n kubernetes-dashboard create token admin-user
```
