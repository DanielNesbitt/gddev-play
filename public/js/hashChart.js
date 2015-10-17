(function() {
    var ctx = document.getElementById("hashtagChart").getContext("2d");

    $('[data-bar-chart]').each(function (i, svg) {
        var $svg = $(svg);
        var data = $svg.data('data').split(',').map(function (datum) {
            return parseFloat(datum);
        });

        var barWidth = parseFloat($svg.data('bar-width')) || 15;
        var barSpace = parseFloat($svg.data('bar-space')) || 0.5;
        var chartHeight = $svg.outerHeight();

        var y = d3.scale.linear()
            .domain([0, d3.max(data)])
            .range([0, chartHeight]);

        d3.select(svg)
            .selectAll("rect")
            .data(data)
            .enter().append("rect")
            .attr("class", "bar")
            .attr("width", barWidth)
            .attr("x", function (d, i) { return barWidth*i + barSpace*i; })
            .attr("y", chartHeight)
            .attr("height", 0)
            .transition()
            .delay(function (d, i) { return i*100; })
            .attr("y", function (d, i) { return chartHeight-y(d); })
            .attr("height", function (d) { return y(d); });
    });


    var ws = new WebSocket('ws://localhost:9000/ws');
    ws.onmessage = function(event) {
        var data = JSON.parse(event.data);
        var chart = new Chart(ctx).Bar(data);
        console.log(data);
    }

})();