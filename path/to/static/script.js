document.getElementById("checkButton").onclick = function (e) {
    e.preventDefault()
    const table = document.getElementById("checkTable");
    let x = $("select[name='x-param']").val();
    let y = $("input[name='y-param']").val();
    let r = $("input[type='radio'][name='r-param']:checked").val();

    if (isNaN(x) || isNaN(y) || isNaN(r) || y < -3 || y > 5) {
        return;
    }

    let data = {
        "x": x,
        "y": y,
        "r": r
    }

    $.ajax({
        url: "/fcgi-bin/server.jar?" + $.param(data),
        type: "GET",
        dataType: "json",
        success: function(response) {
            if (response.error != null) {
                alert("Ошибка");
                return;
            }

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
            rowHit.textContent = response.result;

            // TODO: почему-то этого нет. Считать время, может стоит считать тут
            rowReqTime = response.now;
            rowWorkTime = response.time;
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Ошибка:", textStatus, errorThrown);
        }

    })
};

document.getElementById("clean").onclick = function (e) {
    e.preventDefault()
    // TODO: очистка таблицы
}



function validateX() {
    let x = document.getElementById("x-select").value;
    return true;
}

function validateY() {
    let y = document.getElementById("y-input").value;
    if (y == undefined) {
        return false;
    }
    if (y < -3 || y > 5) {
        return false;
    }
    return true;
}

function validateR() {
    return true;
}