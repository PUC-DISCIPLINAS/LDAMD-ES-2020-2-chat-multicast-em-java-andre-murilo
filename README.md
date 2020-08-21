# Chat Simples em Java

## - Introdução
O próposito da aplicação é permitir vários usuários entrarem em uma sala de bate papo e conversarem entre si.

A aplicação foi construida com a metódologia Cliente-Servidor, podendo coexistir dezenas de clientes conectados em um único servidor. Foi utilizado o protocolo TCP para estabelecer e aceitar as conexões de forma sincrona. Para cada cliente conectado é criado uma thread única.

## - Recursos:
* Gerenciar multiplos clientes conectados.
* Protocolo personalizado com o uso de serialização de objetos.
* Trigger de eventos através de Callbacks.
* Agilidade na criação de novos pacotes sem a necessidade de alterar a camada de Networking.

## - Requisitos da Aplicação:
* O servidor deve gerenciar apenas uma sala de bate papo.
* O cliente deve ser capaz de solicitar acesso à sala de bate papo.
* O servidor deve manter uma lista dos membros da sala.
* O cliente deve ser capaz de enviar mensagens para a sala.
* O cliente deve ser capaz de sair da sala de bate papo.

## - Servidor

Exemplo de uma aplicação servidor.

```java
public static void main(String[] args)
{
    // Objeto de configuracao do servidor.
    ServerConfig config = new ServerConfig("127.0.0.1", 43002);
    config.setMaxConnections(100);

    // Define os callcabks.
    config.setCallbacks(
        ChatServer::OnClientConnected,      
        ChatServer::OnClientDisconnected, 
        ChatServer::OnClientSendPacket);

    // Instancia o TcpServer com as configurações definidas.
    TcpServer server = new new TcpServer(config);
    server.runServer();

    // Espere ate a aplicacao ser encerrada.
    synchronized (server) {
        server.wait();
    }
}

// Quando um novo cliente estabelece uma conexão.
public static Boolean OnClientConnected(Session client)
{
    return true;
}

// Quando um cliente é desconectado.
public static Boolean OnClientDisconnected(Session client)
{
    return true;
}

// Quando um cliente envia um pacote.
public static Boolean OnClientSendPacket(ReceivePacketEvent event)
{
    // Objeto serializado.
    Object obj = event.getObj();

    // Sessao do cliente que enviou o pacote.
    Session session = event.getSession();

    // Identifica o tipo do pacote.
    if(obj instanceof CP_LoginRequest)
    {
        // ... processa a requisição de login.
    }

    return true;
}


```
## - Client
Exemplo de uma aplicação Client.

```java
public static void main(String[] args)
{
    // Configuracao do client.
    ClientConfig config = new ClientConfig(ip, port);

    // Define os callbacks
    config.setCallbacks(ChatClient::OnDisconnected, ChatClient::OnReceivePacket);

    // Instancia o TcpCliente e tenta fazer a conexão com o EndPoint remoto.
    TcpClient tcp_client = new TcpClient(config);
    if(!tcp_client.connect())
    {
        // falha ao se conectar.
        return;
    }

    // pega a sessão criada pelo TcpClient.
    Session session = tcp_cliente.getSession();

    // envia a solicitacao de login para o servidor.
    CP_LoginRequest request = new CP_LoginRequest();
    request.username = "admin";
    request.password = "1234";

    // envia o pacote.
    session.sendPacket(request);

    // Espere ate a aplicacao ser encerrada.
    synchronized (tcp_client) {
        tcp_client.wait();
    }
}

// Quando o servidor / cliente força a desconexão ou se houve algum problema na rede.
private static Boolean OnDisconnected(Session session)
{
    return true;
}

// Quando o servidor envia um pacote.
private static Boolean OnReceivePacket(ReceivePacketEvent event)
{
    // Sessao atual do client.
    Session session = event.getSession();

    // Objeto serializado enviado pelo servidor.
    Object packet_obj = event.getObj(); 

    // Identifica o tipo do pacote.
    if(packet_obj instanceof SP_LoginResponse)
    {
        // ... processa a resposta de login.
    }

    return true;
}
```

## - Protocolo
Como mencionado anteriormente, uma das funcionalidades é o facil envio e recebimento dos pacotes entre o servidor e o cliente.

* CP_Base: Representa os pacotes do lado do cliente, ou seja, mensagens que o cliente envia para o servidor. As mensagens client-side devem extender esse objeto. 

* SP_Base: Representa os pacotes do lado do servidor, ou seja, mensagens que o servidor envia para o cliente. As mensagens server-side devem extender esse objeto.

Um pacote possui a seguite estrutura:

```
1. (Int32)  Tamanho do pacote.           ; Tamanho total do pacote.
2. (PacketBase) Estrutura do pacote.      
    1. (Int32)  packet_id;               ; Cada pacote tem uma identificação.
    2. (Boolean) is_encrypted;           ; NÃO IMPLEMENTADO.
    3. (Boolean) is_compressed;          ; NÃO IMPLEMENTADO.
    4. (Object) Object.                  ; Objeto serializado.
```

Exemplo de uma nova mensagem client-side.

```java
public class CP_LoginRequest extends CP_Base
{
    // atributos da mensagem.
    public String username;
    public String password;
    // ... mais atributos se necessarios.
    
    // No construtor é definido a ID desse pacote. Cada ID deve ser único.
    public CP_LoginRequest()
    {
        super.packet_id = 1;
    }
}
```

# Informações adicionais:

* Objeto Session: Permite abstrair melhor as funcionalidades de baixo nível do objeto Socket do java. O objeto é presente no contexto do cliente e do servidor.

* Para adicionar Objetos nas mensagens, elas precisam implementar a interface Serializable.

* As funcionalidades de criptografia e compressão não foram implementadas, a ideia é mostrar que é possível personalizar tipos de mensagens que podem ter ou não criptografia ou compressão de dados.


[![Work in Repl.it](https://classroom.github.com/assets/work-in-replit-14baed9a392b3a25080506f3b7b6d57f295ec2978f6f33ec97e36a161684cbe9.svg)](https://classroom.github.com/online_ide?assignment_repo_id=2973274&assignment_repo_type=AssignmentRepo)