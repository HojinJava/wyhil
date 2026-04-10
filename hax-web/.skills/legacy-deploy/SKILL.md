# Skill: HAX-WEB Legacy WAR Deployment (SSH/SCP)

이 문서는 Docker 도입 이전의 기존 배포 방식(WAR 파일 직접 전송 및 서비스 재시작)으로 원복할 때 필요한 핵심 설정을 기록합니다.

## 1. 프로젝트 환경 변수 (CI/CD Variables)
원복 시 다음 변수들이 필요합니다:
- `SERVER_IP`: 대상 서버 IP
- `SERVER_PW`: 대상 서버 root 패스워드 (Masked 옵션 권장)
- `SERVER_PORT`: SSH 포트 (기본: 22)
- `DEPLOY_ROOT`: WAR 파일 배포 경로

## 2. `.gitlab-ci.yml` 레거시 설정 (배포 단계)

필요할 경우 아래의 `deploy-job` 설정을 `.gitlab-ci.yml`에 다시 복사하여 사용할 수 있습니다.

```yaml
deploy-job:
  stage: deploy
  image: ubuntu:latest
  tags:
    - ci-docker  # (선택 사항) 해당 환경의 러너 태그
  before_script:
    - apt-get update -y && apt-get install -y openssh-client sshpass
  script:
    # 1. 서버로 WAR 파일 전송 (기존 DEPLOY_ROOT 경로)
    - sshpass -p "$SERVER_PW" scp -P "$SERVER_PORT" -o StrictHostKeyChecking=no target/hax-web.war root@"$SERVER_IP":"$DEPLOY_ROOT"
    # 2. 서버의 Tomcat 서비스 재시작
    - sshpass -p "$SERVER_PW" ssh -p "$SERVER_PORT" -o StrictHostKeyChecking=no root@"$SERVER_IP" "systemctl restart tomcat"
  only:
    - main
```

## 3. 원복 절차
1. 프로젝트 루트의 `Dockerfile` 삭제 (선택 사항).
2. `.gitlab-ci.yml`의 `build` 단계를 Maven 빌드 결과물(artifacts)만 남기는 방식으로 수정.
3. 위의 `deploy-job` 로직으로 교체.



