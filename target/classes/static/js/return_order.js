var returnModal;
var titleModal;
var fieldNote;
var divReason;
var divMessage;
var firstButton;
var secondButton;
$(document).ready(function(){
	returnModal = $("#returnOrderModal");
	titleModal = $("#returnOrderModalTitle");
	fieldNote= $("#returnNote");
	divReason= $("#divReason");
	divMessage = $("#divMessage");
	firstButton = $("#firstButton");
	secondButton = $("#secondButton");
	handleReturnOrderLink();
});

function showReturnModalDialog(link){
	divMessage.hide();
	divReason.show();
	firstButton.show();
	secondButton.text("Cancel");
	fieldNote.val("");
	orderId = link.attr("orderId");
	titleModal.text("Return Order Id #" + orderId);
	returnModal.modal("show");
	
}
function showMessageModal(message){
	divReason.hide();
	firstButton.hide();
	secondButton.text("Close");
	divMessage.show();
	divMessage.text(message);
}

function handleReturnOrderLink(){
	$(".linkReturnOrder").on("click", function(e){
		e.preventDefault(); //ngan chan hanh vi mac dinh cua mot gui bieu mau
		showReturnModalDialog($(this));
	});
}

function submitReturnOrderForm(){
	reason = $("input[name='returnReason']:checked").val();
	note = fieldNote.val();
	sendReturnOrderRequest(reason,note);
	return false;
}

function sendReturnOrderRequest(reason,note){
		alert(reason + note + orderId);
	requestURL  = contextPath + "orders/return";
	requestBody = {orderId: orderId, reason: reason, note:  note};
	$.ajax({
		type:"POST",
		url:requestURL,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		},
		data: JSON.stringify(requestBody),
		contentType : 'application/json'
	}).done(function(returnResponse){
		console.log(returnResponse);
		showMessageModal("Return request has been send");
		updateStatusTextAndHideReturnButton(orderId);
	}).fail(function(err){
		console.log(err);
		showMessageModal(err.responseText);
		});
}
function updateStatusTextAndHideReturnButton(orderId){
	$(".textOrderStatus" + orderId).each(function(index){
		$(this).text("RETURN_REQUESTED");
	});
	$(".linkReturn" + orderId).each(function(index){
		$(this).hide();
	});
}