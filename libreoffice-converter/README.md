# LibreOffice Converter

基于LibreOffice的文档转换服务，使用Spring Boot 3.2.11和JDK 17构建。

## 功能特性

- 支持多种文档格式转换：DOC, DOCX, PDF, RTF, ODT, TXT, HTML, XLS, XLSX, ODS, PPT, PPTX
- 基于LibreOffice的本地转换，无需远程依赖
- RESTful API接口
- Spring Boot 3.2.11 + JDK 17
- 完整的API文档（Swagger UI）
- Docker支持

## 技术栈

- **Java**: JDK 17
- **框架**: Spring Boot 3.2.11
- **构建工具**: Maven 3.9.9
- **文档转换**: LibreOffice
- **API文档**: SpringDoc OpenAPI 3
- **容器化**: Docker

## 项目结构

```
libreoffice-converter/
├── src/
│   ├── main/
│   │   ├── java/com/example/converter/
│   │   │   ├── LibreOfficeConverterApplication.java
│   │   │   ├── config/
│   │   │   │   └── ConverterProperties.java
│   │   │   ├── controller/
│   │   │   │   └── ConversionController.java
│   │   │   └── core/
│   │   │       ├── DocumentFormat.java
│   │   │       ├── ConversionException.java
│   │   │       ├── LibreOfficeManager.java
│   │   │       └── DocumentConverter.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/example/converter/
├── Dockerfile
├── .dockerignore
└── pom.xml
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.9.9+
- LibreOffice

### 本地运行

1. 克隆项目
```bash
git clone <repository-url>
cd libreoffice-converter
```

2. 安装LibreOffice
```bash
# Ubuntu/Debian
sudo apt install libreoffice

# CentOS/RHEL
sudo yum install libreoffice
```

3. 构建项目
```bash
mvn clean package -DskipTests
```

4. 运行应用
```bash
java -jar target/libreoffice-converter-1.0.0.jar
```

5. 访问API文档
```
http://localhost:8080/swagger-ui.html
```

### Docker运行

1. 构建镜像
```bash
docker build -t libreoffice-converter:1.0.0 .
```

2. 运行容器
```bash
docker run -p 8080:8080 libreoffice-converter:1.0.0
```

## API接口

### 健康检查
```
GET /api/v1/conversion/health
```

### 获取支持的格式
```
GET /api/v1/conversion/formats
```

### 文件上传转换
```
POST /api/v1/conversion/convert
Content-Type: multipart/form-data

参数:
- file: 要转换的文件
- outputFormat: 目标格式 (pdf, docx, doc, etc.)
```

### 文件路径转换
```
POST /api/v1/conversion/convert-file
Content-Type: application/json

{
  "inputPath": "/path/to/input.docx",
  "outputPath": "/path/to/output.pdf"
}
```

## 支持的格式

| 格式 | 扩展名 | MIME类型 | 说明 |
|------|--------|----------|------|
| DOC | .doc | application/msword | Microsoft Word 97-2003 |
| DOCX | .docx | application/vnd.openxmlformats-officedocument.wordprocessingml.document | Microsoft Word 2007+ |
| PDF | .pdf | application/pdf | Portable Document Format |
| RTF | .rtf | application/rtf | Rich Text Format |
| ODT | .odt | application/vnd.oasis.opendocument.text | OpenDocument Text |
| TXT | .txt | text/plain | Plain Text |
| HTML | .html | text/html | HyperText Markup Language |
| XLS | .xls | application/vnd.ms-excel | Microsoft Excel 97-2003 |
| XLSX | .xlsx | application/vnd.openxmlformats-officedocument.spreadsheetml.sheet | Microsoft Excel 2007+ |
| ODS | .ods | application/vnd.oasis.opendocument.spreadsheet | OpenDocument Spreadsheet |
| PPT | .ppt | application/vnd.ms-powerpoint | Microsoft PowerPoint 97-2003 |
| PPTX | .pptx | application/vnd.openxmlformats-officedocument.presentationml.presentation | Microsoft PowerPoint 2007+ |

## 配置

### application.yml

```yaml
converter:
  office-home: /usr/lib/libreoffice  # LibreOffice安装路径
  port-numbers: [8100, 8101, 8102, 8103, 8104]  # 端口号
  max-tasks-per-process: 100  # 每个进程最大任务数
  process-timeout: 120000  # 进程超时时间(毫秒)
  retry-timeout: 30000  # 重试超时时间(毫秒)
  kill-existing-process: true  # 启动时是否杀死现有进程
```

## 测试

### 单元测试
```bash
mvn test
```

### 集成测试
```bash
mvn test -Dtest.integration=true
```

### 手动测试转换功能

1. 创建测试文档
```bash
echo "Hello, World! This is a test document." > test.txt
```

2. 使用LibreOffice命令行测试
```bash
libreoffice --headless --convert-to pdf test.txt --outdir .
```

3. 使用API测试
```bash
curl -X POST -F "file=@test.txt" -F "outputFormat=pdf" http://localhost:8080/api/v1/conversion/convert -o output.pdf
```

## 故障排除

### 常见问题

1. **LibreOffice未找到**
   - 确保LibreOffice已正确安装
   - 检查`converter.office-home`配置路径

2. **转换失败**
   - 检查输入文件格式是否支持
   - 查看应用日志获取详细错误信息
   - 确保有足够的磁盘空间

3. **内存不足**
   - 调整JVM内存设置：`-Xmx512m -Xms256m`
   - 减少`max-tasks-per-process`配置

### 日志配置

```yaml
logging:
  level:
    com.example.converter: DEBUG
    org.springframework: WARN
```

## 开发指南

### 添加新格式支持

1. 在`DocumentFormat`枚举中添加新格式
2. 更新`DocumentConverter`中的转换逻辑
3. 添加相应的测试用例

### 性能优化

1. 调整LibreOffice进程池大小
2. 优化JVM参数
3. 使用SSD存储临时文件
4. 考虑使用缓存机制

## 许可证

本项目基于MIT许可证开源。

## 贡献

欢迎提交Issue和Pull Request来改进项目。

## 更新日志

### v1.0.0
- 初始版本发布
- 支持基本的文档转换功能
- 提供RESTful API接口
- 支持Docker容器化部署