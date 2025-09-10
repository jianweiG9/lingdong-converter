#!/bin/bash

# Document Converter 部署脚本

set -e

echo "=== Document Converter 部署脚本 ==="

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "错误: Docker 未安装，请先安装Docker"
    exit 1
fi

# 检查Docker是否运行
if ! docker info &> /dev/null; then
    echo "错误: Docker 服务未运行，请启动Docker服务"
    exit 1
fi

# 构建应用程序
echo "1. 构建应用程序..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "错误: Maven构建失败"
    exit 1
fi

# 构建Docker镜像
echo "2. 构建Docker镜像..."
docker build -t doc-converter:1.0.0 .

if [ $? -ne 0 ]; then
    echo "错误: Docker镜像构建失败"
    exit 1
fi

# 停止现有容器
echo "3. 停止现有容器..."
docker-compose down 2>/dev/null || true

# 启动新容器
echo "4. 启动新容器..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "错误: 容器启动失败"
    exit 1
fi

# 等待应用程序启动
echo "5. 等待应用程序启动..."
for i in {1..30}; do
    if curl -f http://localhost:8080/api/convert/health &> /dev/null; then
        echo "应用程序启动成功!"
        break
    fi
    echo "等待中... ($i/30)"
    sleep 2
done

# 检查最终状态
if curl -f http://localhost:8080/api/convert/health &> /dev/null; then
    echo "✅ 部署成功!"
    echo "应用程序地址: http://localhost:8080"
    echo "健康检查: http://localhost:8080/api/convert/health"
    echo "支持格式: http://localhost:8080/api/convert/formats"
    echo ""
    echo "转换示例:"
    echo "curl -X POST -F \"file=@your-document.docx\" http://localhost:8080/api/convert/to/pdf -o output.pdf"
else
    echo "❌ 部署失败，请检查日志:"
    echo "docker logs doc-converter"
    exit 1
fi