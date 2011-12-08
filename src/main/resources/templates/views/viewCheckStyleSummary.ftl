<html>
<head>
	<title> [@ui.header pageKey='CheckStyle Summary' object='${build.name}' title=true /]</title>
</head>

<body>
    [@ui.header pageKey='CheckStyle Summary' object='${build.name}' /]
    [@cp.buildSubMenu selectedTab='checkstyle' /]
    <div id="graphs" class="topPadded">
        <div id="graph1">
            [@ww.action name="viewCheckStyleViolationSummary" namespace="/build" executeResult="true" /]
        </div>
    </div>
</body>
</html>