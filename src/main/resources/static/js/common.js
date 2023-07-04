$(document).ready(function(){
	$("#logoutLink").on("click", function(e){
		e.preventDefault();
		document.logoutForm.submit();
		});	
		customizeDropDownMenu();
		customizeTabs();   
	});
	
	function customizeDropDownMenu(){
		$(".navbar .dropdown").hover(
			function(){
				$(this).find('.dropdown-menu').first().stop(true,true).delay(250).slideDown();
			},
			function(){
				$(this).find('.dropdown-menu').first().stop(true,true).delay(10).slideUp();
			}
		
		);
		
		$(".dropdown > a").click(function(){
			location.href = this.href;
		});
	}
function customizeTabs(){
	var url = document.location.toString();
	if(url.match("#")){
		$('.nav-tabs a[href="#' + url.split('#')[1] + '"]').tab('show');
	}
	$('.nav-tabs a').on('show.bs.tab', function(e){
		window.location.hash= e.target.hash;
	})
}