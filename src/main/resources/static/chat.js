;(function () {
	let stompClient = null;
	let chatroom = "";
	let connected = false;
	let connectButton = document.querySelector("#connect");

	async function connect() {
		if (!connected) {
			connected = true;
			chatroom = document.querySelector("#room").value;
			if (chatroom === "") {
				console.log("Empty chatroom name not allowed");
				return;
			}
			const headers = {}
			const csrfResult = await fetch("http://localhost:8080/csrf")
			const csrfToken = await csrfResult.json()
			headers[csrfToken.headerName] = csrfToken.token
			stompClient = Stomp.client(`ws://${location.host}/chat`);
			stompClient.connect(headers, function (frame) {
				console.log("Connected: " + frame);
				connectButton.innerHTML = "Disconnect";
				stompClient.subscribe(
					`/info/${chatroom}`,
					(receivedMessage) => {
						const parsedBody = JSON.parse(receivedMessage.body);
						showIMessage(parsedBody);
					}
				);
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
		const messageCell = document.createElement("td");
		messageCell.innerHTML = content;
		const newRow = document.createElement("tr");
		newRow.append(messageCell);
		newRow.classList.add("text-red-500", "font-bold");
		document.querySelector("#messages").append(newRow);
	}

	function showMessage(content) {
		const messageContent = document.createElement("div");
		messageContent.innerHTML = content;
		const saveText = document.createElement("button");
		saveText.innerHTML = "Save this message";
		saveText.classList.add("ml-auto")
		saveText.onclick = () => {
			null
		}
		const messageCell = document.createElement("td");
		messageCell.append(messageContent);
		messageCell.append(saveText);
		const newRow = document.createElement("tr");
		newRow.append(messageCell);
		newRow.classList.add("even:bg-gray-300");
		document.querySelector("#messages").append(newRow);
	}

	connectButton.addEventListener("click", connect);
	document.querySelector("#send").addEventListener("click", sendName);
})();
