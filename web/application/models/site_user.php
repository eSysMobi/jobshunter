<?
class Site_user extends CI_Model {
	private $login = 'admin';
	private $pass = 'cleansing';
	function __construct()
	{
		parent::__construct();
	}
	function login($login,$pass) {
		if ($login==$this->login && $pass==$this->pass) {
			$this->open_session($login,'admin');
			return true;
		} else {
			return false;
		}
	}
	
	private function open_session($login,$type) {
		$this->session->set_userdata(array('logged_in' => true, 'type' => $type, 'login' => $login));
	} 
}

?>