<!-- TinyMCE -->
<script language="javascript" type="text/javascript"
	src="/js/wysiwyg/tiny_mce/tiny_mce.js"></script>
<script language="javascript" type="text/javascript">
	//layer,
	
	var value;
	
	tinyMCE.init({
		mode : "textareas",
		theme : "advanced",
		language :"zh_cn",
		editor_deselector : "mceNoEditor",
		plugins : "style,layer,table,save,advhr,advimage,advlink,emotions,iespell,insertdatetime,preview,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,media",
		theme_advanced_buttons1_add_before : "save,newdocument,separator",
		theme_advanced_buttons1_add : "fontselect,fontsizeselect",
		theme_advanced_buttons2_add : "separator,insertdate,inserttime,preview,separator,forecolor,backcolor",
		theme_advanced_buttons2_add_before: "cut,copy,paste,pastetext,pasteword,separator,search,replace,separator",
		theme_advanced_buttons3_add_before : "tablecontrols,separator",
		theme_advanced_buttons3_add : "emotions,iespell,media,advhr,separator,print,separator,ltr,rtl,separator,fullscreen",
		theme_advanced_buttons4 : "insertlayer,moveforward,movebackward,absolute,|,styleprops",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left",
		theme_advanced_path_location : "bottom",
		content_css : "/js/wysiwyg/css/full.css",
	    plugin_insertdate_dateFormat : "%Y-%m-%d",
	    plugin_insertdate_timeFormat : "%H:%M:%S",
		extended_valid_elements : "hr[class|width|size|noshade],font[face|size|color|style],span[class|align|style]",
		file_browser_callback : "fileBrowserCallBack",
		theme_advanced_resize_horizontal : false,

		cleanup_on_startup : false,
        verify_html : false,
       
		theme_advanced_resizing : true
	});
	
	function setValue(a){
		this.value=a;
	}
	
	function getValue() {
		tinyMCE.execCommand("mceInsertContent",false,this.value);
	}
	
	function fileBrowserCallBack(field_name, url, type, win) {
		// This is where you insert your custom filebrowser logic
		//alert("Example of filebrowser callback: field_name: " + field_name + ", url: " + url + ", type: " + type);
		var style = "dialogWidth:400px;dialogHeight:200px;help:0;status:0;scroll:0;resizable:0;";
		var path
		
		path = win.showModalDialog("/command?action=upload.Upload","",style);
		
		var reurl = path;
		// Insert new URL, this would normaly be done in a popup
		win.document.forms[0].elements[field_name].value = reurl;
	}
</script>
<!-- /TinyMCE -->