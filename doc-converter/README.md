# Document Converter

基于JODConverter项目迁移的本地文档转换器，专注于使用LibreOffice进行文档格式转换。

## 项目特性

- 基于 **Maven 3.9.9** + **JDK 17** + **Spring Boot 3.2.11**
- 使用 **LibreOffice 25.2.5** 进行本地文档转换
- 排除了远程请求和OpenOffice相关依赖
- 所有代码均从JODConverter项目迁移，无需依赖org.jodconverter包
- 符合Spring Doc规范的注释
- 提供REST API接口
- Docker容器化部署支持

## 支持的文档格式

### 输入格式
- **文本文档**: DOC, DOCX, ODT, RTF, TXT
- **电子表格**: XLS, XLSX, ODS, CSV  
- **演示文稿**: PPT, PPTX, ODP
- **PDF文档**: PDF

### 输出格式
- **PDF**: writer_pdf_Export, calc_pdf_Export, impress_pdf_Export, draw_pdf_Export
- **文本文档**: DOC, DOCX, ODT, RTF, TXT
- **电子表格**: XLS, XLSX, ODS, CSV
- **演示文稿**: PPT, PPTX, ODP
- **图片**: PNG, JPG, SVG

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.9.9+
- LibreOffice 24.8+

### 本地运行

1. 克隆项目
```bash
git clone <repository-url>
cd doc-converter
```

2. 编译项目
```bash
mvn clean package
```

3. 运行应用
```bash
java -jar target/doc-converter-1.0.0.jar
```

应用程序将在 `http://localhost:8080` 启动

### API 使用

#### 健康检查
```bash
curl -X GET http://localhost:8080/api/convert/health
```

#### 查看支持的格式
```bash
curl -X GET http://localhost:8080/api/convert/formats
```

#### 文档转换
```bash
# 将DOC文件转换为PDF
curl -X POST -F "file=@document.doc" http://localhost:8080/api/convert/to/pdf -o output.pdf

# 将DOCX文件转换为PDF
curl -X POST -F "file=@document.docx" http://localhost:8080/api/convert/to/pdf -o output.pdf

# 将PDF文件转换为DOCX
curl -X POST -F "file=@document.pdf" http://localhost:8080/api/convert/to/docx -o output.docx
```

## Docker 部署

### 构建镜像

```bash
docker build -t doc-converter:1.0.0 .
```

### 运行容器

```bash
docker run -d \
  --name doc-converter \
  -p 8080:8080 \
  -v /tmp:/tmp \
  doc-converter:1.0.0
```

### Docker Compose (推荐)

创建 `docker-compose.yml`:

```yaml
version: '3.8'

services:
  doc-converter:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    volumes:
      - /tmp:/tmp
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/convert/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
```

运行:
```bash
docker-compose up -d
```

## 配置说明

### 应用配置 (application.yml)

```yaml
doc-converter:
  enabled: true
  office-home: /usr/lib/libreoffice  # LibreOffice安装路径
  host-name: 127.0.0.1
  port-numbers: [2002, 2003]         # LibreOffice进程端口
  process-timeout: 120000            # 进程超时时间 (ms)
  task-execution-timeout: 120000     # 任务执行超时时间 (ms)
  max-tasks-per-process: 200         # 每个进程最大任务数
```

### Docker配置 (application-docker.yml)

Docker环境下的优化配置，包括更长的超时时间和更多的进程端口。

## 兼容性说明

### LibreOffice版本兼容性

- **推荐版本**: LibreOffice 24.8.4+
- **最低版本**: LibreOffice 7.0+
- **注意**: 确保系统安装的LibreOffice版本与项目依赖的版本兼容

### 系统要求

- **Linux**: Ubuntu 20.04+, Debian 11+, CentOS 8+
- **内存**: 最少2GB，推荐4GB+
- **磁盘**: 最少5GB可用空间
- **图形库**: 需要X11库支持 (即使在headless模式)

## 项目结构

```
doc-converter/
├── src/main/java/com/docconverter/
│   ├── DocumentConverterApplication.java     # 主应用类
│   ├── boot/autoconfigure/                   # Spring Boot自动配置
│   ├── controller/                           # REST控制器
│   ├── core/                                 # 核心接口和类
│   │   ├── document/                         # 文档格式相关
│   │   ├── job/                              # 转换作业接口
│   │   ├── office/                           # Office管理接口
│   │   ├── task/                             # 任务接口
│   │   └── util/                             # 工具类
│   └── local/                                # 本地转换实现
│       ├── office/                           # 本地Office管理
│       └── LocalDocumentConverter.java      # 本地转换器
├── src/main/resources/
│   ├── application.yml                       # 应用配置
│   └── application-docker.yml               # Docker环境配置
├── Dockerfile                               # Docker镜像定义
└── pom.xml                                  # Maven配置
```

## 开发说明

### 代码迁移

本项目代码完全从JODConverter项目迁移，主要变更：

1. **包名变更**: `org.jodconverter` → `com.docconverter`
2. **移除依赖**: 排除了org.jodconverter包依赖
3. **功能精简**: 仅保留本地LibreOffice转换功能
4. **注释优化**: 符合Spring Doc规范

### 扩展开发

如需扩展功能，可以：

1. 在 `DocumentFormatRegistry` 中添加新的文档格式
2. 在 `LocalConversionJob` 中实现真正的LibreOffice UNO API调用
3. 添加更多的转换选项和过滤器

## 故障排除

### 常见问题

1. **LibreOffice启动失败**
   - 检查LibreOffice安装路径
   - 确保有足够的内存和磁盘空间
   - 检查端口是否被占用

2. **转换失败**
   - 检查输入文件格式是否支持
   - 验证目标格式是否正确
   - 查看应用程序日志

3. **Docker容器启动失败**
   - 确保有足够的内存分配给容器
   - 检查端口映射是否正确
   - 验证LibreOffice在容器中的安装

### 日志查看

```bash
# 查看应用程序日志
docker logs doc-converter

# 实时查看日志
docker logs -f doc-converter
```

## 许可证

Apache License 2.0

## 贡献

基于JODConverter项目迁移开发，感谢原项目贡献者。