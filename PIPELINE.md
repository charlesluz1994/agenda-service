# Pipeline CI/CD — agenda-service (Jenkins + SonarQube)

Este documento descreve **como a pipeline de CI/CD deste projeto funciona**. A definição executável
está no [`Jenkinsfile`](./Jenkinsfile) na raiz do repositório (*pipeline as code*). A **infraestrutura**
(Jenkins + SonarQube em Docker) vive em um repositório separado de plataforma (`CI-CD`).

Fluxo: **GitHub → Jenkins → SonarQube**, com deploy local como fase seguinte (M2).

---

## Visão geral

```mermaid
flowchart LR
    Dev[Push/PR no GitHub] --> J[Jenkins - Docker, rede devops]
    J -->|./mvnw clean verify + Testcontainers via docker.sock| HD[Docker Desktop do host]
    J -->|sonar:sonar em http://sonarqube:9000 - só main| SQ[SonarQube CE :9000]
    SQ -->|webhook http://jenkins:8080/sonarqube-webhook/| J
    J -.M2, só main.->|docker build + compose up| APP[App :8087 + Postgres da app]
```

| Stage | Roda em | O que faz |
|---|---|---|
| **Checkout** | todas as branches | `checkout scm` + `chmod +x mvnw` |
| **Build & Test** | todas as branches | `./mvnw clean verify`: compila, testa (unit + integração + Testcontainers), JaCoCo + gate de 30% |
| **SonarQube Analysis** | só `main` | `./mvnw sonar:sonar` enviando código + cobertura ao SonarQube |
| **Quality Gate** | só `main` | `waitForQualityGate` — aborta o build se o gate reprovar |
| **Package / Deploy / Smoke** (M2) | só `main` | build da imagem, `docker compose up`, health check |

> **Por que Sonar só na `main`?** O SonarQube **Community Edition não faz análise por branch** —
> todas as análises caem no mesmo projeto. Rodar em cada branch sobrescreveria os resultados, então
> a análise fica restrita à `main`. Build & Test continua rodando em todas as branches.

---

## Detalhe dos stages

### Build & Test
```
./mvnw -B -ntp -Dmaven.repo.local=$MAVEN_LOCAL_REPO clean verify
```
- `-B -ntp`: modo batch, sem "transfer progress" (log limpo).
- `-Dmaven.repo.local=/var/jenkins_home/.m2/repository`: como o container do Jenkins roda como
  `root`, o cache padrão (`~/.m2` = `/root/.m2`) ficaria fora do volume. Fixamos o repositório local
  no `JENKINS_HOME` (volume `jenkins_data`) para **não re-baixar dependências a cada build**.
- `verify` dispara a fase completa: testes + **JaCoCo** (relatório em `target/site/jacoco/jacoco.xml`)
  + o **gate de 30%** de cobertura de linha já configurado no `pom.xml`.
- Publica os resultados JUnit (`target/surefire-reports/*.xml`) e arquiva o `target/agenda-*.jar`.

> **Testcontainers exige Docker.** O teste `PostgresqlTest` sobe um `postgres:16-alpine` via
> Testcontainers. O container do Jenkins acessa o Docker do host pelo socket montado
> (`/var/run/docker.sock`), e a variável `TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal`
> permite que o processo de teste alcance a porta publicada pelo container-irmão.

### SonarQube Analysis
```
withSonarQubeEnv('SonarQube') {
  ./mvnw sonar:sonar -Dsonar.projectKey=agenda-service \
                     -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
}
```
- `withSonarQubeEnv('SonarQube')` injeta a URL e o token do servidor configurado no Jenkins.
- A cobertura vem do XML do JaCoCo gerado no stage anterior.

### Quality Gate
```
timeout(5 min) { waitForQualityGate abortPipeline: true }
```
- Bloqueia até o SonarQube devolver o resultado do Quality Gate **via webhook**
  (`http://jenkins:8080/sonarqube-webhook/`). Se reprovar, o build falha.

---

## Como disparar

O Jenkins local não recebe webhook do GitHub (não é acessível pela internet). Use **SCM polling**:
no job, *Poll SCM* com `H/5 * * * *` (verifica o repositório a cada ~5 min). "Build Now" /
"Scan Repository Now" também disparam manualmente.

Setup completo do Jenkins/SonarQube: ver o `README.md` do repositório de infra (`CI-CD`).

---

## Troubleshooting

| Sintoma | Causa provável | Ação |
|---|---|---|
| `PostgresqlTest` falha por não achar Docker / conexão recusada | socket não montado ou `TESTCONTAINERS_HOST_OVERRIDE` ausente | Conferir o serviço `jenkins` no compose (socket + env); em último caso, `TESTCONTAINERS_RYUK_DISABLED=true` |
| Build re-baixa todas as dependências toda vez | cache Maven fora do volume | Garantir `-Dmaven.repo.local=$MAVEN_LOCAL_REPO` (volume `jenkins_data`) |
| `Quality Gate` fica pendurado até o timeout | webhook do SonarQube ausente/errado | Criar webhook para `http://jenkins:8080/sonarqube-webhook/` |
| SonarQube não sobe | memória / `vm.max_map_count` | `docker run --rm --privileged alpine sysctl -w vm.max_map_count=262144`; dar ~4 GB ao Docker |
| Sonar "pula" em feature branches | comportamento esperado (só `main`) | Análise por branch exige Developer Edition (paga) |

---

## M2 — Deploy local (próxima fase)

Acrescenta ao `Jenkinsfile` (todos com `when { branch 'main' }`):

1. **Package Docker Image** — `docker build -t agenda-service:${BUILD_NUMBER} -t agenda-service:latest .`
   (usa o `Dockerfile` da raiz; o jar já está em `target/`).
2. **Deploy (local)** — `docker compose -f docker/docker-compose-cd.yml up -d --force-recreate`
   (Postgres da app + a imagem recém-buildada; só imagem + volumes nomeados, **sem bind de host**).
3. **Smoke Test** — poll em `http://host.docker.internal:8087/actuator/health` até `UP`.

> **Pré-requisito do smoke test:** `/actuator/health` precisa estar **público** (o projeto tem Spring
> Security). Se estiver protegido, o health responde 401/403 e o smoke test falha — liberar o endpoint
> antes de habilitar o stage.

Para o M2, o container do Jenkins ganha o **docker CLI** (via `jenkins/Dockerfile` na infra), pois
`docker build`/`docker compose` precisam do cliente (o M1 usa apenas o socket, que o Testcontainers
acessa pela API).
