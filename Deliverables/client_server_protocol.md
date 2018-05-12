#Communication protocol
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

##Initialization

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
    [["path_window1a", "path_window2a"], ["path_window1b", "path_window2b"]]\n
</p>
<br>

#####window reply (client)  
<p style:"text-align:left">
    path_window\n
</p>
<br>

#####window ack (server)
<p style:"text-align:left">
    ok\n
</p>
<br>

###Cards phase (Tools and Public objectives)
<pre>
+---------+       +---------+
| Client  |       | Server  |
+---------+       +---------+
     |                 |
     |            ping |
     |<----------------|
     |                 |
     | pong            |
     |---------------->|
     |                 | -------------------------\
     |                 |-| pick public objectives |
     |                 | |------------------------|
     |                 |
     |      objectives |
     |<----------------|
     |                 |
     | ok              |
     |---------------->|
     |                 | -------------\
     |                 |-| pick tools |
     |                 | |------------|
     |                 |
     |           tools |
     |<----------------|
     |                 |
     | ok              |
     |---------------->|
     |                 |
</pre>

#####objectives (server)  
<p style:"text-align:left">
    ["path_obj1", "path_obj2", "path_obj3"]\n
</p>
<br>

#####tools (server)
<p style:"text-align:left">
    ["path_tool1", "path_tool2", "path_tool3"]\n
</p>
<br>

#####reply (client)  
<p style:"text-align:left">
    ok\n
</p>
<br>

###Private objective phase
<pre>
+---------+      +---------+
| Client  |      | Server  |
+---------+      +---------+
     |                |
     |           ping |
     |<---------------|
     |                |
     | pong           |
     |--------------->|
     |                | -------------------------\
     |                |-| pick private objective |
     |                | |------------------------|
     |                |
     |      objective |
     |<---------------|
     |                |
     | ok             |
     |--------------->|
     |                |
</pre>
#####objective (server)
<p style:"text-align:left">
    "path_obj"\n
</p>
<br>

#####reply (client)  
<p style:"text-align:left">
    ok\n
</p>
<br>

##Game

###Move phase
<pre>
+---------+                +---------+
| Client  |                | Server  |
+---------+                +---------+
     |                          |
     |                     ping |
     |<-------------------------|
     |                          |
     | pong                     |
     |------------------------->|
     |                          |
     |                     wake |
     |<-------------------------|
     |                          |
     | move                     |
     |------------------------->|
     |                          |
     |                 response |
     |<-------------------------|
     |                          |
     |      gui_changed_element |
     |<-------------------------|
     |                          |
</pre>

#####wake (client)  
<p style:"text-align:left">
    "your turn"\n
</p>
<br>

#####move (server)
<p style:"text-align:left">
        ["color", "value"]]\n<br>
        ["x", "y"]\n
    </p>
    <br>

#####response
<p style:"text-align:left">
    response\n<br>
    <i>response: y, n</i>
</p>
<br>
    
#####changed_gui_element
<p style:"text-align:left">
   [["color1", "value1"], ..., ["color20", "value20"]]\n
</p>
<br>


###Tool phase
<p style="text-align:justify">
    To use any of the tools the following set up messages are exchanged.<br>
</p>
<pre>
+---------+          +---------+
| Client  |          | Server  |
+---------+          +---------+
     |                    |
     |               ping |
     |<-------------------|
     |                    |
     | pong               |
     |------------------->|
     |                    |
     | tool request       |
     |------------------->|
     |                    |
     |      response[y/n] |
     |<-------------------|
     |                    |
</pre>

#####tool request (client)
<p style:"text-align:left">
    toolID<TAB>numToken\n
</p>
<br>
    
#####response (server)
<p style:"text-align:left">
    response\n<br>
    <i>response: y, n</i>
</p>
<br>

<br>
<p style="text-align:justify">
    The next part of the conversation depends on the chosen tool.<br>
    <br>
    <br>
    Tools (1), (2), (3), (9), (10) follow the diagram below:
</p>

<pre>
+---------+                +---------+
| Client  |                | Server  |
+---------+                +---------+
     |                          |
     | action                   |
     |------------------------->|
     |                          |
     |            reply[y/n]    |
     |<-------------------------|
     |                          |
     |      changed_gui_element |
     |<-------------------------|
     |                          |
</pre>
- #####Pinza Sgrossatrice (1)
     #####action
    <p style:"text-align:left">
        ["color","value"]\n<br>
        command\n<br>
        <i>commands: inc, dec</i>
    </p>
    <br>
    
    #####reply
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["colorn", "valuen"]]\n<br>
        <i>where n is the number of dice on the table</i>
    </p>
    <br>
    
