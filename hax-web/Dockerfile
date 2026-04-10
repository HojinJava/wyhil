# HAX-WEB Server Dockerfile
# 베이스 이미지: Tomcat 10.1 (Java 21 버전)
FROM tomcat:10.1-jdk21-temurin

# Maven 빌드 결과물인 WAR 파일을 Tomcat webapps 디렉토리로 복사
# (build-job의 artifacts로 생성된 target/hax-web.war 사용)
COPY target/hax-web.war /usr/local/tomcat/webapps/hax-web.war

# Tomcat 기본 포트 노출
EXPOSE 8080

# Tomcat 서버 실행
CMD ["catalina.sh", "run"]
