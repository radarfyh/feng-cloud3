#!/bin/bash
echo '>>> 安装feng-ai后台服务'

# 1.生成系统服务启动脚本，使用单引号的EOF可以抑制变量扩展
cat <<'EOF' > feng-ai-service.sh
#!/bin/bash
export JAVA_HOME=/usr/local/java/jdk-17.0.12
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:$CLASSPATH
export JAVA_PATH=${JAVA_HOME}/bin:${JRE_HOME}/bin
export PATH=$PATH:${JAVA_PATH}
${JAVA_HOME}/bin/java -Dfile.encoding=UTF-8 -Xms128m -Xmx256m -Ddruid.mysql.usePingMethod=false -jar feng-ai-biz/target/feng-ai-biz.jar --spring.profiles.active=dev
EOF

# 2.生成agent系统服务脚本
cat <<'EOF' > /etc/systemd/system/feng-ai.service
[Unit]
Description=feng-ai service
Requires=nacos.service
After=nacos.service
[Service]
WorkingDirectory=/feng/feng-cloud2/feng-ai
ExecStart=/bin/sh feng-ai-service.sh
User=root
RestartSec=30s
Restart=on-failure
LimitNOFILE=655360
TimeoutStopSec=180
[Install]
WantedBy=multi-user.target
EOF

#3.启动hip-gateway后台服务
systemctl daemon-reload
systemctl enable feng-ai
systemctl start feng-ai
#systemctl status feng-ai