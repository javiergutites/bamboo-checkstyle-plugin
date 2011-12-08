<html>
	<head>
		<title> [@ui.header pageKey='CheckStyle' object='${build.name} ${buildResults.buildNumber}' title=true /]</title>
	</head>
	<body>
    [@cp.resultsSubMenu selectedTab='checkstyle' /]
    [#assign customDataMap=buildResults.buildResultsSummary.customBuildData /]
    
<style type="text/css">
    table.flexi {
        width: auto;
    }

    table.grid {
        margin-top: 10px;
        margin-left: 2px;
    }

    table.grid th {
        font-weight: bold;
        text-align: center;
    }

    table.grid th.textLeft {
        text-align: left;
    }

    .textRight {
        text-align: right;
    }

    table.grid tr.High th {
        background-color: #ff9999;
        color: #000;
        text-align: left;
    }

    table.grid tr.High td {
        background-color: #ff9999;
        color: #000;
        text-align: right;
    }

    table.grid tr.Medium th {
        background-color: #ffcc99;
        color: #000;
        text-align: left;
    }

    table.grid tr.Medium td {
        background-color: #ffcc99;
        color: #000;
        text-align: right;
    }

    table.grid tr.Low th {
        background-color: #ffffcc;
        color: #000;
        text-align: left;
    }

    table.grid tr.Low td {
        background-color: #ffffcc;
        color: #000;
        text-align: right;
    }

    table.grid tr td.vio {
        background-color: #ff9999;
        color: #000000;
    }

    table.grid tr td.fixed {
        background-color: #a6dba6;
        color: #000000;
    }

    .width70 {
        width: 70%;
    }

    .width30 {
        width: 30%;
    }

    .width20 {
        width: 20%;
    }
</style>
    
    <div class="section editConfiguration">
    	<script type="text/javascript">
		    var myTabs = new YAHOO.widget.TabView("fb_summary");
		</script>

		<div id="fb_summary" class="yui-navset">
			<ul class="yui-nav">
			    <li class="selected"><a href="#"><em>Checkstyle Summary</em></a></li>
			    <li><a href="#"><em>Top Violations</em></a></li>
			</ul>
			
	    <div class="yui-content">
	
			<div class="tabContent">
			    [@ui.bambooInfoDisplay]
			    <table class="grid flexi">
			        <tr>
			            <th>Priority</th>
			            <th>Violations</th>
			            <th>&Delta;</th>
			        </tr>
			
					<tr class="High">
						<th><b>ERROR</b></th>
						<td class="width20">${customDataMap.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS?if_exists}</td>
						<td class="width20">${customDataMap.CHECKSTYLE_ERROR_VIOLATION_DELTA?if_exists}</td>
					</tr>
			
					<tr class="Medium">
						<th><b>WARNING</b></th>
						<td class="width20">${customDataMap.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS?if_exists}</td>
						<td class="width20">${customDataMap.CHECKSTYLE_WARNING_VIOLATION_DELTA?if_exists}</td>
					</tr>
			
					<tr class="Low">
						<th><b>INFO</b></th>
						<td class="width20">${customDataMap.CHECKSTYLE_INFO_PRIORITY_VIOLATIONS?if_exists}</td>
						<td class="width20">${customDataMap.CHECKSTYLE_INFO_VIOLATION_DELTA?if_exists}</td>
					</tr>
			
			        <tr>
			            <th class="textLeft">TOTAL</th>
			            <td class="width20 textRight">${customDataMap.CHECKSTYLE_TOTAL_VIOLATIONS?if_exists}</td>
			            <td class="width20 textRight">${customDataMap.CHECKSTYLE_TOTAL_VIOLATION_DELTA?if_exists}</td>
			        </tr>
			    </table>
			    [/@ui.bambooInfoDisplay]
			</div>
    
			[#if !topViolations.isEmpty()]
			<div class="tabContent">
			  <table class="grid flexi">
			      [#if !topViolations.isEmpty()]
			          <tr>
			              <th>File</th>
			              <th>Violations</th>
			          </tr>
			          [#list topViolations as violationInformation]
			              <tr>
			              [#if violationInformation.fileName?contains("http://")]
			                  <td class="vio" title="${violationInformation.fileName?split("#")?last}"><a href="${violationInformation.fileName}">${violationInformation.fileName?split("#")?last}</a></td>
					      [#else]
					           <td class="vio" title="${violationInformation.fileName?split("#")?last}">${violationInformation.fileName?split("#")?last?split("/")?last}</td>
					      [/#if]
			                  <td class="vio textRight">${violationInformation.numberOfViolations?string('0')}</td>
			              </tr>
			          [/#list]
			      [#else]
			          <tr>
			              <td>No violators found.</td>
			          </tr>
			      [/#if]
			  </table>
			</div>
			[/#if]  
		</div>
		</div>
		</div>
	</body>
</html>