<?php

class Sourcecategories extends DataMapper {
	var $model = 'Sourcecategories';
	var $table = 'sourcecategories';
	
	var $has_one = array();
	var $has_many = array();

	var $validation = array(
		'site' => array(
			'rules' => array('required','valid_match' => array('hh', 'sj', 'rr')),
			'label' => 'Site'
		),
		'site_id' => array(
			'rules' => array('required'),
			'label' => 'SiteId'
		),
		'category_name' => array(
			'rules' => array('required'),
			'label' => 'Category name'
		),
		'parent_id' => array(
			'rules' => array(),
			'label' => 'Parent Id'
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

