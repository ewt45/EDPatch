export DISPLAY=:0

. /opt/recipe/util/progress.sh

progress "-1" "已启动 Wine任务管理器 到 XSDL，请回到XSDL"

wine taskmgr

