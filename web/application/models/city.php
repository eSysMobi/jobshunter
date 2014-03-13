<?php

class City extends DataMapper {
	var $model = 'city';
	var $table = 'cities';
	
	var $has_one = array();
	var $has_many = array();

	var $validation = array(
		'name' => array(
			'rules' => array('required', 'max_length' => 50, 'min_length' => 1),
			'label' => 'Name'
		),
		'hh_id' => array(
			'rules' => array('integer'),
			'label' => 'HH id'
		)
	);

	var $default_order_by = array('id' => 'desc');
 

    function __construct($id = NULL)
	{
		parent::__construct($id);
    }
	
	function get_city_by_name($name) {
		$this->name = $name;
		$c = new City;
		$c->where('name', $this->name)->get();
		$this->validate()->get();
		if (empty($this->id)) {
			return false;
		} else {
			return true;
		}
	}
	function get_cities_by_substr($str) {
		$c = new City;
		$cities = $c->like('name', $str)->get()->all_to_array();
		foreach($cities as &$city) {
			$city = (object)$city;
		}
		
		return $cities;
	}
	function get_parents_for_cities($cities) {
		$c = new City;
		$out = array();
		foreach($cities as $city) {
			if ($city->parent_id) {
				$parent = $c->where('id', $city->parent_id)->get()->all_to_array();
				$parent = (object)$parent[0];
				$out[] = $parent;
			}
		}
		return $out;
	}
	function get_all_parents_for_city($city_id,$string=false) {
		$c = new City;
		$city = $c->where('id', $city_id)->get();
		$out[] = $string?(string)$city_id:$city_id;
		while($city->parent_id!=0) {
			$city = $c->where('id', $city->parent_id)->get();
			$out[] = $string?(string)$city->id:$city->id;
		}
		return $out;
	}
	function post_model_init($from_cache = FALSE)
	{
	}
}