## 动态线程池组件
> 注：默认已经安装有了 Grafana 和 Prometheus 。
> 前端地址：https://gitee.com/Herbert0501/dynamic-thread-pool-web
### 1. 配置

更改 [grafana.ini](./docs/dev-ops/grafana.ini) 配置文件内容。
```ini
[auth.anonymous]
enabled=true # 允许匿名访问
org_name=dynamic-thread-pool-guest # 默认为 dynamic-thread-pool-guest
org_role=Viewer # 默认为 Viewer
hide_version=true # 隐藏版本信息
[security]
allow_embedding = true # 允许嵌入
```
更改 [prometheus.yml](./docs/dev-ops/prometheus.yml) 配置文件内容。
```yaml
scrape_configs:
  # 替换为你的应用名称
  - job_name: 'spring_boot_application'
    metrics_path: '/actuator/prometheus'
    static_configs:
      # 替换为你的应用地址
      - targets: ['192.168.192.160:8095']
```
更改 [docker-compose.yml](./docs/dev-ops/docker-compose.yml) 配置文件内容。
要获取grafanaUrl，则先导入 [dynamic-thread-pool-grafana-dashboard.json](./docs/dev-ops/动态线程池监控-dashboard.json) 到 Grafana。
然后获取 grafanaUrl，可以取消勾选锁定区间。
![如图](./docs/dev-ops/grafana.png)