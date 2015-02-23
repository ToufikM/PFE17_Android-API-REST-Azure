rd "\%ROLENAME%"

if defined DEPLOYROOT_PATH set DEPLOYROOT=%DEPLOYROOT_PATH%
if defined DEPLOYROOT (
	mklink /J "\%ROLENAME%" "%DEPLOYROOT%"
) else (
	mklink /J "\%ROLENAME%" "%ROLEROOT%\approot"
)

set DEPLOYROOT=\%ROLENAME%
set SERVER_APPS_LOCATION=%DEPLOYROOT%

set JAVA_HOME=%DEPLOYROOT%\zulu1.8.0_25-8.4.0.1-win64
set PATH=%JAVA_HOME%\bin;%PATH%
set GLASSFISH_HOME=%DEPLOYROOT%\glassfish4\glassfish
set SERVER_APPS_LOCATION=%GLASSFISH_HOME%\domains\domain1\autodeploy


cmd /c util\wash.cmd blob download "zulu1.8.0_25-8.4.0.1-win64.zip" "zulu1.8.0_25-8.4.0.1-win64.zip" eclipsedeploy cloudshotdeploy "Q8Ib6UhG2aBT/Kmjl2/qd8GZCG/8fdJ34lNRkCqZUW623zet5/bnKx4l21+ROlW0kdGdmGOcu7jfpKGvt/tZag==" "http://core.windows.net"
if not exist "zulu1.8.0_25-8.4.0.1-win64.zip" (
	cmd /c util\wash.cmd file download "http://azure.azulsystems.com/zulu/zulu1.8.0_25-8.4.0.1-win64.zip?eclipse" "zulu1.8.0_25-8.4.0.1-win64.zip"
	if not exist "zulu1.8.0_25-8.4.0.1-win64.zip" exit 0
	cmd /c util\wash.cmd blob upload "zulu1.8.0_25-8.4.0.1-win64.zip" "zulu1.8.0_25-8.4.0.1-win64.zip" eclipsedeploy cloudshotdeploy "Q8Ib6UhG2aBT/Kmjl2/qd8GZCG/8fdJ34lNRkCqZUW623zet5/bnKx4l21+ROlW0kdGdmGOcu7jfpKGvt/tZag==" "http://core.windows.net"
) else (
	echo
)
if not exist "zulu1.8.0_25-8.4.0.1-win64.zip" exit 0
cscript /NoLogo util\unzip.vbs "zulu1.8.0_25-8.4.0.1-win64.zip" "%DEPLOYROOT%"
del /Q /F "zulu1.8.0_25-8.4.0.1-win64.zip"
cmd /c util\wash.cmd blob download "glassfish4.zip" "glassfish4.zip" eclipsedeploy cloudshotdeploy "Q8Ib6UhG2aBT/Kmjl2/qd8GZCG/8fdJ34lNRkCqZUW623zet5/bnKx4l21+ROlW0kdGdmGOcu7jfpKGvt/tZag==" "http://core.windows.net"
if not exist "glassfish4.zip" exit 0
cscript /NoLogo util\unzip.vbs "glassfish4.zip" "%DEPLOYROOT%"
del /Q /F "glassfish4.zip"
cmd /c util\wash.cmd blob download "CloudShotRS.war" "CloudShotRS.war" eclipsedeploy cloudshotdeploy "Q8Ib6UhG2aBT/Kmjl2/qd8GZCG/8fdJ34lNRkCqZUW623zet5/bnKx4l21+ROlW0kdGdmGOcu7jfpKGvt/tZag==" "http://core.windows.net"
if not exist "CloudShotRS.war" exit 0
if not "%SERVER_APPS_LOCATION%" == "\%ROLENAME%" if exist "CloudShotRS.war"\* (echo d | xcopy /y /e /q "CloudShotRS.war" "%SERVER_APPS_LOCATION%\CloudShotRS.war" 1>nul) else (echo f | xcopy /y /q "CloudShotRS.war" "%SERVER_APPS_LOCATION%\CloudShotRS.war" 1>nul)
start "Azure" /D"%GLASSFISH_HOME%\bin" asadmin.bat start-domain


:: *** This script will run whenever Azure starts this role instance.
:: *** This is where you can describe the deployment logic of your server, JRE and applications 
:: *** or specify advanced custom deployment steps
::     (Note though, that if you're using this in Eclipse, you may find it easier to configure the JDK,
::     the server and the server and the applications using the New Azure Deployment Project wizard 
::     or the Server Configuration property page for a selected role.)

echo Hello World!


@ECHO OFF
set ERRLEV=%ERRORLEVEL%
if %ERRLEV%==0 (set _MSG="Startup completed successfully.") else (set _MSG="*** Azure startup failed [%ERRLEV%]- exiting...")
choice /d y /t 5 /c Y /N /M %_MSG%
exit %ERRLEV%