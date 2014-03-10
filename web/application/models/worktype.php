<?php

class Worktype extends DataMapper {
	var $model = 'worktype';
	var $table = 'worktypes';
	
	var $has_one = array();
	var $has_many = array();

	var $validation = array(
		'name' => array(
			'rules' => array('required'),
			'label' => 'Name'
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