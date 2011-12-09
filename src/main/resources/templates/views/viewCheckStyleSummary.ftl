<html>
<head>
	<title> [@ui.header pageKey='CheckStyle Summary' object='${build.name}' title=true /]</title>
	<meta name="tab" content="checkstyle"/>
</head>

<body>
    [@ui.header pageKey='CheckStyle Summary' object='${build.name}' /]
    <div id="graphs" class="topPadded">
        <div id="graph1">
            [@ww.action name="viewCheckStyleViolationSummary" namespace="/build" executeResult="true" /]
        </div>
    </div>
</body>
</html>