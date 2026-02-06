#!/bin/bash
echo '>>> 安装user2后台服务'

# 1.生成系统服务启动脚本，使用单引号的EOF可以抑制变量扩展
cat <<'EOF' > user2-service.sh
#!/bin/bash
export JAVA_HOME=/usr/local/java/jdk-17.0.12
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:$CLASSPATH
export JAVA_PATH=${JAVA_HOME}/bin:${JRE_HOME}/bin
export PATH=$PATH:${JAVA_PATH}
${JAVA_HOME}/bin/java -Dfile.encoding=UTF-8 -Xms128m -Xmx256m -Ddruid.mysql.usePingMethod=false -jar feng-user3-biz/target/feng-user3-biz.jar --spring.profiles.active=dev
EOF

# 2.生成user2系统服务脚本
cat <<'EOF' > /etc/systemd/system/user2.service
[Unit]
Description=user2 service
Requires=nacos.service
After=nacos.service
[Service]
WorkingDirectory=/feng/feng-cloud2/feng-user2
ExecStart=/bin/sh user2-service.sh
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
systemctl enable user2
systemctl start user2
#systemctl status user2