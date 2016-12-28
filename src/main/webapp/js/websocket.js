//todo  нужно блокировать чат, пока соединение не установленно

var socket;
var chatInput;
var urlInput;
//var rock, paper, scissors;

window.onload = function init() {
    //rock = document.getElementById('rock');
    //paper = document.getElementById('paper');
    //scissors = document.getElementById('scissors');
    document.getElementById('chat-input').disabled = true;

    //инициализация
    urlInput = document.querySelector('#url');
    chatInput = document.querySelector('#chat-input > input');

    //установка обработчиков
    urlInput.value = 'текст для тестирования виделения';
    urlInput.onfocus = function (event) {
        var r = document.createRange();
        r.selectNode(event.target);
        window.getSelection().addRange(r);
    };
    chatInput.onkeydown = function (event) {
        if (event.keyCode == 13) clickSend();
    };

    document.querySelector('#select-box').onclick = clickBtn;
    document.querySelector('#chat-input > span').onclick = clickSend;

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
            showConnection(incomingMessage.connection);
            break;
        case 'ID':
            window.location.hash = incomingMessage.id;
            urlInput.value = window.location.href;
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

function showResult(resultObj) {
    if (resultObj.result === 'WIN')
        document.getElementById('your-score').textContent++;
    else if (resultObj.result === "LOSE")
        document.getElementById('opp-score').textContent++;

    //var messageElem = document.createElement('div');
    //messageElem.appendChild(document.createTextNode(JSON.stringify(message)));
    //document.getElementById('subscribe').appendChild(messageElem);
}

function sendResult(choice) {
    var object = {
        type: "RESULT",
        choice: choice
    };

    socket.send(JSON.stringify(object));
}

function showConnection(connected) {
    if (connected) {
        if (!document.getElementById('cover')) {
            document.getElementById('main-box').hidden = false;
            document.getElementById('url-box').hidden = true;
        } else {
            hideCover(); //если оппонент решил вернуться
        }
    } else {
        showCover();
    }
}

//=========================================
//   Methods for event handling
//=========================================

// chatInput.onkeydown = function (event) {
//     if (event.keyCode == 13) clickSend();
// };
//
// document.querySelector('#chat-input > span').onclick = clickSend;

function clickSend() {
    var message = chatInput.value.trim();
    if (!message) return;

    //очистить поле ввода
    chatInput.value = '';

    showMessage(message, true);
    sendMessage(message);
};

function clickBtn(event) {
    var choice;
    var target = event.target
    while (target.tagName !== 'BUTTON') {
        if (this === target) return;
        target = target.parentNode;
    }

    console.log(target.getAttribute('data-choice'));
    sendResult(target.getAttribute('data-choice'));
};

//==============================================
//          Methods for cover window
//==============================================

// Показать полупрозрачный DIV, затеняющий всю страницу
// (а форма будет не в нем, а рядом с ним, чтобы не полупрозрачная)
function showCover() {
    var cover = document.createElement('div');
    cover.id = 'cover';
    cover.classList.add('cover');

    var windowDiv = document.createElement('div');
    windowDiv.classList.add('window');
    windowDiv.textContent = 'Your opponent disconnected.';
    cover.appendChild(windowDiv);

    document.body.appendChild(cover);
}

function hideCover() {
    document.body.removeChild(document.getElementById('cover'));
}
