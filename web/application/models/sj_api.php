<?php
class Sj_api extends CI_Model {

	public $url = 'http://api.superjob.ru/1.0/';
	public $db_name = 'sj';
	function __construct()
	{
		parent::__construct();
	}
	function make_request($request) {
		$opts = array(
			'http'	=>	array(
			'method'	=>"GET",
			'header'	=>"User-Agent: jobshunter/1.0 (v.starkov@mail.ru)\r\n"."Cookie: foo=bar\r\n"
		  )
		);
		$context = stream_context_create($opts);
		$answer = file_get_contents($this->url.$request, false, $context);
		return $answer;
	}
	function get_categories() {
		$cats = json_decode($this->make_request('catalogues'));
		//print_r($cats);
		return($cats); 
	}
	function categories_to_db($cats) {
		foreach($cats as $parent_cat) {
			if (!($parent_id = $this->category_to_db($parent_cat))) {
				return false;
			}
			foreach($parent_cat->positions as $subcat) {
				if (!$this->category_to_db($subcat, $parent_id)) {
					return false;
				}
			}
		}
		return true;
 	}
	function category_to_db($in,$parent_id=null) {
		$cat = new Sourcecategories;
		if ($cat->where('site', $this->db_name)->where('site_id', $in->key)->where('category_name',$in->title_rus)->count()<0.5) {
			if ($cat->where('site', $this->db_name)->where('site_id', $in->key)->count()>0.5) {
				$cat->where('site', $this->db_name)->where('site_id', $in->key)->update('category_name',$in->title_rus);
				return true;
			} else {
				$cat->site=$this->db_name;
				$cat->site_id=$in->key;
				$cat->category_name=$in->title_rus;
				$cat->parent_id = $parent_id;
				if ($cat->save()) {
					return $cat->id;
				} else {
					return false;
				}
			}
		}
		return true;
	}
	function countries_to_db() {
		$countries = json_decode($this->make_request('countries'));
		foreach ($countries->objects as $country) {
			if(!$this->country_to_db($country)) {
				return false;
			}
		}
		return true;
	}
	function regions_to_db() {
		$regions = json_decode($this->make_request('regions/?all=1'));
		foreach ($regions->objects as $region) {
			if(!$this->country_to_db($region)) {
				return false;
			}
		}
		return true;
	}
	function cities_to_db() {
		$cities = json_decode($this->make_request('towns/?all=1'));
		foreach ($cities->objects as $city) {
			$this->towns_to_db($city);
		}
	}
	
	function country_to_db($city) {
		$city_model = new City;
		if ($city_model->get_city_by_name($city->title)) {
			if ($city_model->sj_id != $city->id) {
				$city_model->sj_id = $city->id;
				$city_model->sj_type = 'r';
				if($city_model->save()) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			$city_model = new City;
			$city_model->name = $city->title;
			$city_model->sj_id = $city->id;
			$city_model->sj_type = 'r';
			$city_model->parent_id = null;
			if($city_model->save()) {
				return true;
			} else {
				return false;
			}
		}
	}
	function towns_to_db($city) {
		$city_model = new City;
		if ($city_model->get_city_by_name($city->title)) {
			if ($city_model->sj_id != $city->id) {
				$city_model->sj_id = $city->id;
				$city_model->sj_type = 't';
				if($city_model->save()) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			$city_model = new City;
			$parent_city = $city_model->where('sj_id', $city->id_region)->get();
			$parent_id = null;
			if (!empty($parent_city->id)) {
				$parent_id = $parent_city->id;
			}
			$city_model->clear();
			$city_model->name = $city->title;
			$city_model->sj_id = $city->id;
			$city_model->sj_type = 't';
			$city_model->parent_id = $parent_id;
			if($city_model->save()) {
				return true;
			} else {
				return false;
			}
		}
	}
	function process_vacancies($in) {
		$this->load->model('city');
		$out = array();
		foreach($in->objects as $v) {
			$out[] = $this->process_vacansy($v);
		}
		return $out;
	}
	function process_vacansy($v) {
		$obj = array();
		$obj['site'] = 'sj';
		$obj['site_id'] = $v->id;
		$obj['url'] = $v->link;
		$obj['name'] = $v->profession;
		$obj['city'] = $v->town->title;
		$obj['city'] = $v->town->title;
		$obj['description'] = $v->work;
		$obj['creation_date'] = date('Y-m-d H:i:s', $v->date_published);
		$obj['employer'] = $v->firm_name;
		$obj['salary_from'] = $v->payment_from;
		$obj['salary_to'] = $v->payment_to;
		$obj['salary_currency'] = 'RUR';
		$obj = (object)$obj;
		return $obj;
	}
	function get_vacancies() {
		$this->load->model('settings');
		$days_to_parse = $this->settings->parse_days;
		$vac_list = new Vacancy_list;
		for($page=0; $page<=5; $page++) {
			$results = json_decode($this->make_request('vacancies/?count=100&page='.$page));
			foreach($results->objects as $result) {
				$vac_list->load_from_jobsite($result,'sj');
				$vac_list->last_item()->to_db();
			}
		}
		return $vac_list;
	}
}