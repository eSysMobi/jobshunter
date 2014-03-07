<?
if ($content=='login') {
	$this->load->view('login_form');
}
if ($content=='index') {
	$this->load->view('index_page');
}
if ($content=='categories') {
	$this->load->view('category_page');
}
if ($content=='settings') {
	$this->load->view('settings_page');
}
?>