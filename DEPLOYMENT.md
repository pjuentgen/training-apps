# Spring Boot Demo - Deployment Guide

This guide covers building, containerizing, and deploying the Spring Boot Demo application to various environments.

## 🐳 Docker

### Building the Docker Image

```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t spring-boot-demo:latest .
```

### Running with Docker

```bash
# Run single container
docker run -p 8080:8080 spring-boot-demo:latest

# Run with docker-compose (includes nginx reverse proxy)
docker-compose up -d
```

The application will be available at:
- Direct access: http://localhost:8080
- Through nginx: http://localhost

## ☸️ Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (1.19+)
- kubectl configured
- kustomize (optional, but recommended)

### Quick Deploy

Use the provided deployment script:

```bash
# Deploy to staging
./deploy.sh --environment staging

# Deploy to production  
./deploy.sh --environment production

# Local development with Docker Compose
./deploy.sh --environment local
```

### Manual Deployment

#### Staging Environment

```bash
# Apply staging configuration
kubectl apply -k k8s/staging/

# Check deployment status
kubectl get pods -n spring-boot-demo-staging
kubectl get services -n spring-boot-demo-staging
```

#### Production Environment

```bash
# Apply production configuration
kubectl apply -k k8s/production/

# Check deployment status
kubectl get pods -n spring-boot-demo-production
kubectl get services -n spring-boot-demo-production
```

### Kubernetes Resources

The deployment includes:

- **Deployment**: Application pods with resource limits and health checks
- **Service**: ClusterIP service for internal communication
- **Ingress**: External access with TLS termination
- **ConfigMap**: Application configuration
- **HorizontalPodAutoscaler**: Automatic scaling based on CPU/memory
- **PodDisruptionBudget**: Ensures availability during updates
- **NetworkPolicy**: Network security rules
- **RBAC**: Service account and permissions

### Environment Differences

| Resource | Staging | Production |
|----------|---------|------------|
| Replicas | 2 | 5 |
| Memory Request | 128Mi | 512Mi |
| Memory Limit | 256Mi | 1Gi |
| CPU Request | 250m | 500m |
| CPU Limit | 500m | 1000m |
| HPA Min/Max | 1/5 | 3/20 |

## 🚀 CI/CD Pipeline

### GitHub Actions

The repository includes a comprehensive CI/CD pipeline that:

1. **Test**: Runs unit tests and generates reports
2. **Build**: Compiles application and builds multi-arch Docker images
3. **Security**: Scans for vulnerabilities with Trivy
4. **Deploy**: Automatically deploys to staging/production

#### Required Secrets

Configure these secrets in your GitHub repository:

```bash
# Container registry (if using private registry)
GITHUB_TOKEN  # Automatically provided

# Kubernetes clusters
KUBE_CONFIG_STAGING     # Base64 encoded kubeconfig for staging
KUBE_CONFIG_PRODUCTION  # Base64 encoded kubeconfig for production
```

#### Workflow Triggers

- **Push to main**: Deploy to production
- **Push to develop**: Deploy to staging  
- **Pull requests**: Run tests only
- **Releases**: Deploy tagged version to production

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profiles | `default` |
| `SERVER_PORT` | Application port | `8080` |
| `JAVA_OPTS` | JVM options | `-Xmx512m -Xms256m` |

### Health Checks

- **Liveness**: `GET /api/stats` (used by Kubernetes)
- **Readiness**: `GET /api/stats` (used by Kubernetes)

### Scaling

The application automatically scales based on:
- CPU utilization (target: 70%)
- Memory utilization (target: 80%)

Manual scaling:
```bash
# Scale to 5 replicas
kubectl scale deployment spring-boot-demo --replicas=5 -n spring-boot-demo-production
```

## 🔍 Monitoring

### Logs

```bash
# View application logs
kubectl logs -f deployment/spring-boot-demo -n spring-boot-demo-production

# View logs from all pods
kubectl logs -f -l app=spring-boot-demo -n spring-boot-demo-production
```

### Metrics

The application exposes metrics endpoints:
- `/actuator/health` - Health status
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

## 🛠️ Troubleshooting

### Common Issues

1. **Image pull errors**
   ```bash
   # Check if image exists
   docker pull ghcr.io/your-username/training-apps:latest
   ```

2. **Pod startup failures**
   ```bash
   # Check pod events
   kubectl describe pod <pod-name> -n <namespace>
   
   # Check application logs
   kubectl logs <pod-name> -n <namespace>
   ```

3. **Service not accessible**
   ```bash
   # Check service endpoints
   kubectl get endpoints -n <namespace>
   
   # Test service connectivity
   kubectl run test-pod --image=busybox -it --rm -- wget -qO- http://spring-boot-demo-service
   ```

### Cleanup

```bash
# Remove staging environment
kubectl delete -k k8s/staging/

# Remove production environment  
kubectl delete -k k8s/production/

# Remove Docker containers
docker-compose down
docker system prune -f
```

## 📋 Checklist

Before deploying to production:

- [ ] Update domain names in ingress configuration
- [ ] Configure TLS certificates
- [ ] Set up monitoring and alerting
- [ ] Configure backup strategies
- [ ] Review security policies
- [ ] Test disaster recovery procedures
- [ ] Update DNS records
- [ ] Configure log aggregation

## 🔗 Useful Commands

```bash
# Build and test locally
./deploy.sh --environment local

# Build only (skip deployment)
./deploy.sh --build-only

# Deploy only (skip build)
./deploy.sh --deploy-only --environment staging

# Skip tests during build
./deploy.sh --skip-tests
```