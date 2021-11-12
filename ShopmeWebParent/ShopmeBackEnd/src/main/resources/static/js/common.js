$(document).ready(function() {
	$("#logoutlink").on("click", function(e) {
		e.preventDefault();
		document.logoutForm.submit();
	});
});

function customizeDropdownMenu(){
	$(".dopdown > a").click(function(){
		location.href = this.href;
	});
}