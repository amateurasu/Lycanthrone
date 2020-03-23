var ws;

function connect() {
    var username = document.getElementById("username").value;
    const {host, pathname} = document.location;

    var addr = `ws://${host}/Fundamental/chat/${username}`;
    console.log(addr);
    ws = new WebSocket(addr);

    ws.onmessage = event => {
        var log = document.getElementById("log");
        console.log(event.data);
        var message = JSON.parse(event.data);
        log.innerHTML += `${message.from} : ${message.content}\n`;
    };
}

function send() {
    var content = document.getElementById("msg").value;
    var json = JSON.stringify({content});

    ws.send(json);
}
