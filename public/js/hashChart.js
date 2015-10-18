var pubnub = PUBNUB.init({
    publish_key: 'demo',
    subscribe_key: 'demo'
});

eon.chart({
    channel: "c3-spline",
    generate: {
        bindto: '#chart',
        data: {
            labels: true
        }
    }
});

var ws = new WebSocket('ws://localhost:9000/ws');
ws.onmessage = function (event) {
    var data = event.data;
    pubnub.publish({
        channel: 'c3-spline',
        message: {
            eon: data
        }
    })
};