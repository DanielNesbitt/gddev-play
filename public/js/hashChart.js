var chart = c3.generate({
    data: {
        columns: []
    }
});

var ws = new WebSocket('ws://localhost:9000/ws');
ws.onmessage = function (event) {
    var data = JSON.parse(event.data);
    chart.load(data);
};