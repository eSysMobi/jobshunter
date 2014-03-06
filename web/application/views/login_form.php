<form action="<?echo site_url('admin/login');?>" method="post" class="jNice">
	<fieldset>
		<p>
			<label>Имя пользователя:</label>
			<input type="text" name="login" class="text-medium" />
		</p>
		<p>
			<label>Пароль</label>
			<input type="text" name="pass" class="text-medium" />
		</p>
		<input type="submit" value="Войти" />
	</fieldset>
</form>