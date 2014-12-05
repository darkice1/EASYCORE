<html>
<script src="../../js/c/progressbar/progressbar.js"></script>
<body>
<span style="color:#006600;font-weight:bold;">Custom Progress Bar (without text, without animation)</span> <br/>
			<span id="element5">50%</span>
			<span class="extra"><a href="#" onClick="manualPB.setPercentage('0');return false;">Empty Bar</a></span>
			<span class="options"><a href="#" onClick="manualPB.setPercentage('+15');return false;">Add 15%</span>
			<span class="options"><a href="#" onclick="manualPB.setPercentage('-30');return false;">Minus 30%</a></span>
			<span class="options"><a href="#" onClick="manualPB.setPercentage('80');return false;">Set 80%</a></span>
			<span class="options"><a href="#" onClick="manualPB.setPercentage('50');return false;">Fill 50%</a></span>
			<span class="getOption"><a href="#" onClick="alert(manualPB.getPercentage());return false;">Get Current %</a></span>
			<span id="Text5" style="font-weight:bold">&laquo; Select Options</span>
			<br/><br/>
<script>
manualPB = new JS_BRAMUS.jsProgressBar(	$('element5'),0,
								{
									barImage	: Array(
										'/images/progressbar/percentImage_back4.png',
										'/images/progressbar/percentImage_back3.png',
										'/images/progressbar/percentImage_back2.png',
										'/images/progressbar/percentImage_back1.png'
									),
								});
</script>
</body>
</html>