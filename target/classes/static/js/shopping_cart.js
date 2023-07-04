
decimalSeparator = decimalPointType == "COMMA" ? "," : ".";
thousandsSeparator = thousandsPointType == "COMMA" ? "," : ".";


$(document).ready(function(){
	
	$(".linkMinus").on("click",function(evt){
		evt.preventDefault();
		
		decreaseQuantity($(this));
	});
	
	$(".linkPlus").on("click",function(evt){
		evt.preventDefault();
	
		increaseQuantity($(this));
	});
		
	
	$(".linkRemove").on("click",function(evt){
		evt.preventDefault();
		removeProduct($(this));
	});	
	
});

function decreaseQuantity(link){
	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val())-1;
	
	if(newQuantity > 0){
		quantityInput.val(newQuantity);
		updateQuantity(productId,newQuantity);
		
		$("#minus" + productId).removeClass("disabled").prop("disabled", false);
		$("#plus" + productId).removeClass("disabled").find("a").prop("disabled", false);
			
	}else if(newQuantity==0){
		showWarningModal("Minimum quantity is 1");
		$("#minus" + productId).addClass("disabled").find("a").prop("disabled", true);
		$("#plus" + productId).removeClass("disabled").find("a").prop("disabled", false);
	}
}
function increaseQuantity(link){
	
		productId = link.attr("pid");
		quantityInput = $("#quantity" +productId);
		newQuantity = parseInt(quantityInput.val()) + 1;
		
		if(newQuantity <= 5){
			quantityInput.val(newQuantity);
			updateQuantity(productId,newQuantity);
			
			$("#plus" + productId).removeClass("disabled").find("a").prop("disabled", false);
			$("#minus" + productId).removeClass("disabled").find("a").prop("disabled", false);
		}else if(newQuantity>5){
		showWarningModal("Maximun quantity is 5");	
		$("#plus" + productId).addClass("disabled").find("a").prop("disabled", true);
		
	}
}
	function updateQuantity(productId,quantity){
		url = contextPath + "cart/update/"+ productId +"/" + quantity;
	$.ajax({
		type:"POST",
		url:url,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		}
		
	}).done(function(updatedSubtotal){
		updateSubtotal(updatedSubtotal, productId);
		updateTotal();
	}).fail(function(){
		showErrorModal("Error while adding product to shopping cart.");
		});
	}
	
	
	function updateSubtotal(updatedSubtotal, productId){
		
		$("#subtotal" + productId).text(formatCurrency(updatedSubtotal));
	}
	
	function updateTotal(){
		total =0.0;
		productCount=0;
		$(".subtotal").each(function(index,element){
			productCount++;
			total += parseFloat(clearCurrencyFormat(element.innerHTML));
			
		});
		
		if(productCount <1){
			showEmptyShoppingCart();
		}else{
			
			$("#total").text(formatCurrency(total));
		}
		
	}
	
	
	function showEmptyShoppingCart(){
		$("#sectionTotal").hide();
		$("#sectionEmptyCartMessage").removeClass("d-none");
	}
	
	function removeProduct(link){
		url = link.attr("href");
		$.ajax({
		type:"DELETE",
		url:url,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		}
		
	}).done(function(response){
		rowNumber = link.attr("rowNumber");
		removeHTML(rowNumber);
		updateTotal();
		updateCountNumber();
		showModalDialog("Shopping Cart",response);
	}).fail(function(){
		showErrorModal("Error while removing product.");
		});	
	}
	
	function removeHTML(rowNumber){
		$("#row" + rowNumber).remove();
		$("#blankLine" + rowNumber).remove();
		addItemToCart()
	}
	
	function updateCountNumber(){
		$(".divCount").each(function(index,element){
			element.innerHTML= "" + (index + 1);
		})	
	}
	
	function formatCurrency(amount){
		return $.number(amount,decimalDigits,decimalSeparator,thousandsSeparator);
	}	
	function clearCurrencyFormat(numberString){
		result = numberString.replaceAll(thousandsSeparator,"");
		return result.replaceAll(decimalSeparator,".");
	}
