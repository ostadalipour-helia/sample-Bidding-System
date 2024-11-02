<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Tomcat WebSocket</title>
</head>
<body>
ITEM : ${fchecked}
FIRST TIME : ${firstTime}
<form>
<input id="message" type="text">
<input type="hidden" name="proceed" value="True" />
<input onclick="wsSendMessage();" value="Echo" type="button">
<input onclick="wsCloseConnection();" value="Disconnect" type="button">
</form>
<br>
<textarea id="echoText" rows="5" cols="30"></textarea>
<script type="text/javascript">
var added = '<%= session.getAttribute("fchecked") %>';
var ftime = '<%= session.getAttribute("firstTime") %>';
var proceed = '<%= session.getAttribute("proceed") %>';
var webSocket = new WebSocket("ws://localhost:8080/PracticeJsonWebSocketServer2/websocketendpoint");
var echoText = document.getElementById("echoText");
echoText.value = "";
var message = document.getElementById("message");

webSocket.onopen = function(message){ wsOpen(message); };
webSocket.onmessage = function(message){ wsGetMessage(message); };
webSocket.onclose = function(message){ wsClose(message); };
webSocket.onerror = function(message){ wsError(message); };

function wsOpen(message){
    const initMessage = {
        type: 'INIT',
        service: added,
        firstTime: ftime,
        message: 'BID INITIALIZED'
    };
    webSocket.send(JSON.stringify(initMessage));
    echoText.value += "Connected ... \n";
}

function wsSendMessage(){
    const messageObj = {
        type: 'MESSAGE',
        service: added,
        content: message.value
    };
    webSocket.send(JSON.stringify(messageObj));
    echoText.value += "Message sent to the server: " + message.value + "\n";
    message.value = "";
}

function wsCloseConnection(){
    const closeMessage = {
        type: 'CLOSE',
        service: added
    };
    webSocket.send(JSON.stringify(closeMessage));
    webSocket.close();
}

function wsGetMessage(message){
    try {
        const data = JSON.parse(message.data);
        echoText.value += "Message received from server: " + 
            (data.content || data.message || JSON.stringify(data)) + "\n";
    } catch (e) {
        echoText.value += "Message received from server: " + message.data + "\n";
    }
}

function wsClose(message){
    echoText.value += "Disconnect ... \n";
}

function wsError(message){
    echoText.value += "Error ... \n";
}
</script>
</body>
</html>