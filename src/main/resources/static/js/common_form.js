
$(document).ready(function(){
		$("#buttonCancel").on("click",function(){
			window.location = modulURL;
			});
			
		$("#fileImage").change(function(){
			if(!checkFileSize(this)){
				return;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
			}
				showImageThumbnail(this);
			
			});	
		});
		
		
	function checkFileSize(fileInput){
		
			fileSize = fileInput.files[0].size;
			
			if(fileSize > MAX_FILE_SIZE){
				fileInput.setCustomValidity("You must choose an image lass than 1MB!");	
				fileInput.reportValidity();
				return false;
			}else{
				fileInput.setCustomValidity("");
				return true;
			 }
	}	
	function showImageThumbnail(fileInput){	
		var file = fileInput.files[0];
		var reader = new FileReader();
		reader.onload = function(e){
			$("#thumbnail").attr("src", e.target.result);
		}
		reader.readAsDataURL(file);
	
	}
	