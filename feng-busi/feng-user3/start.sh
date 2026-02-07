#!/bin/sh

# 校验入参，如果为空则设置默认值为 dev
if [ -z "$1" ]; then
  MODULE="dev"
  echo "未提供入参，使用默认值: $MODULE"
else
  MODULE=$1
  echo "使用入参: $MODULE"
fi

echo -------------------------------------------
echo "启动 user2，环境: $MODULE"
echo -------------------------------------------

# JAVA_HOME
export JAVA_HOME=/usr/local/java/jdk-21.0.10
# 设置项目代码路径
export _EXECJAVA="$JAVA_HOME/bin/java"

# 查找 jar 包名称
JAVANAME=$(ls feng-user3-biz/target/*.jar)

# 检查是否找到 jar 包
if [ -z "$JAVANAME" ]; then
  echo "错误: 未找到 jar 包，请检查路径是否正确。"
  exit 1
fi

# 启动类
nohup $_EXECJAVA -Dfile.encoding=UTF-8 -Xms128m -Xmx256m -Ddruid.mysql.usePingMethod=false -jar "$JAVANAME" --spring.profiles.active=$MODULE >startup.log 2>&1 &
