<div id="categories_wrapper">
	<script type="text/javascript">
		$(document).on('click', '.plus', function() {
			$(this).parent().append($("#categories_wrapper div.adding_frame").eq(0).clone().removeClass('hidden'));
			
		});
		$(document).on('click', '.minus', function() {
			$(this).parent().remove();
			
		});
		$(document).on('click', '.add_button', function() {
			var sj_select = $(this).parent().find('[name="sj_cat"]').eq(0);
			var sj_id = sj_select.val().replace('id','');
			var sj_name = sj_select.find('option:selected').text().replace('—','');
			$(this).parent().parent().append($("#categories_wrapper input.hid_fld").eq(0).clone().prop('value',sj_id).prop('name','catid[]'));
			var sj_match = $("#categories_wrapper div.match").eq(0).clone().removeClass('hidden');
			sj_match.find('span').eq(0).text(sj_name);
			sj_match.find('span').eq(0).attr('name','id'+sj_id);
			console.log(sj_match);
			$(this).parent().parent().find('.sjmatches').append(sj_match);
			$(this).parent().remove();
		});
		$(document).on('click', '.delete_match', function() {
			var id = $(this).parent().find('span').eq(0).attr("name").replace('id','');
			$(this).parent().parent().parent().parent().find('input.hid_fld[value='+id+']').eq(0).remove();
			$(this).parent().remove();
		});
	</script>
	<div class='hidden adding_frame'><div class='minus'><img src='<?echo base_url();?>assets/img/delete.png'></div> SJ: <?echo form_dropdown('sj_cat', $sj_tree->arrayForSelect(),'', 'style="width:150px;"');?><button class="add_button" type='button'>Добавить</button></div>
	<input type="hidden" class="hid_fld" name="" value="">
	<div class="hidden match"><span name=""></span><div class="delete_match"><img src='<?echo base_url();?>assets/img/delete.png'></div></div>
	<div class='hidden minus'><img src='<?echo base_url();?>assets/img/delete.png'></div>
	<? echo $cat_tree->displayForAdmin(0); ?>
</div>