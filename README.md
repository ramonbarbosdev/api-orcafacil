# Sistema de Orçamentos SaaS

Plataforma **SaaS para criação, simulação e gestão de orçamentos**, desenvolvida com foco em **flexibilidade, precificação dinâmica e arquitetura escalável**.

O sistema foi projetado para atender diferentes empresas, permitindo configurar regras de precificação, campos personalizados e simulações em tempo real, mantendo separação clara entre frontend e backend.

---

##  Visão Geral

Este projeto tem como objetivo resolver o problema de **orçamentos rígidos e pouco escaláveis**, oferecendo uma solução moderna, extensível e preparada para múltiplos clientes (**multi-tenant**).

A aplicação segue boas práticas de arquitetura, priorizando:

- escalabilidade
- manutenibilidade
- separação de responsabilidades
- código limpo

---

##  Funcionalidades Principais

-  Criação e gestão de orçamentos
-  Precificação dinâmica baseada em regras
-  Simulação de valores em tempo real
-  Suporte a múltiplas empresas (multi-tenant)
-  Arquitetura modular e extensível
-  APIs REST bem definidas
-  Interface moderna e responsiva

---

##  Arquitetura

O sistema foi dividido em duas camadas principais:

### Backend
- Responsável por regras de negócio e precificação
- Fonte única da verdade para cálculos
- APIs REST desacopladas do frontend

### Frontend
- Interface reativa para criação e simulação de orçamentos
- Estado compartilhado via services
- Componentização e reutilização de código

---

##  Tecnologias Utilizadas

### Backend
- Java
- Spring Boot
- PostgreSQL
- Docker

### Frontend
- Angular
- TypeScript
- Tailwind CSS
- PrimeNG

### Infraestrutura
- Docker
- Nginx
- CI/CD com GitHub Actions

---

## 📐 Conceitos Técnicos Aplicados

- Arquitetura em camadas
- Separação entre valor técnico e valor comercial
- Precificação aplicada exclusivamente no backend
- Frontend utilizado apenas para simulação
- APIs REST stateless
- Componentização e reatividade no Angular

---

##  Status do Projeto

 **Em desenvolvimento (MVP)**

O projeto está em constante evolução, com foco inicial em consolidar a base arquitetural antes de expandir funcionalidades comerciais.

---

## Autor

**Ramon Barbosa**  
Desenvolvedor Full Stack  
Foco em Backend, Arquitetura de Sistemas e APIs escaláveis  

🔗 Portfólio: https://ramoncode.com.br  
🔗 GitHub: https://github.com/ramonbarbosdev  
🔗 LinkedIn: https://linkedin.com/in/ramon-barbosa-8b9427223  
🔗 Frontend: https://github.com/ramonbarbosdev/app-orcafacil
---

##  Licença

Este projeto está sob a licença MIT.  
Sinta-se à vontade para estudar, adaptar e evoluir a solução.
