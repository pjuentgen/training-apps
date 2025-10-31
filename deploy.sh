#!/bin/bash

# Build and Deploy Script for Spring Boot Demo
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
IMAGE_NAME="spring-boot-demo"
REGISTRY="ghcr.io"
NAMESPACE="spring-boot-demo"

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Parse command line arguments
ENVIRONMENT="local"
BUILD_ONLY=false
DEPLOY_ONLY=false
SKIP_TESTS=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--environment)
            ENVIRONMENT="$2"
            shift 2
            ;;
        --build-only)
            BUILD_ONLY=true
            shift
            ;;
        --deploy-only)
            DEPLOY_ONLY=true
            shift
            ;;
        --skip-tests)
            SKIP_TESTS=true
            shift
            ;;
        -h|--help)
            echo "Usage: $0 [OPTIONS]"
            echo "Options:"
            echo "  -e, --environment ENV   Target environment (local, staging, production)"
            echo "  --build-only           Only build the application and Docker image"
            echo "  --deploy-only          Only deploy (skip build steps)"
            echo "  --skip-tests           Skip running tests"
            echo "  -h, --help             Show this help message"
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            exit 1
            ;;
    esac
done

log_info "Starting deployment for environment: $ENVIRONMENT"

# Build phase
if [[ "$DEPLOY_ONLY" != true ]]; then
    log_info "Building Spring Boot application..."
    
    if [[ "$SKIP_TESTS" == true ]]; then
        mvn clean package -DskipTests
    else
        mvn clean test package
    fi
    
    log_info "Building Docker image..."
    docker build -t $IMAGE_NAME:latest .
    
    if [[ "$ENVIRONMENT" != "local" ]]; then
        # Tag for registry
        docker tag $IMAGE_NAME:latest $REGISTRY/$IMAGE_NAME:latest
        docker tag $IMAGE_NAME:latest $REGISTRY/$IMAGE_NAME:$(git rev-parse --short HEAD)
        
        log_info "Pushing Docker image to registry..."
        docker push $REGISTRY/$IMAGE_NAME:latest
        docker push $REGISTRY/$IMAGE_NAME:$(git rev-parse --short HEAD)
    fi
fi

# Deploy phase
if [[ "$BUILD_ONLY" != true ]]; then
    case $ENVIRONMENT in
        local)
            log_info "Starting local deployment with Docker Compose..."
            docker-compose down
            docker-compose up -d
            log_info "Application is starting at http://localhost:8080"
            ;;
        staging|production)
            log_info "Deploying to Kubernetes ($ENVIRONMENT)..."
            
            # Check if kubectl is available
            if ! command -v kubectl &> /dev/null; then
                log_error "kubectl is not installed or not in PATH"
                exit 1
            fi
            
            # Check if kustomize is available
            if ! command -v kustomize &> /dev/null; then
                log_warn "kustomize not found, using kubectl kustomize"
                KUSTOMIZE_CMD="kubectl kustomize"
            else
                KUSTOMIZE_CMD="kustomize build"
            fi
            
            # Apply manifests
            $KUSTOMIZE_CMD k8s/$ENVIRONMENT | kubectl apply -f -
            
            # Wait for deployment to be ready
            kubectl wait --for=condition=available --timeout=300s deployment/spring-boot-demo -n $NAMESPACE-$ENVIRONMENT
            
            log_info "Deployment completed successfully!"
            ;;
        *)
            log_error "Unknown environment: $ENVIRONMENT"
            exit 1
            ;;
    esac
fi

log_info "Deployment process completed!"