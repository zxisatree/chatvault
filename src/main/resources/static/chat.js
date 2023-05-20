;(function () {
	let stompClient = null;
	let chatroom = "";
	let connected = false;
	let connectButton = document.querySelector("#connect");

	function connect() {
		if (!connected) {
			connected = true;
			chatroom = document.querySelector("#room").value;
			if (chatroom === "") {
				console.log("Empty chatroom name not allowed");
				return;
			}
			stompClient = Stomp.client(`ws://${location.host}/chat`);
			stompClient.connect({}, function (frame) {
				console.log("Connected: " + frame);
				connectButton.innerHTML = "Disconnect";
				stompClient.subscribe(
					`/info/${chatroom}`,
					(receivedMessage) => {
						const parsedBody = JSON.parse(receivedMessage.body);
						showIMessage(parsedBody);
					}
				);
				// console.log(`Subscribing to /topic/${chatroom}`);
				stompClient.subscribe(`/topic/${chatroom}`, function (receivedMessage) {
					const parsedBody = JSON.parse(receivedMessage.body);
					showIMessage(parsedBody);
				});
			});
		} else {
			connected = false;
			if (stompClient !== null) {
				stompClient.disconnect();
			}
			connectButton.innerHTML = "Connect";
		}
	}

	function sendName() {
		stompClient.send(
			`/send/${chatroom}`,
			{},
			JSON.stringify({
				username: "anonymous",
				content: document.querySelector("#content").value,
			})
		);
	}

	function showIMessage(message) {
		if (message.isServerMessage) {
			showServerMessage(message.content)
		} else {
			showMessage((message.username ? message.username + ": " : "") +
				message.content)
		}
	}

	function showServerMessage(content) {
		const message_cell = document.createElement("td");
		message_cell.innerHTML = content;
		const new_row = document.createElement("tr");
		new_row.append(message_cell);
		new_row.classList.add("text-red-500", "font-bold");
		document.querySelector("#messages").append(new_row);
	}

	function showMessage(content) {
		const message_cell = document.createElement("td");
		message_cell.innerHTML = content;
		const new_row = document.createElement("tr");
		new_row.append(message_cell);
		new_row.classList.add("even:bg-gray-300");
		document.querySelector("#messages").append(new_row);
	}

	connectButton.addEventListener("click", connect);
	document.querySelector("#send").addEventListener("click", sendName);
})();
