;(function () {
	let stompClient = null;
	let chatroom = "";
	let connected = false;
	let connectButton = document.querySelector("#connect");

	async function connect(event) {
		if (!connected) {
			connected = true;
			chatroom = document.querySelector("#room").value;
			if (chatroom === "") {
				console.log("Empty chatroom name not allowed");
				return;
			}
			const headers = {"X-CSRF-TOKEN": document.querySelector("#csrf").value}
			stompClient = Stomp.client(`ws://${location.host}/chat`);
			stompClient.connect(headers, function (frame) {
				document.querySelector("#connectErrorMessage").innerHTML = ""
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
			}, function (errorObject) {
				if (errorObject["command"] === "ERROR") {
					document.querySelector("#connectErrorMessage").innerHTML = "You have been disconnected. Please try again."
					connected = false;
					if (stompClient !== null) {
						stompClient.disconnect();
					}
					connectButton.innerHTML = "Connect";
				}
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
			if (confirm("Do you really want to save this message?")) {
				const today = new Date()
				fetch(`http://${window.location.host}/api/article/`, {
					method: "POST",
					body: JSON.stringify({
						title: `Chatroom message at ${today.getDay()}/${today.getMonth() + 1}/${today.getFullYear()}`,
						content: content
					}),
					headers: {
						"Content-Type": "application/json"
					}
				}).then(res => console.log(res))
			}
		}
		const messageCell = document.createElement("td");
		messageCell.append(messageContent);
		messageCell.append(saveText);
		messageCell.classList.add("flex", "hover:bg-sky-100")
		const newRow = document.createElement("tr");
		newRow.append(messageCell);
		newRow.classList.add("even:bg-gray-300");
		document.querySelector("#messages").append(newRow);
	}

	connectButton.addEventListener("click", connect);
	document.querySelector("#send").addEventListener("click", sendName);
})();
