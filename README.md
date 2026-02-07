# Micro SaaS de Orçamentos

Sistema SaaS de geração e gestão de orçamentos desenvolvido para permitir que empresas criem, simulem e gerenciem orçamentos de forma estruturada, rápida e escalável.

O sistema foi projetado com arquitetura moderna, separação entre frontend e backend, e preparado para suportar múltiplas empresas (multi-tenant).

---

## Visão geral

O Micro SaaS de Orçamentos permite que empresas:

- Criem orçamentos com múltiplos itens
- Configurem métodos de precificação
- Simulem valores em tempo real
- Apliquem regras e ajustes comerciais
- Gerenciem clientes e histórico de orçamentos

O sistema é ideal para empresas que precisam padronizar e automatizar seu processo de orçamento.

---

## Tecnologias utilizadas

### Frontend

- Angular
- TypeScript
- TailwindCSS
- PrimeNG

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Hibernate

### Banco de dados

- PostgreSQL

### Infraestrutura

- Docker
- Nginx

---

## Funcionalidades implementadas

- Criação de orçamentos
- Adição de múltiplos itens
- Simulação de precificação no frontend
- Cálculo de valores no backend
- Persistência de valores finais
- Estrutura preparada para múltiplas empresas
- API REST estruturada

---

## Funcionalidades em desenvolvimento

- Autenticação de usuários
- Multi-tenant completo
- Controle de permissões
- Histórico e versionamento de orçamentos
- Dashboard com métricas
- Exportação de orçamentos

---

## Arquitetura de precificação

O sistema utiliza uma arquitetura onde:

- Cada item é precificado individualmente
- O backend é responsável pelo cálculo final
- O frontend realiza apenas simulações
- O valor final é persistido no banco

Isso garante consistência e confiabilidade dos dados.

---

##  Status

Em desenvolvimento ativo.

Projeto sendo construído como produto SaaS real.

## Autor

Ramon Barbosa  
Desenvolvedor Full Stack  

GitHub: https://github.com/ramonbarbosdev  
LinkedIn: https://linkedin.com/in/ramon-barbosa-8b9427223  
Frontend: https://github.com/ramonbarbosdev/app-orcafacil
