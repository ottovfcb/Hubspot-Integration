# HubSpot Integration

Este projeto é uma API REST em Java com Spring Boot desenvolvida para integrar com a API do HubSpot utilizando o fluxo de autenticação OAuth 2.0. A aplicação permite a criação de contatos no CRM da HubSpot e o recebimento de notificações via webhook.

---

## Tecnologias e Dependências

- Java 21
- Spring Boot
- Spring WebFlux
- Resilience4j (Rate Limiting)
- WebClient
- Ngrok (para testes locais com webhook)
- HubSpot CRM API

---

## Funcionalidades

- Geração da URL de autorização OAuth2 (authorization code flow)
- Callback OAuth para troca de código por access token
- Armazenamento simples do token em memória
- Criação de contatos no CRM HubSpot
- Escuta de eventos via Webhook configurado no app da HubSpot
- Rate limiting usando Resilience4j

---

## 1. Configuração

Antes de executar o projeto, o desenvolvedor deve ter os seguintes itens instalados/configurados:
- **Java SDK**:
    - Certifique-se de que a versão mínima do SDK exigida pelo projeto (nesse caso, pelo menos a **versão 21**) está instalada.
    - Configure a variável de ambiente `JAVA_HOME` apontando para o diretório do SDK.
    - Verifique a versão usando o comando:
``` bash
    java -version
```
- **Maven(versão recomendada: 3.9.9)**:
    - Verifique se o Maven está instalado e configurado:
``` bash
    mvn -v
```
- **Git**:
    - Para clonar o repositório da aplicação do controle de versão:
``` bash
    git --version
```
- **Obter o código-fonte do projeto**
  - Use o comando `git clone` com a URL do repositório:
```bash
git clone https://github.com/ottovfcb/Hubspot-Integration.git
cd hubspot-integration
```
- **Verificar dependências do projeto**:
  - Certifique-se de que as dependências do Maven estão atualizadas. Execute:
``` bash
    mvn clean install
```
- Isso irá baixar todas as bibliotecas necessárias e construir o projeto.

---

### 2. Configurar variáveis sensíveis

Antes de executar a aplicação, é necessário configurar suas credenciais do HubSpot no arquivo **application.yml**, localizado em hubspot-integration/src/main/resources/, trocando os valores indicados abaixo pelas credenciais do seu aplicativo no Hubspot

```bash
hubspot:
  client-id: client-id-aqui    #TROCAR ESTE CAMPO
  redirect-uri: http://localhost:8080/oauth/callback
  scopes: crm.objects.contacts.write%20oauth%20crm.objects.contacts.read
  authorization-url: https://app.hubspot.com/oauth/authorize
  client-secret: client-secret-aqui    #TROCAR ESTE CAMPO
  token-url: https://api.hubapi.com/oauth/v1/token
  api-url: https://api.hubapi.com
```

---

### 3. Configuração e funcionamento do Webhook

A aplicação está preparada para receber notificações automáticas do HubSpot sempre que um novo **contato for criado** com sucesso na plataforma. Essa comunicação é feita via **webhook**, que precisa ser devidamente configurado no painel do aplicativo HubSpot.

- Antes de tudo, utilize uma ferramenta como [Ngrok](https://ngrok.com/) para expor sua aplicação local na internet. Isso é necessário para que o HubSpot consiga enviar requisições ao seu endpoint.

Comando:

```bash
ngrok http 8080
```
Após rodar este comando copie a url pública gerada, cujo formato é parecido com este: 
```bash
https://02fa-2804-14c-da86-8b08-7df0.ngrok-free.app
```

- Acesse o [Painel de Desenvolvedor do HubSpot](https://developers.hubspot.com).
- Selecione o aplicativo criado para este desafio.
- No menu lateral, vá até **"Webhooks"** e clique em **"Adicionar Webhook"**.
- Configure da seguinte forma:

   - **URL**: `(URL pública gerada pelo Ngrok)/webhook` (não esquecer o endpoint /webhook)
   - **Criar assinatura**:
     - selecione **Contatos** em *Quais tipos de objeto?*
     - selecione **Criado** em *Monitorar quais eventos?*

- Salve e publique as alterações do app.

- O endpoint responsável por receber os eventos está mapeado em:
```bash
POST /webhook
```

---

### 4. Execução e Como Testar o Fluxo

- No diretório do projeto e execute o comando:
``` bash
  mvn spring-boot:run
```

*Para os passos 1 e 3 recomendo a utilização de um software para teste de APIs como o Postman*

**Passo 1**: Gerar URL de autorização
``` bash
  GET  http://localhost:8080/oauth/authorize-url
```
Você receberá uma URL. Copie-a e cole no navegador e em seguida autorize a aplicação.

**Passo 2**: Callback OAuth
Após autorização, você será redirecionado para :
``` bash
  GET http://localhost:8080/oauth/callback?code=XYZ
```
O código (code) será trocado por um access token, armazenado automaticamente.

**Passo 3**: Criar um contato
Agora você pode criar contatos por essa API, enviando os atributos do contato via body da requisição.
``` bash
POST http://localhost:8080/contacts
Content-Type: application/json

{
  "email": "contato@teste.com",
  "firstName": "Moacir",
  "lastName": "Antonio",
  "phone": "83999999999"
}
```

- Toda vez que um novo contato for criado com sucesso na conta HubSpot vinculada, o HubSpot enviará uma notificação contendo dados resumidos do evento, enviando uma requisição POST para o endpoint do webhook. No ngrok aparecerá algo semelhante a isto:
```bash
00:27:26.936 -12 POST /webhook                  200
```
- A aplicação processa o payload e imprime no console os dados básicos do evento recebido, como `objectId` e `subscriptionType` por exemplo.
 
---

### 5. Notas de implementação
- OAuth 2.0 com WebClient: não foi utilizado *spring-security-oauth2-client* para manter o fluxo controlado e transparente.

- Token em memória: a classe TokenStorage armazena o token apenas durante a execução. Em ambientes reais, recomenda-se persistência segura e gerenciamento de expiração.

- Resilience4j Rate Limiter: previne chamadas excessivas ao endpoint da HubSpot.

- Webhook sem autenticação: para simplificação, nenhum tipo de validação foi implementada para o webhook. Idealmente, isso deve ser incluído.

### 6. Melhorias Futuras
- Persistência segura de tokens com refresh automático

- Validação HMAC de webhooks

- Testes de integração e maior cobertura de testes unitários

- Interface web para autenticação e gerenciamento de contatos

- Logs estruturados com correlação de requisições
