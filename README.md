# Projeto: Sistema de Gestão Projetos e Tarefas

## Descrição
Este projeto tem como objetivo criar um sistema de gestão de projetos, tarefas e utilizadores, com funcionalidades de cadastro, atualização, exclusão e autenticação de usuários, além de um gestor de projetos com Kanban e relatórios de produtividade.

O backend é desenvolvido em **Java** usando **Spring Boot**, com **JUnit 5 + Mockito** para testes e **Spring Security** para autenticação. O banco de dados utilizado é relacional PostgreSQL.

---

## Funcionalidades implementadas

### Usuários
- [x] Cadastro de usuários com nome, email e senha.
- [x] Validação de email duplicado.
- [x] Atualização de dados do usuário (nome e email).
- [x] Atualização de senha.
- [x] Exclusão de usuários.
- [x] Listagem de todos os usuários.
- [x] Testes unitários cobrindo todos os cenários principais do `UserServiceImpl`.

### Autenticação
- [x] Login via email e senha.
- [ ] Login via Google OAuth (planejado).

### Projetos (Gestor de tarefas)
- [ ] Criação de times e boards de tarefas (Kanban).
- [ ] Atribuição de tarefas a usuários.
- [ ] Atualização e conclusão de tarefas.
- [ ] Relatórios automáticos de produtividade (quantidade de tarefas concluídas por usuário e time).

---

## Tecnologias utilizadas
- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL
- JUnit 5 + Mockito
- Flyway

---

## Como rodar o projeto
1. Clone o repositório:
``` bash
  git clone https://github.com/tailansanttos/gestao-de-projetos.git
```
2. Configure o banco de dados no application.properties
3. Rode a aplicacão:
``` bash
  ./mvnw spring-boot:run
```
4. Execute os testes:
``` bash
  ./mvnw test
```

## Funcionalidades a implementar
1. Integração com Google OAuth para login.
2. Implementação completa do gestor de projetos Kanban com boards e tarefas.
3. Geração de relatórios de produtividade.
4. Melhorias na interface (caso seja integrado com frontend).
5. Segurança avançada com JWT e validação de roles (Cliente, Gestor).
6. Dockerização do backend e do banco de dados para facilitar deploy.

## Observações
1. O sistema já possui testes unitários cobrindo a lógica de negócios do gerenciamento de usuários.
2. Em testes de senha, o **PasswordEncoder** está sendo mockado para simular a criptografia.
3. Todo o código segue boas práticas e separação de responsabilidades.

## Contato
E-mail: tailansanttos02@gmail.com

GitHub: https://github.com/tailansanttos