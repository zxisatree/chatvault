;(function () {
    let stompClient = null;

    let connectButton = document.querySelector("#connect");
    let disconnectButton = document.querySelector("#disconnect");

    function setConnected(connected) {
        if (connected) {
            connectButton.setAttribute("disabled", "true");
            disconnectButton.removeAttribute("disabled");
        } else {
            connectButton.removeAttribute("disabled");
            disconnectButton.setAttribute("disabled", "true");
        }
        document.querySelector("#conversation").style.display = connected
            ? "table"
            : "none";
        document.querySelector("#messages").innerHTML = "";
    }

    function connect() {
        stompClient = Stomp.client("ws://localhost:8080/chat");
        stompClient.connect({}, function (frame) {
            setConnected(true);
            console.log("Connected: " + frame);
            stompClient.subscribe("/topic/chatroom", function (greeting) {
                console.log(`received ${greeting}`);
                showMessage(JSON.parse(greeting.body).content);
            });
        });
    }

    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
    }

    function sendName() {
        stompClient.send(
            "/app/sendmessage",
            {},
            JSON.stringify({
                content: document.querySelector("#content").value,
            })
        );
    }

    function showMessage(message) {
        const message_cell = (document.createElement("td").innerHTML = message);
        const new_row = document.createElement("tr");
        new_row.append(message_cell);
        document.querySelector("#messages").append(new_row);
    }

    connectButton.addEventListener("click", connect);
    disconnectButton.addEventListener("click", disconnect);
    document.querySelector("#send").addEventListener("click", sendName);
})();
