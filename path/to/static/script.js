const table = document.getElementById("checkTable");
const message = document.getElementById("message");
$("#message").fadeOut();

window.onload = function() {
    load()
}

document.getElementById("checkButton").onclick = async function (e) {
    e.preventDefault();

    let x = $("select[name='x-param']").val();
    let y = $("input[name='y-param']").val();
    let r = $("input[type='radio'][name='r-param']:checked").val();

    if (!(validateX(x) && validateY(y) && validateR(r))) {
        return;
    }

    try {
        let date = new Date();
        let start = dateToString(date);
        let data = { x, y, r, start };

        const response = await fetch("/fcgi-bin/server.jar", {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        const json = await response.json();
        if (json.error != null) {
            changeMessage(json.error);
            showFadeIn("#message");
            return;
        }

        append(x, y, r, json.result, start, json.time);
        $("#message").fadeOut();
    } catch(err) {
        changeMessage("Ошибка: " + err.message);
        console.log(err.message);
    }
};

document.getElementById("clean").onclick = async function (e) {
    e.preventDefault();

    const response = await fetch("/fcgi-bin/server.jar", {
        method: "PATCH",
        headers: {

        }
    });

    while (table.rows.length > 1) {
        table.deleteRow(1);
    }

}

function append(x, y, r, result, start, time) {
    let newRow = table.insertRow(1);
    const rowX = newRow.insertCell(0);
    const rowY = newRow.insertCell(1);
    const rowR = newRow.insertCell(2);
    const rowHit = newRow.insertCell(3);
    const rowReqTime = newRow.insertCell(4);
    const rowWorkTime = newRow.insertCell(5);

    rowX.textContent = x;
    rowY.textContent = y;
    rowR.textContent = r;
    rowHit.textContent = result;
    rowReqTime.textContent = start;
    rowWorkTime.textContent = time;
}

function dateToString(date) {
    return (date.getHours() > 9 ? date.getHours() : "0" + date.getHours()) + ":" +
                (date.getMinutes() > 9 ? date.getMinutes() : "0" + date.getMinutes()) + ":"
                + (date.getSeconds() > 9 ? date.getSeconds() : "0" + date.getSeconds());
}

function validateX(x) {
    if (isNaN(x)) {
        changeMessage("Не выбрано значение поля X");
        showFadeIn("#message");
        return false;
    }
    return true;
}

function validateY(y) {
    if (y == null || y == "") {
        changeMessage("Не введено значение поля Y");
        showFadeIn("#message");
        return false;
    }
    if (isNaN(y)) {
        changeMessage("Введено некорректное значение поля Y");
        showFadeIn("#message");
        return false;
    }
    if (y < -3 || y > 5) {
        changeMessage("Значение поля Y должно быть в промежутке [-3; 5]");
        showFadeIn("#message");
        return false;
    }
    return true;
}

function showFadeIn(field) {
    $(field).fadeIn();
}

function validateR(r) {
    if (isNaN(r)) {
        changeMessage("Не выбрано значение поля R");
        showFadeIn("#message");
        return false;
    }
    if (r < 0) {
        changeMessage("Радиус не может быть отрицательным");
        showFadeIn("#message");
        return false;
    }
    return true;
}

function changeMessage(str) {
    message.textContent = str;
}

function load() {
    fetch('data.csv')
        .then(response => response.text())
        .then(csv => {
            const rows = csv.split("\n");
            if (rows.length < 2) {
                return;
            }
            rows.slice(1).forEach(row => {
                const values = row.split(",");
                append(...values);
            })
        })

}