- #####Pennello per Eglomise (2)
    #####action
    <p style:"text-align:left">
        [color, ["x<sub>i</sub>", "y<sub>i</sub>"], ["x<sub>f</sub>", "y<sub>f</sub>"]]\n<br>
    </p>
    <br>
    
    #####reply
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["color20", "value20"]]\n
    </p>
    <br>

- #####Alesatore per Lamine di rame (3)
    #####action
    <p style:"text-align:left">
        ["value", ["x<sub>i</sub>", "y<sub>i</sub>"], ["x<sub>f</sub>", "y<sub>f</sub>"]]\n<br>
    </p>
    <br>
    
    #####reply
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["color20", "value20"]]\n
    </p>
    <br>
    
- #####Riga in sughero (9)
    #####action
    <p style:"text-align:left">
        ["color", value"]\n<br>
        ["x", "y"]\n<br>
    </p>
    <br>
    
    #####reply
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["colorn", "valuen"]]\n<br>
        [["color1", "value1"], ..., ["color20", "value20"]]\n<br>
        <i>where n is the number of dice on the table</i>
    </p>
    <br>
    
- #####Tampone diamantato (10)
    #####action
    <p style:"text-align:left">
        ["color", value"]\n
    </p>
    <br>
    
    #####reply
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["color20", "value20"]]\n<br>
        <i>where n is the number of dice on the table</i>
    </p>
    <br>

<br>
<p style="text-align:justify">
    Tool (4) follows the diagram below:
</p>  
<pre>
                               +---------+                +---------+
                               | Client  |                | Server  |
                               +---------+                +---------+
  --------------------------------\ |                          |
  | the clients sends coordinates |-|                          |
  |-------------------------------| |                          |
----------------------------------\ |                          |
| until the server replies with y |-|                          |
|---------------------------------| |                          |
                                    |                          |
                                    | dice_coordinates         |
                                    |------------------------->|
                                    |                          | --------\
                                    |                          |-| check |
                                    |                          | |-------|
                                    |                          |
                                    |                 response |
                                    |<-------------------------|
                                    |                          |
                                    | dice_coordinates         |
                                    |------------------------->|
                                    |                          | --------\
                                    |                          |-| check |
                                    |                          | |-------|
                                    |                          |
                                    |                 response |
                                    |<-------------------------|
                                    |                          | --------------------------------------\
                                    |                          |-| if all the coordinates are accetted |
                                    |                          | |-------------------------------------|
                                    |                          |
                                    |      changed_gui_element |
                                    |<-------------------------|
                                    |                          |
</pre>

- #####Lathekin (4)
    #####dice_coordinates
    <p style:"text-align:left">
        ["value", ["x<sub>i</sub>", "y<sub>i</sub>"], ["x<sub>f</sub>", "y<sub>f</sub>"]]\n<br>
    </p>
    <br>
    
    #####response
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["color20", "value20"]]\n
    </p>
    <br>  
    
<br>
<p style="text-align:justify">
    Tool (5) follows the diagram below:
</p>  
<pre>
+---------+                +---------+
| Client  |                | Server  |
+---------+                +---------+
     |                          |
     | selected_dice            |
     |------------------------->|
     |                          |
     | turn, round_dice         |
     |------------------------->|
     |                          | --------\
     |                          |-| check |
     |                          | |-------|
     |                          |
     |                 response |
     |<-------------------------|
     |                          |
     |      changed_gui_element |
     |<-------------------------|
     |                          |
</pre>

- #####Taglierina circolare (5)
    #####selected_dice
    <p style:"text-align:left">
        ["color", "value"]\n
    </p>
    <br>
    
    #####selected_dice
    <p style:"text-align:left">
        ["turn", ["color", "value"]]\n
    </p>
    <br>
    
    #####response
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["colorn", "valuen"]]\n<br>
        [["color1", "value1"], ..., ["colorm", "valuem"]]\n<br>
        <i>where n is the number of dice on the table, and m the number of dice on the trace round</i>
    </p>
    <br>
    
    
<br>
<p style="text-align:justify">
    Tool (6) follows the diagram below:
</p>  
<pre>
+---------+                +---------+
| Client  |                | Server  |
+---------+                +---------+
     |                          |
     | selected_dice            |
     |------------------------->|
     |                          | -------\
     |                          |-| roll |
     |                          | |------|
     |                          |
     |              rolled_dice |
     |<-------------------------|
     |                          |
     | dice_coordinates         |
     |------------------------->|
     |                          | --------\
     |                          |-| check |
     |                          | |-------|
     |                          |
     |                 response |
     |<-------------------------|
     |                          |
     |      changed_gui_element |
     |<-------------------------|
     |                          |  
