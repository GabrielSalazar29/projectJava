# 📅 Agenda de Compromissos

Este é um projeto de aplicação web Full Stack, desenvolvida como uma Agenda de Compromissos que permite aos usuários gerenciar seus eventos, adicionar participantes (amigos) aos compromissos e interagir com uma rede de contatos.

## 🚀 Visão Geral do Projeto

A "Agenda de Compromissos" é uma aplicação robusta e interativa projetada para facilitar a organização pessoal e social. Com um back-end construído em **Spring Boot** e um front-end dinâmico em **Vue.js 3**, a aplicação oferece uma experiência de usuário fluida para agendamento, visualização e gerenciamento de compromissos, bem como funcionalidades de rede social para conexão com amigos.

### Funcionalidades Principais:

* **Autenticação e Autorização:** Registro de novos usuários e login seguro com tokens JWT.
* **Gerenciamento de Compromissos:**
    * Criação, visualização, edição e exclusão de compromissos.
    * Associação de compromissos a um usuário criador.
    * Definição de título, descrição, local e horários de início/fim.
* **Gestão de Amizades:**
    * Envio e recebimento de solicitações de amizade.
    * Aceite e rejeição de solicitações.
    * Visualização da lista de amigos e desfazimento de amizades.
    * Busca de outros usuários pelo nome de usuário.
* **Participantes em Compromissos:** Capacidade de adicionar amigos como participantes em seus compromissos.
* **Visualização Interativa:** Utilização de um calendário (`FullCalendar`) no front-end para uma visualização clara e interativa dos compromissos (dia, semana, mês e lista).
* **Persistência de Dados:** Armazenamento de todas as informações em um banco de dados **PostgreSQL**.

## 💻 Tecnologias Utilizadas

### Back-end (Spring Boot - Java)

* **Linguagem:** Java 24
* **Framework:** Spring Boot 3.x
* **Persistência:** Spring Data JPA, Hibernate
* **Banco de Dados:** PostgreSQL
* **Segurança:** Spring Security, JWT (JSON Web Tokens)
* **Construção:** Maven

### Front-end (Vue.js 3)

* **Framework:** Vue.js 3 (com Composition API e `<script setup>`)
* **Gerenciamento de Estado:** Pinia
* **Roteamento:** Vue Router
* **Requisições HTTP:** Axios
* **Componentes de UI:** `primevue` (ou similar, dependendo da biblioteca de componentes utilizada)
* **Calendário:** `FullCalendar`
* **Empacotador:** Vite

## ⚙️ Como Rodar o Projeto (Localmente)

### Pré-requisitos:

* Java Development Kit (JDK) 17 ou superior
* Maven
* Node.js (LTS recomendado) e npm
* PostgreSQL (com um banco de dados configurado para o projeto)
* Git

### Passos:

1.  **Clone o repositório - Front-End:**
    ```bash
    git clone https://github.com/GabrielSalazar29/frontAgenda.git
    ```

2.  **Clone o repositório - Back-End:**
    ```bash
    git clone https://github.com/GabrielSalazar29/projectJava.git
    ```

3.  **Configurar o Banco de Dados (PostgreSQL):**
    * Crie um banco de dados PostgreSQL para a aplicação (ex: `agenda_db`).
    * Atualize as configurações no arquivo `src/main/resources/application.properties` do back-end com suas credenciais do PostgreSQL:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/agenda_db
        spring.datasource.username=seu_usuario_db
        spring.datasource.password=sua_senha_db
        spring.jpa.hibernate.ddl-auto=update # ou create, se for a primeira vez
        spring.jpa.show-sql=true
        ```

4.  **Rodar o Back-end:**
    * Navegue até a pasta `backend` do projeto:
        ```bash
        cd backend
        ```
    * Construa e execute a aplicação Spring Boot:
        ```bash
        mvn clean install
        mvn spring-boot:run
        ```
    * O back-end estará rodando em `http://localhost:8080`.

5.  **Rodar o Front-end:**
    * Abra um novo terminal e navegue até a pasta `frontend` do projeto:
        ```bash
        cd frontend
        ```
    * Instale as dependências:
        ```bash
        npm install
        ```
    * Inicie o servidor de desenvolvimento:
        ```bash
        npm run dev
        ```
    * O front-end estará acessível em `http://localhost:5173` (ou outra porta indicada pelo Vite).

## 👥 Equipe de Desenvolvimento

Este projeto foi desenvolvido por:

* **[Sinézio da Silva Ramos Junior]** – Matrícula 202302375081 - https://github.com/ZTX7
* **[Paulo Henrique Ribeiro Chaves]** – Matrícula 202303677308 - https://github.com/XDChaves
* **[Bruno Santos Oliveira]** – Matrícula 202302375138 - https://github.com/BrunnoSantos1
* **[Gabriel Salazar Araujo Alcântara]** - Matrícula 202302375022 - https://github.com/GabrielSalazar29



## 📝 Licença

Este projeto está licenciado sob a MIT License. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---