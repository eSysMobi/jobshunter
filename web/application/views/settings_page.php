<form action="<?echo site_url('admin/save_settings');?>" method="post" class="jNice">
	<fieldset>
		<?foreach(array(array('parse_days','Количество дней парсинга'),array('cur_uah','Курс гривен'),array('cur_byr','Курс белорусских рублей'),
		array('cur_usd','Курс доллара'), array('cur_eur','Курс евро')) as $vars) {?>
		<p>
			<label><?echo $vars[1];?>:</label>
			<input type="text" name="<?echo $vars[0];?>" class="text-medium" value="<?echo $settings->$vars[0];?>" />
		</p>
		<?}?>
		<input type="submit" value="Сохранить" />
	</fieldset>
</form>