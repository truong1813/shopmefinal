$(document).ready(function(){
			$(".link-delete").on("click",function(e){
				e.preventDefault();
				link=$(this);
				todoId =link.attr("todoId");
				$("#yesButton").attr("href",link.attr("href"));
				$("#confirmText").text("Bạn có chắc chắn muốn xoá công việc: " + todoId);
				$("#confirmModal").modal();
			});
			var currentDate = new Date().toISOString().split('T')[0];
	        $('#fromDate').val(currentDate);
	        $('#toDate').val(currentDate);
	        
	        $("#fromDate").change(function() {
	            validateDates();
	          });

	          // Gắn hàm xử lý sự kiện cho trường completedDate
	          $("#toDate").change(function() {
	            validateDates();
	          });
		});
		
		
		
		function validateDates() {
		    var createDate = new Date($("#fromDate").val());
		    var completedDate = new Date($("#toDate").val());

		    if (createDate > completedDate) {
		      $("#createMessage").text("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày hoàn thành.");
		      $("#formSearch").attr("onsubmit", "return false;");
		    } else {
		      $("#createMessage").text("");
		      $("#formSearch").removeAttr("onsubmit");
		     
		    }
		  }
		function clearFilter(){
			window.location = "[[@{/todos}]]";
		}