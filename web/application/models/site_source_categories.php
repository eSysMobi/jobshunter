<?php

class site_source_categories extends DataMapper {
	var $model = 'site_source_categories';
	var $table = 'site_source_categories';
	
	var $has_one = array();
	var $has_many = array();

	var $validation = array(
		'Site_id' => array(
			'rules' => array('required'),
			'label' => 'Site_id'
		),
		'Source_id' => array(
			'rules' => array('required'),
			'label' => 'Source_id'
		)
	);

	var $default_order_by = array('Site_id' => 'desc');
 

    function __construct($id = NULL)
	{
		parent::__construct($id);
    }

	function post_model_init($from_cache = FALSE)
	{
	}
}

