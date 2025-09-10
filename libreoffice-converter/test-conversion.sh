#!/bin/bash

# 测试脚本：验证LibreOffice转换功能
echo "=== LibreOffice转换功能测试 ==="

# 创建测试文件
echo "创建测试文件..."
echo "Hello, World! This is a test document for conversion." > test.txt
echo "测试文档内容："
cat test.txt

# 测试LibreOffice命令行转换
echo -e "\n=== 测试LibreOffice命令行转换 ==="
echo "转换 test.txt 到 test.pdf..."
libreoffice --headless --convert-to pdf test.txt --outdir . 2>/dev/null

if [ -f "test.pdf" ]; then
    echo "✅ PDF转换成功！文件大小: $(ls -lh test.pdf | awk '{print $5}')"
else
    echo "❌ PDF转换失败"
fi

# 测试转换回文本
echo -e "\n=== 测试PDF转文本 ==="
echo "转换 test.pdf 到 test_converted.txt..."
libreoffice --headless --convert-to txt test.pdf --outdir . 2>/dev/null

if [ -f "test_converted.txt" ]; then
    echo "✅ 文本转换成功！"
    echo "转换后的内容："
    cat test_converted.txt
else
    echo "❌ 文本转换失败"
fi

# 清理测试文件
echo -e "\n=== 清理测试文件 ==="
rm -f test.txt test.pdf test_converted.txt
echo "测试完成！"