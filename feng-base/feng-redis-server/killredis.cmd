
@echo off

chcp 65001 >nul

:: 检测管理员权限
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo 请右键选择"以管理员身份运行"
    pause
    exit /b
)

:: 查询端口占用
echo 正在检查6379端口占用...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":6379"') do (
    set pid=%%a
    for /f "tokens=1,*" %%b in ('tasklist /FI "PID eq %%a" /NH') do (
        set pname=%%b
    )
)

if not defined pid (
    echo 6379端口未被占用
    pause
    exit /b
)

:: 显示进程信息
echo 发现进程占用:
echo [PID] %pid%  [名称] %pname%
echo.
choice /c yn /m "确认终止进程？(y/n)"
if %errorlevel% equ 2 exit /b

:: 终止进程
taskkill /F /PID %pid%
if %errorlevel% equ 0 (
    echo 进程已终止
) else (
    echo 终止失败，错误代码: %errorlevel%
)
pause
