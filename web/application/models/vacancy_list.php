<?php
class Vacancy_list extends CI_Model {
	const SUB_CATEGORY = 1;
	const SUB_WORK = 2;
	const SUB_CITY = 3;
	public $items = array();
	function load_from_jobsite($results,$site) {
		if (!is_array($results)) {
			$results = array($results);
		}
		foreach($results as $result) {
			$vacancy = new Vacancy;
			if ($vacancy->{'load_from_'.$site}($result) && $this->check_if_unique($vacancy)) {
				$this->items[] = $vacancy;
			}
		}
		return true;
	}
	function last_item() {
		return end($this->items);
	}
	function load_from_db() {
		$query = $this->db->get('vacancies');
		foreach ($query->result() as $row)	{
			$vacancy = new Vacancy;
			$vacancy->load_from_db_row($row);
			$this->items[] = $vacancy;
		}
	}
	function check_if_unique($vacancy) {
		foreach($this->items as $item) {
			if ($item->link==$vacancy->link) {
				return false;
			}
		}
		return true;
	}
	
}