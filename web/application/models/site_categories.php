<?php

class Site_categories extends DataMapper {
	var $model = 'Site_categories';
	var $table = 'site_categories';
	
	var $has_one = array();
	var $has_many = array();

	var $validation = array(
		'name' => array(
			'name' => array('required', 'max_length' => 200, 'min_length' => 1),
			'label' => 'Name'
		),
		'parent_id' => array(
			'rules' => array('integer'),
			'label' => 'Parent id'
		)
	);

	var $default_order_by = array('id' => 'desc');
 

    function __construct($id = NULL)
	{
		parent::__construct($id);
    }
	
	function post_model_init($from_cache = FALSE)
	{
	}
}