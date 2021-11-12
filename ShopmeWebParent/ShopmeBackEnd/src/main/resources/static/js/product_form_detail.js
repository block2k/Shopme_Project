/**Author: Block
 * 
 */

function addNextDetailSection() {
	allDivDetails = $("[id^='divDetail']");
	divDetailsCount = allDivDetails.length;
	htmlDetailSection = `
	<div class="form-inline m-2" id="divDetail${divDetailsCount}">
			<label class="m-2">Name: </label>
			<input type="text" class="form-control w-25" name="detailNames" maxlength="255"/>
			<label class="m-2">Value: </label>
			<input type="text" class="form-control w-25" name="detailValues" maxlength="255"/>
		</div>
		`;
	previousDivDetailSection = allDivDetails.last();
	previousDivDetailId = previousDivDetailSection.attr("id");
	$("#divProductDetails").append(htmlDetailSection);
	htmlLinkRemove = `<a href="javascript:removeDetailSectionById('${previousDivDetailId}')" class="btn fas fa-times-circle fa-2x icon-dark" title="remove this detail"></a>`;
	previousDivDetailSection.append(htmlLinkRemove);
	
	$("input[name = 'detailNames']").last().focus();
}

function removeDetailSectionById(id) {
	$("#" + id).remove();
}