var chart = c3.generate({
    bindto: '#chart',
    data: {
        columns: [],
        type: 'bar'
    },
    bar: {
        width: {
            ratio: 0.5 // this makes bar width 50% of length between ticks
        }
    }
});

var ws = new WebSocket('ws://localhost:9000/ws');
ws.onmessage = function (event) {
    var data = event.data;
    chart.load(data.event);
};