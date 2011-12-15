var inverse_sort = false;

$(document).ready(function() {

$('.thlink').click(function() {
	var header = $(this); 
	var tds = header.closest('table').find('td');
	var tds_in_header_column = tds.filter(function() {                     
		return $(this).index() === header.index();
	});
	
	tds_in_header_column.sort(function(a, b) { 
		a = $(a).text(); 
		b = $(b).text();
		
		var a_is_greater = false;
		
		if (isNaN(a) || isNaN(b)) {
			if (a > b)
				a_is_greater = true;
		}
		else {
			if (+a > +b)
				a_is_greater = true;
		}
		
		if (a_is_greater) {
			if (inverse_sort)
				return -1;
			else
				return 1;
		}
		else { // b is greater
			if (inverse_sort)
				return 1;
			else
				return -1;
		}
	}, function() {
		return this.parentNode; 
	});
	
	inverse_sort = !inverse_sort;
});
});