</pre>

- #####Pennello per pasta salda (6)
    #####selected_dice
    <p style:"text-align:left">
        ["color", "value"]\n
    </p>
    <br>
    
    #####rolled_dice
    <p style:"text-align:left">
        "value"\n
    </p>
    <br>

    #####dice_coordinates
    <p style:"text-align:left">
        ["x", "y"]\n
    </p>
    <br>

    #####response
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["colorn", "valuen"]]\n<br>
        [["color1", "value1"], ..., ["color20", "value20"]]\n<br>
        <i>where n is the number of dice on the table</i>
    </p>
    <br>


<br>
<p style="text-align:justify">
    Tools (7), (8) don't need additional information to be executed. Once the server has processed 
    the information it just sends a reply and a graphic update:
</p>

- #####Martelletto (7) and Tenaglia a rotelle (8)
    #####response
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["colorn", "valuen"]]\n<br>
        [["color1", "value1"], ..., ["color20", "value20"]]\n<br>
        <i>where n is the number of dice on the table. <br>(7) sends just the first one</i>
    </p>
    <br>


<br>
<p style="text-align:justify">
    Tool (11) follows the diagram below:
</p> 
<pre>
+---------+                +---------+
| Client  |                | Server  |
+---------+                +---------+
     |                          |
     | selected_dice            |
     |------------------------->|
     |                          | ----------------\
     |                          |-| pick new dice |
     |                          | |---------------|
     |                          |
     |              picked_dice |
     |<-------------------------|
     |                          |
     | value                    |
     |------------------------->|
     |                          |
     | dice_coordinate          |
     |------------------------->|
     |                          | --------\
     |                          |-| check |
     |                          | |-------|
     |                          |
     |                 response |
     |<-------------------------|
     |                          |
     |      changed_gui_element |
     |<-------------------------|
     |                          |
</pre>

- #####Diluente per pasta salda (11)
    #####selected_dice
    <p style:"text-align:left">
        ["color", "value"]\n
    </p>
    <br>
    
    #####picked_dice
    <p style:"text-align:left">
        ["color", "value"]\n
    </p>
    <br>
    
    #####value
    <p style:"text-align:left">
        value\n
    </p>
    <br>
    
    #####dice_coordinates
    <p style:"text-align:left">
        ["x", "y"]\n
    </p>
    <br>
    
    #####response
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["colorn", "valuen"]]\n<br>
        [["color1", "value1"], ..., ["color20", "value20"]]\n<br>
        <i>where n is the number of dice on the table</i>
    </p>
    <br>
    

<br>
<p style="text-align:justify">
    Tool (12) follows the diagram below:
</p> 
<pre>
                               +---------+                +---------+
                               | Client  |                | Server  |
                               +---------+                +---------+
                                    |                          |
                                    | num_of_dice              |
                                    |------------------------->|
                                    |                          |
                                    | color                    |
                                    |------------------------->|
                                    |                          |
                                    |                 response |
                                    |<-------------------------|
  --------------------------------\ |                          |
  | the clients sends coordinates |-|                          |
  |-------------------------------| |                          |
----------------------------------\ |                          |
| until the server replies with y |-|                          |
|---------------------------------| |                          |
                                    |                          |
                                    | dice_coordinates         |
                                    |------------------------->|
                                    |                          | --------\
                                    |                          |-| check |
                                    |                          | |-------|
                                    |                          |
                                    |                 response |
                                    |<-------------------------|
                                    |                          |
                                    | dice_coordinates         |
                                    |------------------------->|
                                    |                          | --------\
                                    |                          |-| check |
                                    |                          | |-------|
                                    |                          |
                                    |                 response |
                                    |<-------------------------|
                                    |                          | --------------------------------------\
                                    |                          |-| if all the coordinates are accetted |
                                    |                          | |-------------------------------------|
                                    |                          |
                                    |      changed_gui_element |
                                    |<-------------------------|
                                    |                          |
</pre>

- #####Taglierina manuale (12)
    #####num_of_dice and color
    <p style:"text-align:left">
        "num_of_dice"<TAB>"selected_color"\n
    </p>
    <br>
    
    #####dice_coordinates
    <p style:"text-align:left">
        ["x", "y"]\n
    </p>
    <br>
    
    #####response
    <p style:"text-align:left">
        response\n<br>
        <i>response: y, n</i>
    </p>
    <br>
    
    #####changed_gui_element
    <p style:"text-align:left">
        [["color1", "value1"], ..., ["color20", "value20"]]\n
    </p>
    <br>