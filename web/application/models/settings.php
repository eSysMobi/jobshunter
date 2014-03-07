<?php

class settings extends DataMapper {
	var $model = 'settings';
	var $table = 'settings';
	
	var $has_one = array();
	var $has_many = array();

	var $validation = array(
		'parse_days' => array(
			'rules' => array('required', 'integer'),
			'label' => 'Parse_days'
		)
	);

	var $default_order_by = array('id' => 'desc');
 

    function __construct($id = NULL)
	{
		parent::__construct($id);
		$this->where('id',1)->get();
	}
	
	function post_model_init($from_cache = FALSE)
	{
	}
}