function onUserChange(listId, storeFieldId) {
	var storeField = document.getElementById(storeFieldId);
	var list = document.getElementById(listId);

	var selIndex = list.selectedIndex;

	if (storeField != null && selIndex != null) {
		storeField.value = list.options[selIndex].value;
	}
};

function assignToUser(userOptionId, listId, storeFieldId) {
	var option = document.getElementById(userOptionId);
	var list = document.getElementById(listId);

	var i = option.index;

	if (list.selectedIndex != i) {
		list.selectedIndex = i;
		onUserChange(listId, storeFieldId);
	}
};

function onCbSelected(cbId, textFieldId) {
	var cb = document.getElementById(cbId);
	var textField = document.getElementById(textFieldId);

	if (cb.checked.toString() == 'true') {
		textField.value = "true";
	} else {
		textField.value = "false";
	}
};