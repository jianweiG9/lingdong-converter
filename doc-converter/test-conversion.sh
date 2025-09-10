#!/bin/bash

# Document Converter 转换功能测试脚本

set -e

BASE_URL="http://localhost:8081"
TEST_DIR="test-files"

echo "=== Document Converter 转换功能测试 ==="

# 检查应用程序是否运行
if ! curl -f $BASE_URL/api/convert/health &> /dev/null; then
    echo "错误: 应用程序未运行，请先启动应用程序"
    exit 1
fi

echo "✅ 应用程序运行正常"

# 创建测试目录
mkdir -p $TEST_DIR

# 测试1: 健康检查
echo "1. 测试健康检查..."
HEALTH_RESPONSE=$(curl -s $BASE_URL/api/convert/health)
echo "健康检查响应: $HEALTH_RESPONSE"

# 测试2: 查看支持格式
echo "2. 查看支持的格式..."
curl -s $BASE_URL/api/convert/formats

# 测试3: 创建测试文档
echo "3. 创建测试文档..."
cat > $TEST_DIR/test-document.txt << EOF
Document Converter 测试文档
========================

这是一个测试文档，用于验证文档转换功能。

支持的转换:
• DOC → PDF
• DOCX → PDF  
• PDF → DOCX
• TXT → PDF

测试内容:
Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.

中文内容测试:
这是中文内容测试，验证字符编码是否正确处理。

结束。
EOF

echo "✅ 测试文档创建完成"

# 测试4: TXT转PDF
echo "4. 测试 TXT → PDF 转换..."
if curl -X POST -F "file=@$TEST_DIR/test-document.txt" \
        $BASE_URL/api/convert/to/pdf \
        -o $TEST_DIR/test-output.pdf \
        -s -w "HTTP状态: %{http_code}\n"; then
    
    if [ -f "$TEST_DIR/test-output.pdf" ] && [ -s "$TEST_DIR/test-output.pdf" ]; then
        echo "✅ TXT → PDF 转换成功"
        echo "输出文件: $TEST_DIR/test-output.pdf"
        echo "文件大小: $(ls -lh $TEST_DIR/test-output.pdf | awk '{print $5}')"
    else
        echo "❌ TXT → PDF 转换失败: 输出文件为空或不存在"
    fi
else
    echo "❌ TXT → PDF 转换请求失败"
fi

# 测试5: 创建简单的HTML文档进行转换测试
echo "5. 创建HTML测试文档..."
cat > $TEST_DIR/test-document.html << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Document Converter Test</title>
    <meta charset="UTF-8">
</head>
<body>
    <h1>Document Converter 测试</h1>
    <h2>功能特性</h2>
    <ul>
        <li>支持多种文档格式转换</li>
        <li>基于LibreOffice引擎</li>
        <li>REST API接口</li>
        <li>Docker容器化部署</li>
    </ul>
    
    <h2>支持格式</h2>
    <table border="1">
        <tr>
            <th>输入格式</th>
            <th>输出格式</th>
        </tr>
        <tr>
            <td>DOC, DOCX</td>
            <td>PDF, TXT, ODT</td>
        </tr>
        <tr>
            <td>XLS, XLSX</td>
            <td>PDF, CSV, ODS</td>
        </tr>
        <tr>
            <td>PPT, PPTX</td>
            <td>PDF, ODP</td>
        </tr>
    </table>
</body>
</html>
EOF

# 测试6: HTML转PDF
echo "6. 测试 HTML → PDF 转换..."
if curl -X POST -F "file=@$TEST_DIR/test-document.html" \
        $BASE_URL/api/convert/to/pdf \
        -o $TEST_DIR/test-html-output.pdf \
        -s -w "HTTP状态: %{http_code}\n"; then
    
    if [ -f "$TEST_DIR/test-html-output.pdf" ] && [ -s "$TEST_DIR/test-html-output.pdf" ]; then
        echo "✅ HTML → PDF 转换成功"
        echo "输出文件: $TEST_DIR/test-html-output.pdf"
        echo "文件大小: $(ls -lh $TEST_DIR/test-html-output.pdf | awk '{print $5}')"
    else
        echo "❌ HTML → PDF 转换失败: 输出文件为空或不存在"
    fi
else
    echo "❌ HTML → PDF 转换请求失败"
fi

# 测试结果总结
echo ""
echo "=== 测试结果总结 ==="
echo "测试文件目录: $TEST_DIR"
ls -la $TEST_DIR/

echo ""
echo "=== 转换功能测试完成 ==="
echo ""
echo "注意: 当前实现为基础版本，实际的LibreOffice UNO API集成需要进一步开发"
echo "建议下一步: 集成完整的LibreOffice UNO API以实现真正的文档转换"