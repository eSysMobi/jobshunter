<?
class utility_functions extends CI_Model {
	public static function days_ago($str_date) {
		$now = time()+ 60*60;
		$your_date = strtotime($str_date);
		$datediff = $now - $your_date;
		return($datediff/(60*60*24));
	}
}