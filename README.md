# DungeonFinder Backend

API REST em Spring Boot para encontrar grupos de jogo: usuários, jogos, raids, eventos, guildas (com membros e chat), amizades e notificações.

**Stack:** Java 17 · Spring Boot 3.5 · Spring Security + JWT · JPA/Hibernate · banco H2 em arquivo · Maven.

## Pré-requisitos

- JDK 17+ (`java -version`)
- Nada mais: o Maven Wrapper (`mvnw`) já vem no projeto e o banco H2 é embutido.

## Rodando o projeto

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

A API sobe em **http://localhost:8080**.

Na primeira execução (e sempre que ainda não existir) é criado automaticamente o usuário administrador:

| Email            | Senha    |
|------------------|----------|
| admin@admin.com  | 123456   |

Jogos iniciais são carregados por `GameInitializer`. O banco fica em `./data/dungeonFinderDB.mv.db` (apague a pasta `data/` para começar do zero).

## Console do banco (H2)

Acesse http://localhost:8080/console e use:

- **JDBC URL:** `jdbc:h2:file:./data/dungeonFinderDB`
- **User:** `sa`
- **Password:** `coxinha123`

> Pare a aplicação antes de abrir o mesmo arquivo por outra ferramenta, ou use o console pelo navegador com a app rodando.

## Testando a API (passo a passo)

Os exemplos usam `curl` (no PowerShell do Windows use `curl.exe`). Pode usar Postman/Insomnia igualmente.

### 1. Registrar um usuário
O retorno é o **token JWT** (texto puro).

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@teste.com","nick":"teste","senha":"123456"}'
```

### 2. Fazer login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@admin.com","senha":"123456"}'
```

Guarde o token retornado:

```bash
TOKEN=<cole_o_token_aqui>
```

### 3. Chamar um endpoint protegido
Envie o token no header `Authorization`:

```bash
curl http://localhost:8080/usuarios -H "Authorization: Bearer $TOKEN"
curl http://localhost:8080/notificacoes -H "Authorization: Bearer $TOKEN"
```

Sem o token, endpoints protegidos retornam `401/403`.

### 4. Explorar os endpoints públicos

```bash
curl http://localhost:8080/jogos
curl http://localhost:8080/raids
curl http://localhost:8080/eventos
curl http://localhost:8080/guildas
```

> Atenção: no `SecurityConfig` os caminhos `/auth`, `/console`, `/jogos`, `/eventos`, `/raids`, `/guildas`, `/participantes` e `/jogos_favoritos` estão liberados (`permitAll`). Os demais exigem JWT.

## Endpoints

| Recurso | Base | Observação |
|---|---|---|
| Autenticação | `/auth` | `POST /login`, `POST /register` |
| Usuários | `/usuarios` | CRUD, `PUT /{id}/admin` |
| Jogos | `/jogos` | CRUD |
| Jogos favoritos | `/jogos_favoritos` | `GET /usuario/{id}`, `POST`, `DELETE` |
| Raids | `/raids` | CRUD, `GET /jogo/{jogoId}` |
| Participantes de raid | `/participantes` | `GET /raid/{id}`, `POST`, `DELETE` |
| Eventos | `/eventos` | CRUD, `GET /jogo/{jogoId}` |
| Guildas | `/guildas` | CRUD, `GET /jogo/{id}`, `GET /usuario/{id}` |
| Membros da guilda | `/guildas/{guildaId}/membros` | entrar, convidar, solicitar-entrada, aprovar, papel, sair, expulsar |
| Mensagens da guilda | `/guildas/{guildaId}/mensagens` | `GET`, `POST`, `DELETE /{id}` |
| Amizades | `/amizades` | solicitar, responder, listar, remover |
| Notificações | `/notificacoes` | `GET`, `GET /nao-lidas`, `PUT /{id}/lida`, `DELETE /{id}` |

O diagrama de classes está em [DiagramaUML.pdf](DiagramaUML.pdf).

## Testes automatizados

Os testes JUnit ficam em `src/main/test/java/...` (fora do diretório padrão `src/test/java` do Maven), então `mvnw test` **não os encontra** sozinho. Para rodá-los, execute pela sua IDE (IntelliJ/VS Code: botão direito na pasta de testes → *Run*) ou mova/aponte a pasta para `src/test/java`.

## Problemas comuns

- **Porta 8080 ocupada:** altere `server.port` em `src/main/resources/application.yml`.
- **`Database may be already in use`:** há outra instância da app rodando com o mesmo arquivo em `data/`. Encerre-a.
- **Erro de versão do Java:** confirme que `java -version` mostra 17 ou superior.
- **Resetar dados:** pare a app e apague a pasta `data/`.
