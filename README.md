# Finanças


## Descrição
 é um projeto backend modular e escalável desenvolvido em Java Spring Boot, voltado para startups. 
Ele integra módulos de Finance (transações), Task (tarefas), Analytics (dashboard) e Auth (autenticação JWT), 
com arquitetura de eventos internos, persistência com JPA e Redis, e deploy pronto via Docker. 
O projeto segue boas práticas de desenvolvimento sênior, é modular, seguro e pronto para integração com frontend ou APIs externas.

## Estrutura do Projeto

- `finance/` → Módulo de transações financeiras
- `task/` → Módulo de tarefas
- `analytics/` → Módulo de métricas e dashboard
- `auth/` → Autenticação e autorização JWT
- `shared/` → Classes utilitárias e base
- `Dockerfile` e `docker-compose.yml` → Configuração para containerização
- `build_nexusone.sh` → Script para gerar toda a estrutura do projeto

## Como rodar

1. Gerar projeto:

```bash
./build_nexusone.sh
