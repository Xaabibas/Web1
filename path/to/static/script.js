const table = document.getElementById("checkTable");

document.getElementById("checkButton").onclick = async function (e) {
    e.preventDefault();
    let x = $("select[name='x-param']").val();
    let y = $("input[name='y-param']").val();
    let r = $("input[type='radio'][name='r-param']:checked").val();

    if (!(validateX(x) && validateY(y) && validateR(r))) {
        return;
    }

    let data = { x, y, r };

    try {
        let start = new Date();

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
            alert(json.error);
            return;
        }

        let end = new Date();
        append(json, x, y, r, start, end);
    } catch(err) {
        alert("Ошибка: " + err.message);
        console.log(err.message);
    }
};

document.getElementById("clean").onclick = function (e) {
    e.preventDefault();
    while (table.rows.length > 1) {
        table.deleteRow(1);
    }
}

function append(json, x, y, r, start, end) {
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
    rowHit.textContent = json.result;
    rowReqTime.textContent = (start.getHours() > 10 ? start.getHours() : "0" + start.getHours()) + ":" +
            (start.getMinutes() > 10 ? start.getMinutes() : "0" + start.getMinutes()) + ":"
            + (start.getSeconds() > 10 ? start.getSeconds() : "0" + start.getSeconds());
    rowWorkTime.textContent = end.getTime() - start.getTime();
}

function validateX(x) {
    if (isNaN(x)) {
        alert("Не выбрано значение поля X");
        return false;
    }
    return true;
}

function validateY(y) {
    if (y == null || y == "") {
        alert("Не введено значеине поля Y")
        return false;
    }
    if (isNaN(y)) {
        alert("Введено некорректное значение поля Y")
        return false;
    }
    if (y < -3 || y > 5) {
        alert("Значение поля Y должно быть в промежутке [-3; 5]")
        return false;
    }
    return true;
}

function validateR(r) {
    if (isNaN(r)) {
        alert("Не выбрано значение поля R");
        return false;
    }
    if (r < 0) {
        alert("Радиус не может быть отрицательным");
        return false;
    }
    return true;
}