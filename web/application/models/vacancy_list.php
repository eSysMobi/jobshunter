<?
class Vacancy_list extends CI_Model {
	public $items = array();
	function load_from_jobsite($results,$site) {
		if (!is_array($results)) {
			$results = array($results);
		}
		foreach($results as $result) {
			$vacancy = new Vacancy;
			$vacancy->{'load_from_'.$site}($result);
			$this->items[] = $vacancy;
		}
	}
	function last_item() {
		return end($this->items);
	}
}