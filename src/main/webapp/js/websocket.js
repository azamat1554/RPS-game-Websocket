//todo  нужно блокировать чат, пока соединение не установленно

var socket;// = new WebSocket("ws://localhost:8080/RPS_Game_1.0/game/");
//var rock, paper, scissors;

 window.onload = function init() {
    //rock = document.getElementById('rock');
    //paper = document.getElementById('paper');
    //scissors = document.getElementById('scissors');

    var url = "ws://" + location.host + "/RPS_Game_1.0/game/" + location.hash.replace('#', '');
    socket = new WebSocket(url);
    socket.onopen = onOpen;
    socket.onclose = onClose;
    socket.onerror = onError;
    socket.onmessage = onMessage;
};

//============================================
//            WebSocket methods
//============================================

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

function onError(error) {
    console.log("Ошибка " + error.message);
};

//метод который вызывается, когда приходят сообщения
function onMessage(event) {
    var incomingMessage = JSON.parse(event.data);
    switch (incomingMessage.type) {
        case 'MESSAGE':
            showMessage(incomingMessage.message, false);
            break;
        case 'RESULT':
            showResult(incomingMessage);
            break;
        case 'CONNECTION':
            //showConnection(incomingMessage);
            break;
        case 'ID':
            window.location.hash = incomingMessage.id;
    }
}

//===========================================
//   Methods for handling messages
//===========================================

// показать сообщение в div#subscribe
function showMessage(message, isYour) {
    var newMessageElem = document.createElement('div');
    newMessageElem.classList.add('message-style');
    newMessageElem.classList.add(isYour ? 'your-message' : 'opp-message');
    newMessageElem.appendChild(document.createTextNode(message));

    var parentElem = document.createElement('div');
    parentElem.classList.add('media');
    parentElem.appendChild(newMessageElem);

    var block = document.getElementById('message-body');
    block.appendChild(parentElem);
    block.scrollTop = block.scrollHeight; //чтобы прокручивалось в конец
}

function showResult(message) {
    var messageElem = document.createElement('div');
    messageElem.appendChild(document.createTextNode(JSON.stringify(message)));
    document.getElementById('subscribe').appendChild(messageElem);
}

function sendMessage(message) {
    var msgJObj = {
        type: "MESSAGE",
        message: message
    };
    //showMessage(object);
    try {
        socket.send(JSON.stringify(msgJObj));
    } catch (exp) {
        console.log(exp)
    }
}

function sendResult(choice) {
    var object = {
        type: "RESULT",
        choice: choice
    };

    socket.send(JSON.stringify(object))
}

//=========================================
//   Methods for event handling
//=========================================

document.forms[0].onsubmit = function clickSend() {
    var message = document.getElementById('message').value;
    if (!message) return false;

    //очистить поле ввода
    document.getElementById('message').value = "";

/*    var newMessageElem = document.createElement('div');
    newMessageElem.classList.add('message-style');
    newMessageElem.classList.add('your-message');
    newMessageElem.appendChild(document.createTextNode(message));

    var parentElem = document.createElement('div');
    parentElem.classList.add('media');
    parentElem.appendChild(newMessageElem);

    var block = document.getElementById('message-body');
    block.appendChild(parentElem);
    block.scrollTop = block.scrollHeight; //чтобы прокручивалось в конец*/
    showMessage(message, true);
    sendMessage(message);

    //нужно чтобы браузер не отправил форму не сервер,
    //так событие отменяется.
    return false;
}

function clickBtn(obj) {
    //скрыть блок с кнопками

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

/*
document.forms[0].onsubmit = function() {
    var value = this.elements.message.value;
    if (value == '') return false; // игнорировать пустой submit

    clickSend(value);
    return false;
};*/
