<?php
class Hh_api extends CI_Model {

	public $url = 'https://api.hh.ru/';
	public $db_name = 'hh';
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
		$try=1;
		while(!($answer = @file_get_contents($this->url.$request, false, $context)) && $try<=5) {
			$try++;
			sleep(0.5*$try);
		}
		return $answer;
	}
	function get_categories() {
		$cats = json_decode($this->make_request('specializations'));
		return($cats);
	}
	function categories_to_db($cats) {
		foreach($cats as $parent_cat) {
			if (!($parent_id = $this->category_to_db($parent_cat))) {
				return false;
			}
			foreach($parent_cat->specializations as $subcat) {
				if (!$this->category_to_db($subcat, $parent_id)) {
					return false;
				}
			}
		}
		return true;
 	}
	function category_to_db($in,$parent_id=null) {
		$cat = new Sourcecategories;
		if ($cat->where('site', $this->db_name)->where('site_id', $in->id)->where('category_name',$in->name)->count()<0.5) {
			if ($cat->where('site', $this->db_name)->where('site_id', $in->id)->count()>0.5) {
				$cat->where('site', $this->db_name)->where('site_id', $in->id)->update('category_name',$in->name);
				return true;
			} else {
				$cat->site=$this->db_name;
				$cat->site_id=$in->id;
				$cat->category_name=$in->name;
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
	function get_cities() {
		$cities = json_decode($this->make_request('areas'));
		foreach ($cities as $city) {
			$this->city_to_db($city);
		}
	}
	function city_to_db($city, $parent_id = null) {
		$city_model = new City;
		$city_model->name = $city->name;
		$city_model->hh_id = $city->id;
		$city_model->parent_id = $parent_id;
		$city_model->save();
		$parent_id = $city_model->id;
		if(!empty($city->areas)) {
			foreach($city->areas as $area) {
				$this->city_to_db($area,$parent_id);
			}
		}
	}
	function create_query_part($array,$value) {
		$first = true;
		$out = '';
		foreach($array as $array_row) {
			if (!$first) {
				$out .='&';
			}
			$out .= $value.'='.$array_row;
			$first = false;
		}
		return $out;
	}
	function process_vacancies($in) {
		$out = array();
		foreach($in->items as $v) {
			$out[] = $this->process_vacansy($v);
		}
		return $out;
	}
	function process_vacansy($v) {
		$this->load->model('city');
		$obj = array();
		$obj['site'] = 'hh';
		$obj['site_id'] = $v->id;
		$obj['url'] = $v->alternate_url;
		$obj['name'] = $v->name;
		$city = $this->city->where('hh_id',$v->area->id)->get()->all_to_array();
		$city = $city[0];
		if (isset($v->description)) {
			$obj['description'] = $v->description;
		}
		$obj['city'] = $city['name'];
		$exploded_time = explode('T',$v->created_at);
		$obj['creation_date'] = $exploded_time[0].' '.current(explode('+',$exploded_time[1]));
		$obj['employer'] = $v->employer->name;
		if ($v->salary) {
			$obj['salary_from'] = $v->salary->from;
			$obj['salary_to'] = $v->salary->to;
			$obj['salary_currency'] = $v->salary->currency;
		}
		return (object)$obj;
	}
	function get_vacancies() {
		$this->load->model('settings');
		$days_to_parse = $this->settings->parse_days;
		$vac_list = new Vacancy_list;
		for($page=0; $page<=49; $page++) {
			$results = json_decode($this->make_request('vacancies?per_page=40&page='.$page));
			foreach($results->items as $result) {
				$url = str_replace('https://api.hh.ru/','',$result->url);
				if ($vac_list->load_from_jobsite(json_decode($this->make_request($url)),'hh') && $vac_list->last_item()->check_if_unique_in_db()) {
					$vac_list->last_item()->to_db();
				}
			}
		}
		return $vac_list;
	}
}

?>