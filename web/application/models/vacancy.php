﻿<?php
class Vacancy extends CI_Model {
	var $id;
	var $salary_from;
	var $salary_to;
	var $date;
	var $job;
	var $description;
	var $currency;
	var $worktype;
	var $categories;
	var $city;
	var $company;
	var $link;
	function load_from_hh($result) {
		if (!isset($result->id) || !$result->id) {
			return false;
		}
		if ($result->salary) {
			$rate = 1;
			$result->salary->currency = strtoupper($result->salary->currency);
			if (isset($this->settings->{'cur_'.strtolower($result->salary->currency)})) {
				$this->currency = 'RUB';
				$rate = $this->settings->{'cur_'.strtolower($result->salary->currency)};
			} else {
				$this->currency = str_replace('RUR','RUB',$result->salary->currency);
			}
			if ($result->salary->to) {
				$this->salary_to = $rate*$result->salary->to;
			}
			$this->salary_from = $rate*$result->salary->from;
		}
		$exploded_time = explode('T',$result->published_at);
		$this->{'date'} = $exploded_time[0].' '.current(explode('+',$exploded_time[1]));
		$this->job = $result->name;
		$this->description = strip_tags(str_replace(array("<ul>","</li>"),"\n",$result->description));
		if ($result->schedule) {
			switch ($result->schedule->id) {
				case 'fullDay':
					$this->worktype = 1;
					break;
				case 'shift':
					$this->worktype = 3;
					break;
				case 'part':
					$this->worktype = 2;
					break;
				case 'remote':
					$this->worktype = 6;
					break;
			}
		}
		if ($result->employment) {
			if ($result->employment->id=='project') {
				$this->worktype = 4;
			}
		}
		$cats = array();
		if ($result->specializations) {
			$this->load->model('sourcecategories');
			$this->load->model('site_source_categories');
			foreach($result->specializations as $spec) {
				$cat = reset($this->sourcecategories->where('site','hh')->where('site_id',$spec->id)->get()->all_to_array());
				$cats[] = (string)$cat['id'];
			}
		}
		$this->categories = json_encode($cats);
		if ($result->area)	{
			$this->load->model('City');
			$city = reset($this->City->where('hh_id',$result->area->id)->get()->all_to_array());
			$this->city = json_encode($this->City->get_all_parents_for_city($city['id'],true));
		}
		if ($result->employer) {
			$this->company = $result->employer->name;
		}
		$this->link = $result->alternate_url;
		return true;
	}
	
	function load_from_sj($result) {
		if (!isset($result->id) || !$result->id) {
			return false;
		}
		$rate = 1;
		if (isset($this->settings->{'cur_'.$result->currency})) {
			$this->currency = 'RUB';
			$rate = $this->settings->{'cur_'.$result->currency};
		} else {
			$this->currency = strtoupper($result->currency);
		}
		if ($result->payment_to!=0) {
			$this->salary_to = $rate*$result->payment_to;
		}
		if ($result->payment_from!=0) {
			$this->salary_from = $rate*$result->payment_from;
		}
		$this->{'date'} = date('Y-m-d H:i:s',strtotime('+4 hours', $result->date_published));
		$this->job = $result->profession;
		$this->description = "Обязанности: \n".$result->work."Условия: \n".$result->compensation."Требования: \n".$result->candidat;
		if (isset($result->type_of_work->id)) {
			switch ($result->type_of_work->id) {
				case 6:
					$this->worktype = 1;
					break;
				case 10:
					$this->worktype = 2;
					break;
				case 12:
					$this->worktype = 3;
					break;
				case 13:
					$this->worktype = 2;
					break;
				case 7:
					$this->worktype = 4;
					break;
				case 9:
					$this->worktype = 5;
					break;
			}
		}
		$cats = array();
		if ($result->catalogues) {
			$this->load->model('sourcecategories');
			$this->load->model('site_source_categories');
			foreach($result->catalogues as $spec) {
				$id = $spec->id;
				$cat = reset($this->sourcecategories->where('site','sj')->where('site_id',$id)->get()->all_to_array());
				$cats[] = (string)$cat['id'];
			}
		}
		$this->categories = json_encode($cats);
		if ($result->town)	{
			$this->load->model('City');
			$city = reset($this->City->where('sj_id',$result->town->id)->get()->all_to_array());
			$this->city = json_encode($this->City->get_all_parents_for_city($city['id'],true));
		}
		if ($result->firm_name) {
			$this->company = $result->firm_name;
		}
		$this->link = $result->link;
		return true;
	}
	
	function to_db() {
		$this->db->insert('vacancies', $this);
	}
	
	function load_from_db_row($row) {
		foreach(array('id','salary_from','salary_to','date','job','description','currency','worktype','categories','city','company','link') as $var) {
			if(isset($row->$var)) {
				$this->$var = $row->$var;
			}
		}
	}
	function check_if_unique_in_db() {
		$this->db->where('link',$this->link);
		$this->db->from('vacancies');
		if ($this->db->count_all_results()>0.5) {
			return false;
		} else {
			return true;
		}
	}
}