window.onload = init;
var socket;// = new WebSocket("ws://localhost:8080/RPS_Game_1.0/game/");
var rock, paper, scissors;

function init() {
    rock = document.getElementById('rock');
    paper = document.getElementById('paper');
    scissors = document.getElementById('scissors');

    var url = "ws://" + location.host + "/RPS_Game_1.0/game/" + location.hash.replace('#', '');
    socket = new WebSocket(url);
    socket.onopen = onOpen;
    socket.onclose = onClose;
    socket.onerror = onError;
    socket.onmessage = onMessage;

    buttonsDisable(true);
}

function onOpen() {
    console.log("Соединение установлено.");
};

function onClose(event) {
    if (event.wasClean) {
        console.log('Соединение закрыто чисто');
    } else {
        console.log('Обрыв + соединения'); // например, "убит" процесс сервера
    }
    console.log('Код: ' + event.code + ' причина: ' + event.reason);
}

//метод который вызывается, когда приходят сообщения
function onMessage(event) {
    var incomingMessage = JSON.parse(event.data);
    switch (incomingMessage.type) {
        case 'MESSAGE':
            showMessage(incomingMessage);
            break;
        case 'RESULT':
            showResult(incomingMessage);
            break;
        case 'CONNECTION':
            showMessage(incomingMessage);
            break;
        case 'ID':
            window.location.hash = incomingMessage.id;
    }
    buttonsDisable(false);
}

function buttonsDisable(disable) {
    rock.disabled = disable;
    paper.disabled = disable;
    scissors.disabled = disable;
}

// показать сообщение в div#subscribe
function showMessage(message) {
    var messageElem = document.createElement('div');
    messageElem.appendChild(document.createTextNode(JSON.stringify(message)));
    document.getElementById('subscribe').appendChild(messageElem);
}

function showResult(message) {
    var messageElem = document.createElement('div');
    messageElem.appendChild(document.createTextNode(JSON.stringify(message)));
    document.getElementById('subscribe').appendChild(messageElem);
}


function onError(error) {
    console.log("Ошибка " + error.message);
};

function clickSend() {
    var message = document.getElementById('message').value;
    if (!message) return;
    document.getElementById('message').value = "";

    var messageElem = document.createElement('div');
    messageElem.classList.add('message-style');
    messageElem.classList.add('your-message');
    messageElem.appendChild(document.createTextNode(message));

    var parentElem = document.createElement('div');
    parentElem.classList.add('media');
    parentElem.appendChild(messageElem);

    var block = document.getElementById('message-body')
    block.appendChild(parentElem);
    block.scrollTop = block.scrollHeight; //чтобы прокручивалось в конец
    // sendMessage("user", form.elements.message.value);
}

function sendMessage(nick, message) {
    var object = {
        type: "MESSAGE",
        nick: nick,
        message: message
    };
    showMessage(object);
    socket.send(JSON.stringify(object));
}

function sendResult(choice) {
    var object = {
        type: "RESULT",
        choice: choice
    };

    socket.send(JSON.stringify(object))
}

function clickBtn(obj) {
    buttonsDisable(true);

    var choice;
    switch (obj.id) {
        case 'rock':
            choice = "ROCK";
            break;
        case 'paper':
            choice = "PAPER";
            break;
        case 'scissors':
            choice = "SCISSORS";
    }

    sendResult(choice);
};

document.forms[0].onsubmit = function() {
    var value = this.elements.message.value;
    if (value == '') return false; // игнорировать пустой submit

    clickSend();
    return false;
};