##Communication protocol
<p style="text-align:justify">
    When the connection is initialized the server sends a "ping\n" message (plain text)
    and the client replies with a "pong\n" message (plain text).<br>
    This procedure is performed every time a new client connects to the server. Also, when a new client connects,
    the server verifies that the others are still connected, sending them a "ping\n" message and waiting for a "pong\n" reply.<br>
    <br>
    All the message exchanged between client and server are plain text (always), and everyone of them ends with a new line character "\n".<br>
    <br>
    The whole communication can be divided into five phases (described below), each one beginning with a ping-pong message exchange.<br>
</p>
<br>

###Login phase 
<pre>
+---------+              +---------+  
| Client  |              | Server  |  
+---------+              +---------+  
     |                        |  
     |                   ping |  
     |<-----------------------|  
     |                        |  
     | pong                   |  
     |----------------------->|  
     |                        |  
     |                  login |  
     |<-----------------------|  
     |                        |  
     |      Inserire username |  
     |<-----------------------|  
     |                        |  
     | username               |  
     |----------------------->|  
     |                        |  
</pre>

#####login request (server)  
<p style="text-align:left">
    login\n<br>
    Inserire username\n
</p>
<br>

#####login reply (client)  
<p style:"text-align:left">
    username\n
</p>
<br>

<!--- #####login aknowledge  
<p></p>
<br> --->


###Window choice phase 
<pre>
+---------+                     +---------+
| Client  |                     | Server  |
+---------+                     +---------+
     |                               |
     |                          ping |
     |<------------------------------|
     |                               |
     | pong                          |
     |------------------------------>|
     |                               |
     |                    windowinit |
     |<------------------------------|
     |                               |
     |             Scegli la vetrata |
     |<------------------------------|
     |                               |
     |      list of possible windows |
     |<------------------------------|
     |                               |
     | vetrata                       |
     |------------------------------>|
     |                               |
     |                            ok |
     |<------------------------------|
     |                               |
</pre>

#####window request (server)  
<p style:"text-align:left">
    windowinit\n<br>
    Scegli la vetrata\n<br>
    [[path_window1a, path_window2a], [path_window1b, path_window2b]]\n
</p>
<br>

#####window reply (client)  
<p>
    path_window\n
</p>
<br>

#####window ack (server)
<p style:"text-align:left">
    ok\n
</p>